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
import com.clothcat.hpoolauto.RpcWorker;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.tidy.Tidy;

/**
 * Generates static HTML pages displaying the current state of the pools. The
 * HTML is placed in
 * <pre>${user.home}/.Hyperpool/html</pre>. This uses templates from
 * <pre>${user.home}/.Hyperpool/templates</pre>
 *
 * @author Stephen Stafford <clothcat@gmail.com>
 */
public class HtmlGenerator {

    //<editor-fold defaultstate="collapsed" desc="MASTER_HTML">
    private static final String MASTER_HTML_TEMPLATE = "<html>\n"
            + "    <head>\n"
            + "        <title>Hyperpool</title>\n"
            + "        <meta charset=\"UTF-8\">\n"
            + "        <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n"
            + "        <!--CSS-->\n"
            + "    </head>\n"
            + "    <body>\n"
            + "        <div id=\"hat_header\"><p>This page was automatically generated the by the Hyperpool Automation Tool (HAT)</p>\n"
            + "            <p>Generated at: <!--CREATION_DATE--></p></div>\n"
            + "\n"
            + "        <div id=\"current_status\">\n"
            + "            <!--NUM_FILLED_POOLS--> pools filled.<br/>\n"
            + "            <!--NUM_POOLS_MATURING--> pools currently maturing.<br/>\n"
            + "            <!--NUM_POOLS_STAKING--> pools currently staking.<br/>\n"
            + "            <!--NUM_POOLS_PAID--> pools staked and paid out.</br>\n"
            + "            <!--TOTAL_PROFIT_AMOUNT--> total profit so far.</div>\n"
            + "\n"
            + "<div id=\"network_stats\">\n"
            + "Current difficulty: <!--CURRENT_DIFFICULTY--><br/>\n"
            + "</div>\n"
            + "        <div id=\"master_pool_table\">\n"
            + "            <!--MASTER_POOL_TABLE-->\n"
            + "        </div>\n"
            + "\n"
            + "<h1>Pool Address: <!--POOL_ADDRESS--></h1>\n"
            + "    </body>\n"
            + "</html>\n"
            + "";
//</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="POOL_HTML">
    private static final String POOL_HTML_TEMPLATE = "<html>\n"
            + "    <head>\n"
            + "        <title>Hyperpool</title>\n"
            + "        <meta charset=\"UTF-8\">\n"
            + "        <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n"
            + "        <!--CSS-->\n"
            + "    </head>\n"
            + "    <body>\n"
            + "        <div id=\"hat_header\"><p>This page was automatically generated the by the Hyperpool Automation Tool (HAT)</p>\n"
            + "            <p>Generated at: <!--CREATION_DATE--></p></div>\n"
            + "\n"
            + "<h1 id=\"pool_heading\"><!--POOL_NAME--></h1>\n"
            + "<div id=\"pool_status\">\n"
            + "Status: <!--POOL_STATUS--><br/>\n"
            + "Pool filled: <!--POOL_FILL_DATE--><br/>\n"
            + "Pool age: <!--POOL_AGE--><br/>\n"
            + "Potential stake: <!--POTENTIAL_STAKE--><br/>\n"
            + "Transfer fees subtracted: <!--XFER_FEES--><br/>\n"
            + "\n"
            + "        <div id=\"pool_table\">\n"
            + "            <!--POOL_TABLE-->\n"
            + "        </div>\n"
            + "        <div id=\"other_stuff\">\n"
            + "             <!--OTHER_STUFF-->\n"
            + "        </div>\n"
            + "    </body>\n"
            + "</html>\n"
            + "";
//</editor-fold>

