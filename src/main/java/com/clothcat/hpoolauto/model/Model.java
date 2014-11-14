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

import java.util.List;

/**
 * The Model consists of several pools in various stages.
 *
 * It's a singleton which is ugly, and I'll probably change that at some point.
 *
 * @author Stephen Stafford <clothcat@gmail.com>
 */
public class Model {

    private static Model instance;
    List<Pool> pools;
    CurrentState currentState;

    static final String CURRENT_STATE_FILENAME = "currentState.json";

    private Model() {

    }

    static Model getInstance() {
        if (instance == null) {
            instance = new Model();
        }
        return instance;
    }
    /* Method skeletons for now */

    /**
     *
     * @return The number of pools we've ever filled.
     */
    public int getNumFilledPools() {
        return 10;
    }

    /**
     * @return How many pools we currently have that are maturing (full, but not
     * yet old enough to be eligible for stake)
     */
    public int getNumMaturingPools() {
        return 5;
    }

    /**
     * @return How many pools we currently have that are old enough to stake but
     * which haven't yet staked.
     */
    public int getNumStakingPools() {
        return 2;
    }
    
    /**
     * @return How many pools have staked and been paid out.
     */
    public int getNumPoolsPaid(){
        return 3;
    }
    
    /**
     * @return How much profit we've made for investors thus far (in uHyp)
     */
    public long getTotalProfit(){
        return 1234123400L;
    }
    
}
