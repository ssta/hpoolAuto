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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class does all of the rpc necessary to the wallet.
 *
 */
public class RpcWorker {

    String[] cmdline = new String[]{"/home/hyp/.Hyperpool/wallet/hyperstaked",
        "-rpcport=20000",
        "-conf=/home/hyp/.Hyperpool/wallet/HyperStake.conf",
        "-datadir=/home/hyp/.Hyperpool/wallet/"
    };

    // TODO -- for testing only! Remove me before release!
    public static void main(String[] args) {
        RpcWorker r = new RpcWorker();

    }

    /**
     * Gets the address associated with the accountname passed. If no account of
     * that name exists yet, then one will be created and a new address
     * generated and associated with it.
     */
    public String getPoolAddress(String poolName) {
        List<String> cmd = new ArrayList<>();
        cmd.addAll(Arrays.asList(cmdline));
        cmd.add("getaccountaddress");
        cmd.add(poolName);
        String s = runCommand(cmd);
        System.out.println("Got address: "+s+" for pool address");
        return s;
    }

    /**
     * Transfer amount coins from fromAccount to toAddress.
     *
     * Note amount is in uHyp and needs to be translated to Hyp.
     */
    public String xferCoins(String fromAccount, String toAddress, long amount) {
        List<String> cmd = new ArrayList<>();
        cmd.addAll(Arrays.asList(cmdline));
        cmd.add("sendfrom");
        cmd.add(fromAccount);
        cmd.add(toAddress);
        double a = amount / 1000000.0;
        String as = Double.toString(a);
        cmd.add(as);
        String s = runCommand(cmd);
        return s;
    }

    /**
     * Get a new address and associate it with poolName
     */
    public String getNewAddress(String poolName) {
        List<String> cmd = new ArrayList<>();
        cmd.addAll(Arrays.asList(cmdline));
        cmd.add("getnewaddress");
        cmd.add(poolName);

        String s = runCommand(cmd);
        return s;
    }

    /**
     * Run the rpc command checkwallet.
     *
     * @return true if the wallet check passed
     */
    public String checkwallet() {
        List<String> cmd = new ArrayList<>();
        cmd.addAll(Arrays.asList(cmdline));
        cmd.add("checkwallet");

        return runCommand(cmd);
    }

    public String getTransaction(String txid) {
        List<String> cmd = new ArrayList<>();
        cmd.addAll(Arrays.asList(cmdline));
        cmd.add("gettransaction");
        cmd.add(txid);

        return runCommand(cmd);
    }

    /**
     * Grab up to the last 20 transactions
     */
    public String getNextTransactions(String account) {
        List<String> cmd = new ArrayList<>();
        cmd.addAll(Arrays.asList(cmdline));
        cmd.add("listtransactions");
        cmd.add(account);
        cmd.add("20");
        cmd.add(String.valueOf(0));

        return runCommand(cmd);
    }

    public String getPosDifficulty() {
        List<String> cmd = new ArrayList<>();
        cmd.addAll(Arrays.asList(cmdline));
        cmd.add("getdifficulty");

        return runCommand(cmd);
    }

    private String runCommand(List<String> command) {
        String s = "";

        try {
            ProcessBuilder ps = new ProcessBuilder(command);
            ps.redirectErrorStream(true);
            Process pr = ps.start();
            BufferedReader in = new BufferedReader(new InputStreamReader(pr.getInputStream()));
            s = "";
            String line;
            while ((line = in.readLine()) != null) {
                s += line;
            }
            pr.waitFor(5, TimeUnit.SECONDS);
            s = s.trim();
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(RpcWorker.class.getName()).log(Level.SEVERE, null, ex);
        }
        return s;
    }
}
