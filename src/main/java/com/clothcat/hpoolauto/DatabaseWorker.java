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

import com.clothcat.hpoolauto.entities.Addresses;
import com.clothcat.hpoolauto.entities.Keyvalues;
import com.clothcat.hpoolauto.entities.Transactions;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.Query;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Utility class to abstract sqlite connectivity
 *
 */
public class DatabaseWorker {

    EntityManager entityManager;

    public DatabaseWorker() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hyp");
        entityManager = emf.createEntityManager();
    }

    /**
     * Return the address of the pool. This is the address people send Hyp to,
     * NOT the address of any of the staking pools.
     *
     * @return The pool address.
     */
    public String getPoolAddress() {
        String result = "";
        Query q = entityManager.createNamedQuery("Addresses.findByAccount");
        q.setParameter("account", "pool");
        Addresses row;
        row = (Addresses) q.getSingleResult();
        result = row.getAddress();
        return result;
    }

    /**
     * Grab the txid from the given JSONObject and mark it as having been
     * processed.
     */
    public void markTransactionDone(JSONObject jo) throws JSONException {
        String txid = jo.getString("txid");
        System.out.println("Trying to mark as done: " + txid);
        Transactions t = new Transactions();
        t.setTxid(txid);
        entityManager.getTransaction().begin();
        entityManager.persist(t);
        entityManager.getTransaction().commit();
    }

    /**
     * Returns the name of the pool we're currently trying to fill
     *
     * @return The pool name (eg: "pool1")
     */
    public String getCurrPoolName() {
        Query q = entityManager.createNamedQuery("Keyvalues.findByKey");
        q.setParameter("key", "cur_pool");
        Keyvalues row = (Keyvalues) q.getSingleResult();
        return row.getValue();
    }

    /**
     * Returns the minimum amount we need to add to fill the current pool
     *
     * @return The minimum amount needed in uHYP
     */
    public long getCurrPoolMinToFill() {
        Query q = entityManager.createNamedQuery("Keyvalues.findByKey");
        q.setParameter("key", "tgt_min");
        Keyvalues row = (Keyvalues) q.getSingleResult();
        return Long.valueOf(row.getValue());
    }

    /**
     * Returns the maximum amount we need to add to fill the current pool before
     * it overflows.
     *
     * @return The maximum amount allowed in uHYP
     */
    public long getCurrPoolMaxToFill() {
        Query q = entityManager.createNamedQuery("Keyvalues.findByKey");
        q.setParameter("key", "tgt_max");
        Keyvalues row = (Keyvalues) q.getSingleResult();
        return Long.valueOf(row.getValue());
    }

    /**
     * Update the current idea of how full the pool is.
     *
     * @param amountToAdd How much to add to the current pool fill size.
     */
    public void updateCurrPoolFill(int amountToAdd) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Roll us over to the next pool. This assumes that the current pool is full
     * and has been marked as staking.
     */
    public void nextPool() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Pulls the txid from the given JSONObject and returns true only if that
     * txid is not marked as already processed.
     */
    public boolean isNewTransaction(JSONObject j) throws JSONException {
        String txid = j.getString("txid");
        Query q = entityManager.createNamedQuery("Transactions.findByTxid");
        q.setParameter("txid", txid);
        Transactions row;
        try {
            row = (Transactions) q.getSingleResult();
            if (row == null) {
                return true;
            }
        } catch (NoResultException nre) {
            return true;
        }

        return false;
    }
}
