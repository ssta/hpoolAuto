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
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

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
            + "        <div id=\"master_pool_table\">\n"
            + "            <!--MASTER_POOL_TABLE-->\n"
            + "        </div>\n"
            + "\n"
            + "    </body>\n"
            + "</html>\n"
            + "";
//</editor-fold>

    public static void main(String[] args) {
        try {
            generateMaster();
        } catch (IOException ex) {
            Logger.getLogger(HtmlGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void generateAll() {
        try {
            generateMaster();
            generatePools();
            copyStylesheet();
        } catch (IOException ex) {
            Logger.getLogger(HtmlGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void generateMaster() throws IOException {
        String template = MASTER_HTML_TEMPLATE;
        // date generated
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm z yyyy/MM/dd");
        template = template.replace(PlaceholderStrings.DATE_GENERATED, sdf.format(new Date()));
        // number of filled pools
        // number of pools maturing
        // number of paid pools
        // number or pools staking
        // total profit
        // master pool table
        // write file
        // make sure target directory exists
        File d = new File(Constants.HTML_FILEPATH);
        d.mkdirs();
        FileUtils.write(new File(Constants.HTML_FILEPATH + "master.html"), template);
    }

    private static void generatePools() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private static void copyStylesheet() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
        public static final String NUM_POOLS_PAID = "< !--NUM_POOLS_PAID-- >";
        /**
         * total profit so far
         */
        public static final String TOTAL_PROFIT = "<!--TOTAL_PROFIT_AMOUNT-->";
        /**
         * The master table with information and links to the pool pages
         */
        public static final String MASTER_POOL_TABLE = "<!--MASTER_POOL_TABLE-->";
    }
//</editor-fold>
}
