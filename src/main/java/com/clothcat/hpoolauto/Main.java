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

import com.clothcat.hpoolauto.model.Model;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author hyp
 */
public class Main {

    RpcWorker rpcworker = new RpcWorker();
    Model model = new Model();

    public static void main(String[] args) {
        Main m = new Main();
        m.launch();
    }

    private void checkHyperstakedRunning() {
        try {
            String s = rpcworker.checkwallet();
            debug(s);
            JSONObject j = new JSONObject(s);
            boolean pass = j.getBoolean("wallet check passed");

            if (!pass) {
                System.out.println("Problem!");
            }
        } catch (JSONException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void launch() {
        // currently all we do is go into the main loop
        mainLoop();
    }

    private void mainLoop() {
        while (true) {
            // make sure hyperstaked is running
            checkHyperstakedRunning();
            // get and process any new transactions
            model.processNewTx();
            // save the model
            model.updateAndSave();
            // sleep for 30 seconds    
            try {
                Thread.sleep(30000);
            } catch (InterruptedException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void debug(String s) {
        System.out.println(String.valueOf(System.currentTimeMillis()) + "::" + s);
    }

}
