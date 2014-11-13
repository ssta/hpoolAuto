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
package com.clothcat.hpoolauto.model;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Encapsulates a Pool of investments
 *
 */
public class Pool {

    private List<Investment> investments;
    private String poolName;
    private PoolStatus status;
    private String poolAddress;
    private long poolAge;
    private long startTimestamp; /* The timestamp that the pool started maturing */


    private Pool() {
        //    throw new UnsupportedOperationException("Use Pool(JsonObject) instead");
    }

    public Pool(JSONObject jo) throws JSONException {
        investments = new ArrayList<>();
        JSONArray ja = jo.getJSONArray("investments");
        for (int i = 0; i < ja.length(); i++) {
            Investment inv = new Investment(ja.getJSONObject(i));
            investments.add(inv);
        }
        poolName = jo.getString("poolName");
        status = PoolStatus.valueOf(jo.getString("status"));
        poolAddress = jo.getString("poolAddress");
        startTimestamp = jo.getLong("startTimestamp");
        poolAge = jo.getLong("poolAge");
    }

    /**
     * @return the investments
     */
    public List<Investment> getInvestments() {
        return investments;
    }

    /**
     * @param investments the investments to set
     */
    public void setInvestments(List<Investment> investments) {
        this.investments = investments;
    }

    /**
     * @return the poolName
     */
    public String getPoolName() {
        return poolName;
    }

    /**
     * @param poolName the poolName to set
     */
    public void setPoolName(String poolName) {
        this.poolName = poolName;
    }

    /**
     * @return the status
     */
    public PoolStatus getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(PoolStatus status) {
        this.status = status;
    }

    public JSONObject toJson() {
        JSONObject jo = new JSONObject(this);
        try {
            // for reasons I don't understand, the json library does not serialise
            // enum values properly, so we set it by hand instead.
            jo.put("status", status.toString());
        } catch (JSONException ex) {
            Logger.getLogger(Pool.class.getName()).log(Level.SEVERE, null, ex);
        }
        return jo;
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

    /**
     * @return the startTimestamp
     */
    public long getStartTimestamp() {
        return startTimestamp;
    }

    /**
     * @param startTimestamp the startTimestamp to set
     */
    public void setStartTimestamp(long startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    /**
     * Returns the age of this pool, this is the time between now and when it
     * started maturing.
     *
     * If it has matured then the age is fixed and will not change.
     *
     * @return
     */
    public long getPoolAge() {
        if (status == PoolStatus.MATURING || status == PoolStatus.STAKING) {
            long newAge = System.currentTimeMillis() - startTimestamp;
            poolAge = newAge;
            return newAge;
        }
        return poolAge;
    }

    /**
     * @param poolAge the poolAge to set
     */
    public void setPoolAge(long poolAge) {
        this.poolAge = poolAge;
    }

    /**
     * return the amount of uHyp in this pool
     */
    public long calculateFillAmount() {
        long sum = 0;
        for (Investment inv : investments) {
            sum += inv.getAmount();
        }
        return sum;
    }

    /**
     * 7.5 / 365 * DAYS * BLOCKSIZE
     *
     * Return the amount of uHyp the pool would earn if it staked now
     */
    public long calculatePotentialStake() {
        long MAXSTAKE = (1000 * 1000000);
        long blocksize = calculateFillAmount();
        long days = getPoolAge() / 1000 / 3600 / 24;
        long potStake = (long) ((7.5 / 365) * days * blocksize);
        if (potStake > MAXSTAKE) {
            potStake = MAXSTAKE;
        }

        return potStake;
    }
}
