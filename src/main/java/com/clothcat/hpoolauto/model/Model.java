/*
 * The MIT License
 *
 * Copyright 2014 Stephen Stafford <clothcat@gmail.com>.
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
package com.clothcat.hpoolauto.model;

import com.clothcat.hpoolauto.Constants;
import com.clothcat.hpoolauto.JSONObjectDuplicates;
import com.clothcat.hpoolauto.JsonFileHelper;
import com.clothcat.hpoolauto.Main;
import com.clothcat.hpoolauto.RpcWorker;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * The Model consists of several pools in various stages.
 *
 * It's a singleton which is ugly, and I'll probably change that at some point.
 *
 * @author Stephen Stafford <clothcat@gmail.com>
 */
public class Model {

    RpcWorker rpcWorker = new RpcWorker();
    List<Pool> pools;
    private String currPoolName;
    private long minInvestment;
    private long minFill;
    private long maxFill;
    private String poolAddress;
    TransactionList transactions;

    public Model() {
        this(JsonFileHelper.readFromFile("model.json"));
    }

    public Model(JSONObject jo) {
        try {
            pools = new ArrayList<>();
            if (jo.has("pools")) {
                JSONArray parray = jo.getJSONArray("pools");
                for (int i = 0; i < parray.length(); i++) {
                    Pool p = new Pool(JsonFileHelper.readFromFile(parray.getString(i) + ".json"));
                    pools.add(p);
                }
            }

            if (jo.has("minInvestment")) {
                minInvestment = jo.getLong("minInvestment");
            } else {
                // default to 50HYP
                minInvestment = 50 * Constants.uH;
            }

            if (jo.has("minFill")) {
                minFill = jo.getLong("minFill");
            } else {
                // default to 3000 HYP
                minFill = 3000 * Constants.uH;
            }

            if (jo.has("maxFill")) {
                maxFill = jo.getLong("maxFill");
            } else {
                // default to 4000 HYP
                maxFill = 4000 * Constants.uH;
            }
            if (jo.has("poolAddress")) {
                poolAddress = jo.getString("poolAddress");
            } else {
                poolAddress = rpcWorker.getPoolAddress("pool");
            }
            transactions = new TransactionList();
        } catch (JSONException ex) {
            Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    boolean isNewTransaction(JSONObject j) {
        boolean result = false;
        try {
            String txid = j.getString("txid");
            result = !transactions.getTxids().contains(txid);
        } catch (JSONException ex) {
            Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    private void processReceipt(JSONObject j) {
        try {
            if (isNewTransaction(j)) {
                System.out.println("new transaction! " + j.getString("txid"));
                // get the transaction for this receipt
                String txid = j.getString("txid");
                String s = rpcWorker.getTransaction(txid);

                JSONTokener jt = new JSONTokener(s);
                JSONObject tx = new JSONObjectDuplicates(jt);
                JSONArray vout = tx.getJSONArray("vout");

                String sendingAddress = vout.getJSONObject(0).getJSONObject("scriptPubKey").getJSONArray("addresses").getString(0);
                String receivingAddress = vout.getJSONObject(1).getJSONObject("scriptPubKey").getJSONArray("addresses").getString(0);
                String amountStr = vout.getJSONObject(1).getString("value");
                double d = Double.valueOf(amountStr);
                d *= Constants.uH;
                long amount = (long) d;

                Pool p = getPool(getCurrPoolName());
                long minSpace = getMinFill() - p.calculateFillAmount();
                long maxSpace = getMaxFill() - p.calculateFillAmount();

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
                    moveToNextPool();
                } else {
                    // investment overflows pool, so fill it and then rollover
                    // what's left as the first investment in the next pool.

                    // we want to take a random amount of investment that
                    // lets the pool size be between min and max, but which 
                    // still leaves the investor enough for the minimum 
                    // investment for the next pool.
                    long maxInvAmount = amount - getMinInvestment();
                    long minInvAmount = minSpace;

                    long randomAmount = getRandomLongInRange(minInvAmount, maxInvAmount);
                    long remainingAmount = amount - randomAmount;

                    Investment inv = new Investment();
                    inv.setAmount(randomAmount);
                    inv.setDatestamp(new java.util.Date().getTime() / 1000);
                    inv.setFromAddress(sendingAddress);
                    p.getInvestments().add(inv);
                    moveToNextPool();

                    Pool p2 = getPool(getCurrPoolName());
                    Investment inv2 = new Investment();
                    inv2.setAmount(remainingAmount);
                    inv2.setDatestamp(new java.util.Date().getTime() / 1000);
                    inv2.setFromAddress(sendingAddress);
                    p2.getInvestments().add(inv2);
                }

                setTransactionDone(txid);
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

    public void processNewTx() {
        try {
            String s = rpcWorker.getNextTransactions("pool");
            JSONArray ja = new JSONArray(s);
            for (int i = 0; i < ja.length(); i++) {
                JSONObject jo = ja.getJSONObject(i);
                //System.out.println(jo.toString());
                if (jo.getString("category").equals("receive")) {
                    // make sure the transaction is old enough (10 minutes)
                    // this time delay is to allow enough time for the transaction
                    // to be verified
                    // This mechanism may change in the future… 
                    long now = System.currentTimeMillis() / 1000; // current time in seconds since the epoch
                    long txtime = jo.getLong("timereceived");
                    long TENMINUTES = 600; // in seconds
                    if (now > (txtime + TENMINUTES)) {
                        processReceipt(jo);
                    }
                }
            }
        } catch (JSONException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     *
     * @return The number of pools we've ever filled.
     */
    public int getNumFilledPools() {
        int num = 0;
        for (Pool p : pools) {
            PoolStatus status = p.getStatus();
            if (status == PoolStatus.MATURING || status == PoolStatus.STAKED || status == PoolStatus.STAKING) {
                num++;
            }
        }
        return num;
    }

    /**
     * @return How many pools we currently have that are maturing (full, but not
     * yet old enough to be eligible for stake)
     */
    public int getNumMaturingPools() {
        int num = 0;
        for (Pool p : pools) {
            PoolStatus status = p.getStatus();
            if (status == PoolStatus.MATURING) {
                num++;
            }
        }
        return num;
    }

    /**
     * @return How many pools we currently have that are old enough to stake but
     * which haven't yet staked.
     */
    public int getNumStakingPools() {
        int num = 0;
        for (Pool p : pools) {
            PoolStatus status = p.getStatus();
            if (status == PoolStatus.STAKING) {
                num++;
            }
        }
        return num;
    }

    /**
     * @return How many pools have staked and been paid out.
     */
    public int getNumPoolsPaid() {
        int num = 0;
        for (Pool p : pools) {
            PoolStatus status = p.getStatus();
            if (status == PoolStatus.STAKED) {
                num++;
            }
        }
        return num;
    }

    /**
     * @return How much profit we've made for investors thus far (in uHyp)
     */
    public long getTotalProfit() {
        long total = 0;
        for (Pool p : pools) {
            total += p.getProfit();
        }
        return total;
    }

    /**
     * @return the currPoolName
     */
    public String getCurrPoolName() {
        if (currPoolName == null) {
            currPoolName = "pool1";
        }
        return currPoolName;
    }

    /**
     * @param currPoolName the currPoolName to set
     */
    public void setCurrPoolName(String currPoolName) {
        this.currPoolName = currPoolName;
    }

    public Pool getPool(String poolName) {
        for (Pool p : pools) {
            if (p.getPoolName().equals(poolName)) {
                return p;
            }
        }

        // if we got here that means there isn't a current pool generated yet :(
        Pool p = new Pool();
        p.setPoolName(poolName);
        p.setPoolAge(0);
        p.setProfit(0);
        p.setStatus(PoolStatus.FILLING);
        pools.add(p);
        return p;
    }

    public void updateAndSave() {
        // update webpages
        HtmlGenerator.generateAll(this);
        // save everything
        saveJson();
    }

    public void saveJson() {
        // write self to JSON
        JsonFileHelper.writeToFile(toJson(), "model.json");
        // write all pools to JSON
        for (Pool p : pools) {
            JSONObject j = p.toJson();
            JsonFileHelper.writeToFile(j, p.getPoolName() + ".json");
        }
        transactions.saveTransactions();
    }

    public JSONObject toJson() {
        JSONObject jo = new JSONObject();
        try {
            jo.put("currPoolName", getCurrPoolName());
            jo.put("poolAddress", getPoolAddress());
            jo.put("maxFill", getMaxFill());
            jo.put("minFill", getMinFill());
            jo.put("minInvestment", getMinInvestment());
            JSONArray poolsArray = new JSONArray();
            for (Pool p : pools) {
                poolsArray.put(p.getPoolName());
            }
            jo.put("pools", poolsArray);
        } catch (JSONException ex) {
            Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
        }
        return jo;
    }

    /**
     * @return the minInvestment
     */
    public long getMinInvestment() {
        return minInvestment;
    }

    /**
     * @param minInvestment the minInvestment to set
     */
    public void setMinInvestment(long minInvestment) {
        this.minInvestment = minInvestment;
    }

    /**
     * @return the minFill
     */
    public long getMinFill() {
        return minFill;
    }

    /**
     * @param min_fill the minFill to set
     */
    public void setMinFill(long min_fill) {
        this.minFill = min_fill;
    }

    /**
     * @return the maxFill
     */
    public long getMaxFill() {
        return maxFill;
    }

    /**
     * @param max_fill the maxFill to set
     */
    public void setMaxFill(long max_fill) {
        this.maxFill = max_fill;
    }

    public void moveToNextPool() {
        Pool oldPool = getPool(getCurrPoolName());

        // generate new pool name
        // pool names are "poolN" where N is an incrementing number
        int oldNumber = Integer.parseInt(getCurrPoolName().substring(4));
        int newNumber = oldNumber + 1;
        String newPoolName = "pool" + newNumber;

        // set old pool status to maturing
        oldPool.setStatus(PoolStatus.MATURING);

        // address for the current pool we're creating
        String oldAddress = oldPool.getPoolAddress();
        if (oldAddress == null || "".equals(oldAddress)) {
            oldAddress = rpcWorker.getPoolAddress(oldPool.getPoolName());
            oldPool.setPoolAddress(oldAddress);
        }

        System.out.println("amount==" + oldPool.calculateFillAmount());
        // xfer into the now full pool
        rpcWorker.xferCoins("pool", oldAddress, oldPool.calculateFillAmount() - Constants.XFER_FEE);

        // set currentpool to new pool
        setCurrPoolName(newPoolName);

        // update and save all
        updateAndSave();
    }

    public void setTransactionDone(String txid) {
        transactions.getTxids().add(txid);
        transactions.saveTransactions();
    }

    /**
     * @return the poolAddress
     */
    public String getPoolAddress() {
        return poolAddress;
    }

    /**
     * @param poolAddress the poolAddress to set
     */
    public void setPoolAddress(String poolAddress) {
        this.poolAddress = poolAddress;
    }
}
