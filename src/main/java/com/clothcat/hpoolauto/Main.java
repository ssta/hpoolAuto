/*
 * The MIT License
 *
 * Copyright 2014 hyp.
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
            DatabaseWorker dw = new DatabaseWorker();
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
        DatabaseWorker dw = new DatabaseWorker();

        try {
            if (dw.isNewTransaction(j)) {
                System.out.println("new transaction! "+j.getString("txid"));
                String curPool = dw.getCurrPoolName();
                System.out.println("Pool Name: " + curPool);
                // get the transaction for this receipt
                String txid = j.getString("txid");
                String s = rpcworker.getTransaction(txid);

                JSONTokener jt = new JSONTokener(s);
                JSONObject tx = new JSONObjectDuplicates(jt);
                JSONArray vout = tx.getJSONArray("vout");

                String sendingAddress = vout.getJSONObject(0).getJSONObject("scriptPubKey").getJSONArray("addresses").getString(0);
                String receivingAddress = vout.getJSONObject(1).getJSONObject("scriptPubKey").getJSONArray("addresses").getString(0);
                String amount = vout.getJSONObject(1).getString("value");
                dw.markTransactionDone(j);
            }
        } catch (JSONException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
