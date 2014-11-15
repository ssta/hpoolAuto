/*
 * The MIT License
 *
 * Copyright 2014 Stephen Stafford.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.clothcat.hpoolauto;

import com.clothcat.hpoolauto.model.Investment;
import com.clothcat.hpoolauto.model.Model;
import com.clothcat.hpoolauto.model.Pool;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 *
 * @author hyp
 */
public class Main {

    static final String POOL_ADDRESS = "pBhLfm9o7L4gB7oscgaJusRmv4JUb1Xwnk";
    static final String POOL_ACCOUNT = "pool";
    RpcWorker rpcworker = new RpcWorker();

    public static void main(String[] args) {
        Main m = new Main();
        m.launch();
    }

    private void checkHyperstakedRunning() {
        try {
            String s = rpcworker.checkwallet();
            debug(s);
            JSONObject j = new JSONObject(s);
            boolean pass = j.getBoolean("wallet check passed");

            if (!pass) {
                System.out.println("Problem!");
            }
        } catch (JSONException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void launch() {
        // currently all we do is go into the main loop
        mainLoop();
    }

    private void mainLoop() {
        while (true) {
            // make sure hyperstaked is running
            checkHyperstakedRunning();
            // get and process any new transactions
            processNewTx();
            // sleep for 10 seconds    
            try {
                Thread.sleep(10000);
            } catch (InterruptedException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void debug(String s) {
        System.out.println(String.valueOf(System.currentTimeMillis()) + "::" + s);
    }

    private void processNewTx() {
        try {
            String s = rpcworker.getNextTransactions(POOL_ACCOUNT);
            //System.out.println(s);
            //System.out.println(s);
            JSONArray ja = new JSONArray(s);
            for (int i = 0; i < ja.length(); i++) {
                JSONObject jo = ja.getJSONObject(i);
                //System.out.println(jo.toString());
                if (jo.getString("category").equals("receive")) {
                    processReceipt(jo);
                }
            }
        } catch (JSONException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    private void processReceipt(JSONObject j) {
        Model model = new Model();
        try {
            if (JsonDbHelper.isNewTransaction(j)) {
                System.out.println("new transaction! " + j.getString("txid"));
                String curPool = model.getCurrPoolName();
                long tgt_min = model.getMinFill();
                long tgt_max = model.getMaxFill();
                System.out.println("Pool Name: " + curPool + "\ttgt_min: " + tgt_min + "\ttgt_max: " + tgt_max);
                // get the transaction for this receipt
                String txid = j.getString("txid");
                String s = rpcworker.getTransaction(txid);

                JSONTokener jt = new JSONTokener(s);
                JSONObject tx = new JSONObjectDuplicates(jt);
                JSONArray vout = tx.getJSONArray("vout");

                String sendingAddress = vout.getJSONObject(0).getJSONObject("scriptPubKey").getJSONArray("addresses").getString(0);
                String receivingAddress = vout.getJSONObject(1).getJSONObject("scriptPubKey").getJSONArray("addresses").getString(0);
                String amountStr = vout.getJSONObject(1).getString("value");
                double d = Double.valueOf(amountStr);
                d *= Constants.uH;
                long amount = (long) d;

                Pool p = model.getPool(model.getCurrPoolName());
                long minSpace = model.getMinFill() - p.calculateFillAmount();
                long maxSpace = model.getMaxFill() - p.calculateFillAmount();

                if (amount < minSpace) {
                    // investment fits in pool but does not fill it
                    Investment inv = new Investment();
                    inv.setAmount(amount);
                    inv.setDatestamp(new java.util.Date().getTime() / 1000);
                    inv.setFromAddress(sendingAddress);
                    p.getInvestments().add(inv);
                } else if (amount < maxSpace) {
                    // investment fits in pool and fills it
                    Investment inv = new Investment();
                    inv.setAmount(amount);
                    inv.setDatestamp(new java.util.Date().getTime() / 1000);
                    inv.setFromAddress(sendingAddress);
                    p.getInvestments().add(inv);
                    model.moveToNextPool();
                } else {
                    // investment overflows pool, so fill it and then rollover
                    // what's left as the first investment in the next pool.

                    // we want to take a random amount of investment that
                    // lets the pool size be between min and max, but which 
                    // still leaves the investor enough for the minimum 
                    // investment for the next pool.
                    long maxInvAmount = amount - model.getMinInvestment();
                    long minInvAmount = minSpace;

                    long randomAmount = getRandomLongInRange(minInvAmount, maxInvAmount);
                    long remainingAmount = amount - randomAmount;

                    Investment inv = new Investment();
                    inv.setAmount(randomAmount);
                    inv.setDatestamp(new java.util.Date().getTime() / 1000);
                    inv.setFromAddress(sendingAddress);
                    p.getInvestments().add(inv);
                    model.moveToNextPool();

                    Pool p2 = model.getPool(model.getCurrPoolName());
                    Investment inv2 = new Investment();
                    inv2.setAmount(remainingAmount);
                    inv2.setDatestamp(new java.util.Date().getTime() / 1000);
                    inv2.setFromAddress(sendingAddress);
                    p2.getInvestments().add(inv2);
                }

                model.setTransactionDone(txid);
            }
        } catch (JSONException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private long getRandomLongInRange(long from, long to) {
        long range = to - from + 1;
        long r = new Random().nextLong();
        r %= range;
        // we only produce positive values
        r = Math.abs(r);
        r += from;
        return r;
    }
}