    public static void generateAll(Model m) {
        try {
            generateMaster(m);
            generatePools(m);
            copyStylesheet();
        } catch (IOException ex) {
            Logger.getLogger(HtmlGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void generateMaster(Model model) throws IOException {
        String template = MASTER_HTML_TEMPLATE;
        // date generated
        template = template.replace(PlaceholderStrings.DATE_GENERATED, toDateString(new Date().getTime()));
        // number of filled pools
        template = template.replace(PlaceholderStrings.NUM_FILLED_POOLS, String.valueOf(model.getNumFilledPools()));
        // number of pools maturing
        template = template.replace(PlaceholderStrings.NUM_POOLS_MATURING, String.valueOf(model.getNumMaturingPools()));
        // number of paid pools
        template = template.replace(PlaceholderStrings.NUM_POOLS_PAID, String.valueOf(model.getNumPoolsPaid()));
        // number or pools staking
        template = template.replace(PlaceholderStrings.NUM_POOLS_STAKING, String.valueOf(model.getNumStakingPools()));
        // pools paid out
        template = template.replace(PlaceholderStrings.NUM_POOLS_PAID, String.valueOf(model.getNumPoolsPaid()));
        // total profit
        template = template.replace(PlaceholderStrings.TOTAL_PROFIT, String.valueOf(model.getTotalProfit() / 1000000));
        // difficulty
        try {
            JSONObject diffJo = new JSONObject(new RpcWorker().getPosDifficulty());
            String diff = diffJo.getString("proof-of-stake");
            template = template.replace(PlaceholderStrings.CURRENT_DIFFICULTY, diff);
        } catch (JSONException ex) {
            Logger.getLogger(HtmlGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
        // master pool table
        String poolTable = "<table id=\"pool_table\" border=\"1\">\n"
                + "<tr id=\"pool_table_header\">\n"
                + "<th>Pool Name</td>\n"
                + "<th>Pool Size</td>\n"
                + "<th>Pool Status</td>\n"
                + "</tr>\n";
        int rownum = 0;
        for (Pool pool : model.pools) {
            if (rownum % 2 == 0) {
                poolTable += "<tr id=\"pool_table_oddrow\">\n";
            } else {
                poolTable += "<tr id=\"pool_table_evenrow\">\n";
            }
            rownum++;
            poolTable += "<td><a href=\"" + pool.getPoolName() +".html\">"+  pool.getPoolName()+ "</a></td>\n"
                    + "<td>" + pool.calculateFillAmount() / Constants.uH + "</td>\n"
                    + "<td>" + pool.getStatus().name() + "</td>\n"
                    + "</td>\n"
                    + "</tr>\n";
        }
        poolTable += "</table>\n";
        template = template.replace(PlaceholderStrings.MASTER_POOL_TABLE, poolTable);
        template = template.replace((PlaceholderStrings.POOL_ADDRESS), model.getPoolAddress());
        // tidy html
        template = tidyHtml(template);
        // write file
        // make sure target directory exists
        File d = new File(Constants.HTML_FILEPATH);
        d.mkdirs();

        FileUtils.write(new File(Constants.HTML_FILEPATH + "master.html"), template);
    }

    /**
     * Pretty print the given HTML
     */
    private static String tidyHtml(String html) {
        String result = "";
        try {
            try (StringWriter out = new StringWriter()) {
                try (InputStream in = new ByteArrayInputStream(html.getBytes())) {
                    Tidy tidy = new Tidy();
                    tidy.setIndentContent(true);
                    tidy.parse(in, out);

                    result = out.toString();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private static void generatePools(Model model) {
        for (Pool pool : model.pools) {
            String template = POOL_HTML_TEMPLATE;
            // date generated
            template = template.replace(PlaceholderStrings.DATE_GENERATED, toDateString(new Date().getTime()));
            template = template.replace(PlaceholderStrings.POOL_NAME, pool.getPoolName());
            template = template.replace(PlaceholderStrings.POOL_STATUS, pool.getStatus().toString());
            template = template.replace(PlaceholderStrings.POOL_FILL_DATE, toDateString(pool.getStartTimestamp()));
            long age = pool.getPoolAge(); // this is in ms
            long ageinseconds = age / 1000;
            String ageString = convertTimeToString(ageinseconds);
            template = template.replace(PlaceholderStrings.POOL_AGE, ageString);

            long potStake = pool.calculatePotentialStake();
            double potStaked = potStake;
            potStaked /= Constants.uH;
            String potStakeStr = String.format("%.4f", potStaked);

            template = template.replace(PlaceholderStrings.POTENTIAL_STAKE, potStakeStr);
            template = template.replace(PlaceholderStrings.XFER_FEES, "TODO");

            String poolTable = "<table id=\"pool_table\" border=\"1\">\n"
                    + "<tr id=\"pool_table_header\">\n"
                    + "<th>Participants</th>\n"
                    + "<th>Investment</th>\n"
                    + "<th>Percentage</th>\n"
                    + "<th>Current return</th>\n"
                    + "</tr>\n"
                    + "";
            int rownum = 0;
            for (Investment inv : pool.getInvestments()) {
                if (rownum % 2 == 0) {
                    poolTable += "<tr id=\"pool_table_oddrow\">\n";
                } else {
                    poolTable += "<tr id=\"pool_table_evenrow\">\n";
                }
                rownum++;
                double invAmount = inv.getAmount();
                invAmount /= Constants.uH;
                String invStr = String.format("%.3f", invAmount);
                poolTable += "<td>" + inv.getFromAddress() + "</td>\n"
                        + "<td>" + invStr + "</td>\n"
                        + "<td>" + calcPercentage(pool.calculateFillAmount(), inv.getAmount()) + "</td>\n"
                        + "<td>" + calcReturn(pool.calculateFillAmount(), inv.getAmount(), potStake) + "</td>\n"
                        + "</tr>\n";
            }
            // TODO: Total row
            poolTable += "</table>\n";
            template = template.replace(PlaceholderStrings.POOL_TABLE, poolTable);

            template = template.replace(PlaceholderStrings.OTHER_STUFF, "TODO");

            // tidy html
            template = tidyHtml(template);
            // write file
            // make sure target directory exists
            File d = new File(Constants.HTML_FILEPATH);
            d.mkdirs();

            try {
                FileUtils.write(new File(Constants.HTML_FILEPATH + pool.getPoolName() + ".html"), template);
            } catch (IOException ex) {
                Logger.getLogger(HtmlGenerator.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private static void copyStylesheet() {
    }

    private static CharSequence toDateString(long date) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm z yyyy/MM/dd");
        Date d = new Date(date);
        return sdf.format(d);
    }

    /**
     * Take a number of seconds and convert it to days, hours, minutes and
     * seconds
     */
    private static String convertTimeToString(long s) {
        long days = s / Constants.SECS_IN_DAY;
        s %= Constants.SECS_IN_DAY;
        long hours = s / Constants.SECS_IN_HOUR;
        s %= Constants.SECS_IN_HOUR;
        long minutes = s / Constants.SECS_IN_MINUTE;
        s %= Constants.SECS_IN_MINUTE;
        String response = "";
        if (days > 0) {
            response += "" + days + ((days == 1) ? " day " : " days ");
        }
        response += "" + hours + ((hours == 1) ? " hour " : " hours ");
        response += "" + minutes + ((minutes == 1) ? " minute " : " minutes ");
        response += "" + s + ((s == 1) ? " second" : " seconds");
        return response;
    }

    /**
     * calculate the percentage of poolSize investment is and return it as a
     * string with 3 digit precision
     */
    private static String calcPercentage(long poolSize, long invSize) {
        double d = ((double) poolSize * 100) / invSize;
        return String.format("%.3f", d);
    }

    /**
     * Calculate the potential return in HYP and return it as a string with 3
     * digit precision
     */
    private static String calcReturn(long poolSize, long invSize, long potStake) {
        double d = ((double) poolSize) / invSize;
        double ret = d * potStake;
        ret += invSize;
        // profit in in uHyp, so convert it to Hyp
        ret /= Constants.uH;
        return String.format("%.3f", ret);
    }

    //<editor-fold defaultstate="collapsed" desc="List of placeholder strings to be replaced">
    private class PlaceholderStrings {

        /**
         * The date/time this page was generated
         */
        public static final String DATE_GENERATED = "<!--CREATION_DATE-->";
        /**
         * The number of pools which we have ever filled
         */
        public static final String NUM_FILLED_POOLS = "<!--NUM_FILLED_POOLS-->";
        /**
         * How many pools are currently maturing
         */
        public static final String NUM_POOLS_MATURING = "<!--NUM_POOLS_MATURING-->";
        /**
         * Number of pools currently staking.
         */
        public static final String NUM_POOLS_STAKING = "<!--NUM_POOLS_STAKING-->";
        /**
         * Number of pools which have staked and paid out
         */
        public static final String NUM_POOLS_PAID = "<!--NUM_POOLS_PAID-->";
        /**
         * total profit so far
         */
        public static final String TOTAL_PROFIT = "<!--TOTAL_PROFIT_AMOUNT-->";
        /**
         * The master table with information and links to the pool pages
         */
        public static final String MASTER_POOL_TABLE = "<!--MASTER_POOL_TABLE-->";
        /**
         * current POS difficulty
         */
        public static final String CURRENT_DIFFICULTY = "<!--CURRENT_DIFFICULTY-->";
        public static final String POOL_NAME = "<!--POOL_NAME-->";
        public static final String POOL_ADDRESS = "<!--POOL_ADDRESS-->";
        public static final String POOL_STATUS = "<!--POOL_STATUS-->";
        public static final String POOL_FILL_DATE = "<!--POOL_FILL_DATE-->";
        public static final String POOL_AGE = "<!--POOL_AGE-->";
        public static final String POTENTIAL_STAKE = "<!--POTENTIAL_STAKE-->";
        public static final String XFER_FEES = "<!--XFER_FEES-->";
        public static final String POOL_TABLE = "<!--POOL_TABLE-->";
        public static final String OTHER_STUFF = "<!--OTHER_STUFF-->";
    }
//</editor-fold>
}
