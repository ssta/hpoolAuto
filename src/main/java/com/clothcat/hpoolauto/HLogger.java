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

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Singleton logger class. Yeah, I know singletons are evil blah blah blah...
 *
 * @author Stephen Stafford <clothcat@gmail.com>
 */
public class HLogger {

  private static HLogger instance;
  private Logger logger;
  private FileHandler fileHandler;

  private HLogger() {
    try {
      /* set the format property
       * format string stolen from SimpleFormatter API docs:
       * This prints 2 lines where the first line includes the timestamp 
       * (1$) and the source (2$); the second line includes the log level 
       * (4$) and the log message (5$) followed with the throwable and its 
       * backtrace (6$), if any */
      System.getProperties().setProperty(
          "java.util.logging.SimpleFormatter.format",
          "%1$tc %2$s%n%4$s: %5$s%6$s%n");

            // set the logging level to ALL - this may change later to remove
      // debugging stuff not needed normally
      fileHandler.setLevel(Level.ALL);

      logger = Logger.getLogger("HLogger");
      fileHandler = new FileHandler(Constants.LOGGFILE_PATTERN,
          5 * Constants.MEBIBYTES, 3, true);
      SimpleFormatter format = new SimpleFormatter();
      fileHandler.setFormatter(format);
      logger.info("Constructed new HLogger.");
    } catch (IOException | SecurityException ex) {
      Logger.getLogger(HLogger.class.getName()).log(Level.SEVERE, null,
          ex);
    }
  }

  private static HLogger getInstance() {
    if (instance == null) {
      instance = new HLogger();
    }
    return instance;
  }

  /**
   * Log message with level INFO
   */
  public static void log(String message) {
    log(Level.INFO, message, null);
  }

  public static void log(Level level, String message) {
    log(Level.INFO, message, null);
  }

  public static void log(Level level, String message, Throwable t) {
    getInstance().logger.log(level, message, t);
  }

}
