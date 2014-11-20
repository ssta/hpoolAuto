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
package com.clothcat.hpoolauto.shim;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.json.JSONObject;

/**
 * Processes shim commands.
 *
 * @author Stephen Stafford <clothcat@gmail.com>
 */
public class CommandProcessor {

  static final Map<String, Class<? extends ShimCommand>> dispatchTable;

  static {
    // static initializer (basically generates the dispatch table).
    dispatchTable = new TreeMap<>();
    dispatchTable.put("gettransactiontoprocess", GetTransactionsToProcess.class);
    dispatchTable.put("marktransactionprocessed", MarkTransactionProcessed.class);
    dispatchTable.put("gettransactiondetails", GetTransactionDetails.class);
    dispatchTable.put("sendcoins", SendCoins.class);
    dispatchTable.put("movecoins", MoveCoins.class);
    dispatchTable.put("getnewaddress", GetNewAddress.class);
  }

  public CommandProcessor() {

  }

  public JSONObject processCommand(List<String> command) {
    throw new UnsupportedOperationException("not yet implemented");
  }

}
