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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Level;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPReply;

/**
 *
 * @author Stephen Stafford <clothcat@gmail.com>
 */
public class HtmlUploader {

  /**
   * Upload the html via FTP
   */
  public static void uploadHtml() {
    FTPClient ftpc = new FTPClient();
    FTPClientConfig conf = new FTPClientConfig();
    ftpc.configure(conf);
    try {
      ftpc.connect(Constants.FTP_HOSTNAME);
      ftpc.login(Constants.FTP_USERNAME, Constants.FTP_PASSWORD);
      File dir = new File(Constants.HTML_FILEPATH);
      File[] files = dir.listFiles();
      ftpc.changeWorkingDirectory("/htdocs");
      for (File f : files) {
        HLogger.log(Level.FINEST, "Uploading file: " + f.getName());
        FileInputStream fis = new FileInputStream(f);
        ftpc.storeFile(f.getName(), fis);
      }
      ftpc.logout();
    } catch (IOException ioe) {
      ioe.printStackTrace();
    } finally {
      if (ftpc.isConnected()) {
        try {
          ftpc.disconnect();
        } catch (IOException ioe) {
        }
      }
    }
  }
}
