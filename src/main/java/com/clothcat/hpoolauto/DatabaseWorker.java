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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class to abstract sqlite connectivity
 *
 */
public class DatabaseWorker {

    static Connection connection;

    static {
        try {
            Class.forName("org.sqlite.JDBC");
            // create tables if they don't already exist
            String address_table_sql = "CREATE TABLE IF NOT EXISTS ADDRESSES "
                    + "( ACCOUNT VARCHAR(10) PRIMARY KEY NOT NULL,"
                    + " ADDRESS VARCHAR(40) )";
            String keyvalues_table_sql = "CREATE TABLE IF NOT EXISTS KEYVALUES "
                    + "( KEY VARCHAR(10) PRIMARY KEY NOT NULL,"
                    + " VALUE VARCHAR(50) )";
            try (Statement st = getConnection().createStatement()) {
                st.executeUpdate(address_table_sql);
                st.executeUpdate(keyvalues_table_sql);
            }
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(DatabaseWorker.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static Connection getConnection() {
        if (connection == null) {
            try {
                connection = DriverManager.getConnection("jdbc:sqlite:/home/hyp/.hyperpool/hyperpool.db");
            } catch (SQLException ex) {
                Logger.getLogger(DatabaseWorker.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return connection;
    }

    public static int getLastTransactionNumber() {
        String result = null;
        String sql = "SELECT value FROM KEYVALUES "
                + "WHERE key='lasttx'";

        try {
            try (Statement st = getConnection().createStatement()) {
                ResultSet rs = st.executeQuery(sql);
                if (rs.next()) {
                    result = rs.getString("value");
                }
                if (result == null) {
                    result = "0";
                    String insertSql = "INSERT INTO KEYVALUES values('lasttx', '0')";
                    st.executeUpdate(insertSql);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseWorker.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Integer.valueOf(result);
    }
}
