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

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Encapsulates an investment in the pool.
 *
 */
public class Investment {

    private String fromAddress; /* The address the investment came from */

    private long amount; /* in μHyp */

    private long datestamp; /* The timestamp of the transaction */

    private Investment() {
        throw new UnsupportedOperationException("Use Investment(JsonObject) instead");
    }

    /**
     * Construct a new Investment instance by deserializing the JSON given
     *
     * @param jo
     * @throws org.json.JSONException
     */
    public Investment(JSONObject jo) throws JSONException {
        this.amount = jo.getLong("amount");
        this.fromAddress = jo.getString("fromAddress");
        this.datestamp = jo.getLong("datestamp");
    }

    /**
     * @return the fromAddress
     */
    public String getFromAddress() {
        return fromAddress;
    }

    /**
     * @param fromAddress the fromAddress to set
     */
    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    /**
     * @return the amount
     */
    public long getAmount() {
        return amount;
    }

    /**
     * @param amount the amount to set
     */
    public void setAmount(long amount) {
        this.amount = amount;
    }

    /**
     * @return the datestamp
     */
    public long getDatestamp() {
        return datestamp;
    }

    /**
     * @param datestamp the datestamp to set
     */
    public void setDatestamp(long datestamp) {
        this.datestamp = datestamp;
    }

    public JSONObject toJson() {
        JSONObject jo = new JSONObject(this);
        return jo;
    }

    public static Investment fromJson(JSONObject jo) throws JSONException {
        return new Investment(jo);
    }
}
