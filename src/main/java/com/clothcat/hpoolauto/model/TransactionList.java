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

import com.clothcat.hpoolauto.JsonFileHelper;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A list of transactions we have seen and processed.
 *
 * @author Stephen Stafford <clothcat@gmail.com>
 */
public class TransactionList {

    private Set<String> txids;

    public TransactionList() {
        this(JsonFileHelper.readFromFile("transactions.json"));
    }

    public void saveTransactions(){
        JsonFileHelper.writeToFile(new JSONObject(this), "transactions.json");
    }
    
    public TransactionList(JSONObject jo) {
        txids = new TreeSet<>();
        try {
            if (jo.has("txids")) {
                JSONArray transactions = jo.getJSONArray("txids");
                for (int i = 0; i < transactions.length(); i++) {
                    txids.add(transactions.getString(i));
                }
            }
        } catch (JSONException ex) {
            Logger.getLogger(TransactionList.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * @return the txids
     */
    public Set<String> getTxids() {
        return txids;
    }

    /**
     * @param txids the txids to set
     */
    public void setTxids(Set<String> txids) {
        this.txids = txids;
    }
}
