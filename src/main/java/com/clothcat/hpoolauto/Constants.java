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
package com.clothcat.hpoolauto;

/**
 *
 * @author Stephen Stafford <clothcat@gmail.com>
 */
public class Constants {

  public static final String APPNAME = "Hyperpool";

  /**
   * The base directory where we store everything for Hyperpool
   */
  public static final String FILE_BASE = System.getProperty("user.home")
      + "/.Hyperpool/";

  /**
   * Where we store our json files
   */
  public static final String JSON_FILEPATH = FILE_BASE + "json/";

  /**
   * Where we store HTML files
   */
  public static final String HTML_FILEPATH = FILE_BASE + "html/";

  /**
   * Will generates log files called
   * <pre>Hyperpool_n.log</pre> where n is a rotating digit
   */
  public static final String LOGGFILE_PATTERN = FILE_BASE + APPNAME + "_%g.log";

  /**
   * How many microHyp there is in one Hyp
   */
  public static final long uH_IN_HYP = 1000000;
  /**
   * The transfer fee (in uHyp)
   */
  public static final long XFER_FEE = 10;
  public static final long SECS_IN_MINUTE = 60;
  public static final long SECS_IN_HOUR = 60 * SECS_IN_MINUTE;
  public static final long SECS_IN_DAY = 24 * SECS_IN_HOUR;
  public static final int MEBIBYTES = 1024 * 1024;

  /* FTP details at byethost */
  public static final String FTP_HOSTNAME="ftp.byethost11.com";
  public static final String FTP_USERNAME="b11_15559137";
  public static final String FTP_PASSWORD="tqNUz6o.1vE2";
  
  
  
}
