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
import com.clothcat.hpoolauto.JsonFileHelper;
import com.clothcat.hpoolauto.RpcWorker;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

    public Model() {
        this(JsonFileHelper.readFromFile("model.json"));
    }

    public Model(JSONObject jo) {
        try {
            pools = new ArrayList<>();
            if (jo.has("pools")) {
                JSONArray parray = jo.getJSONArray("pools");
                for (int i = 0; i < parray.length(); i++) {
                    Pool p = new Pool(JsonFileHelper.readFromFile(parray.getString(i)));
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

            if (jo.has("max_fill")) {
                maxFill = jo.getLong("maxFill");
            } else {
                // default to 4000 HYP
                maxFill = 4000 * Constants.uH;
            }

        } catch (JSONException ex) {
            Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     *
     * @return The number of pools we've ever filled.
     */
    public int getNumFilledPools() {
        return 10;
    }

    /**
     * @return How many pools we currently have that are maturing (full, but not
     * yet old enough to be eligible for stake)
     */
    public int getNumMaturingPools() {
        return 5;
    }

    /**
     * @return How many pools we currently have that are old enough to stake but
     * which haven't yet staked.
     */
    public int getNumStakingPools() {
        return 2;
    }

    /**
     * @return How many pools have staked and been paid out.
     */
    public int getNumPoolsPaid() {
        return 3;
    }

    /**
     * @return How much profit we've made for investors thus far (in uHyp)
     */
    public long getTotalProfit() {
        return 1234123400L;
    }

    /**
     * @return the currPoolName
     */
    public String getCurrPoolName() {
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
        return null;
    }

    public void saveJson() {
        JsonFileHelper.writeToFile(toJson(), "model.json");
    }

    public JSONObject toJson() {
        JSONObject jo = new JSONObject();
        try {
            jo.put("currPoolName", getCurrPoolName());
            jo.put("maxFill", getMaxFill());
            jo.put("minFill", getMinFill());
            jo.put("minInvestment", getMinInvestment());
            JSONArray poolsArray = new JSONArray();
            for (Pool p : pools) {
                poolsArray.put(p.getPoolName());
                // save the pool json while we're at it
                JsonFileHelper.writeToFile(p.toJson(), p.getPoolName() + ".json");
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
        // pool names are "poolN" where N is an incremebting number
        int oldNumber = Integer.parseInt(getCurrPoolName().substring(4));
        int newNumber = oldNumber + 1;
        String newPoolName = "pool" + newNumber;

        // set old pool status to maturing
        oldPool.setStatus(PoolStatus.MATURING);

        // generate a new address for the now full pool
        String newAddress = rpcWorker.getNewAddress(newPoolName);
        oldPool.setPoolAddress(newAddress);
        
        // xfer into the now full pool
        rpcWorker.xferCoins("pool", newAddress, oldPool.calculateFillAmount());
        // update webpages
        HtmlGenerator.generateAll();
        // set currentpool to new pool
        setCurrPoolName(newPoolName);
        // save all json
        // TODO
        TODO;
    }

    public void setTransactionDone(String txid) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
