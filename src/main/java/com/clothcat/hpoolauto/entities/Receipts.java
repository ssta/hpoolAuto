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
package com.clothcat.hpoolauto.entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author hyp
 */
@Entity
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Receipts.findAll", query = "SELECT r FROM Receipts r"),
    @NamedQuery(name = "Receipts.findByRowid", query = "SELECT r FROM Receipts r WHERE r.rowid = :rowid"),
    @NamedQuery(name = "Receipts.findByFromAddress", query = "SELECT r FROM Receipts r WHERE r.fromAddress = :fromAddress"),
    @NamedQuery(name = "Receipts.findByAmount", query = "SELECT r FROM Receipts r WHERE r.amount = :amount"),
    @NamedQuery(name = "Receipts.findByForPool", query = "SELECT r FROM Receipts r WHERE r.forPool = :forPool"),
    @NamedQuery(name = "Receipts.findByTransactionTime", query = "SELECT r FROM Receipts r WHERE r.transactionTime = :transactionTime")})
public class Receipts implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(nullable = false)
    private int rowid;
    @Basic(optional = false)
    @Column(name = "FROM_ADDRESS", nullable = false, length = 50)
    private String fromAddress;
    private Integer amount;
    @Column(name = "FOR_POOL", length = 10)
    private String forPool;
    @Column(name = "TRANSACTION_TIME")
    private Integer transactionTime;

    public Receipts() {
    }

    public Receipts(int rowid, String fromAddress) {
        this.rowid = rowid;
        this.fromAddress = fromAddress;
    }

    public int getRowid() {
        return rowid;
    }

    public void setRowid(int rowid) {
        this.rowid = rowid;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public String getForPool() {
        return forPool;
    }

    public void setForPool(String forPool) {
        this.forPool = forPool;
    }

    public Integer getTransactionTime() {
        return transactionTime;
    }

    public void setTransactionTime(Integer transactionTime) {
        this.transactionTime = transactionTime;
    }

    @Override
    public int hashCode() {
        return rowid;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Receipts)) {
            return false;
        }
        Receipts other = (Receipts) object;
        return (this.rowid == other.rowid);
    }
}
