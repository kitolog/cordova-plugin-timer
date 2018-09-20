/**
 * Copyright (c) 2018, kitolog
 * All rights reserved.
 * <p>
 * Redistribution and use in source and binary forms are permitted
 * provided that the above copyright notice and this paragraph are
 * duplicated in all such forms and that any documentation,
 * advertising materials, and other materials related to such
 * distribution and use acknowledge that the software was developed
 * by kitolog. The name of the
 * kitolog may not be used to endorse or promote products derived
 * from this software without specific prior written permission.
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 */

package com.applurk.nativetimer;

import android.os.Handler;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

public class NativeTimerAdapterImpl implements NativeTimerAdapter {

    private Consumer<Void> startEventHandler;
    private Consumer<Integer> tickHandler;
    private Consumer<Boolean> stopEventHandler;
    private Consumer<String> errorEventHandler;

    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        public void run() {
            invokeTickHandler(2);
        }
    };


    private Timer mTimer;
    int tick = 0;

    public NativeTimerAdapterImpl() {
    }

    @Override
    public void start(final int delay, final int interval) {
        tick = 0;
        final Handler handler = new Handler();
        mTimer = new Timer(false); //true or false?
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        tick++;
                        invokeTickHandler(tick);
                    }
                });
            }
        };

        mTimer.scheduleAtFixedRate(timerTask, delay, interval);
        invokeStartEventHandler();
    }

    @Override
    public void stop() {
        if (mTimer != null) {
            mTimer.cancel();
            this.invokeStopEventHandler(false);
        } else {
            Log.d("NativeTimer", "Timer stop: not found instance");
            this.invokeStopEventHandler(false);
        }
    }

    @Override
    public void setStartEventHandler(Consumer<Void> startEventHandler) {
        this.startEventHandler = startEventHandler;
    }

    @Override
    public void setTickHandler(Consumer<Integer> tickHandler) {
        this.tickHandler = tickHandler;
    }

    @Override
    public void setStopEventHandler(Consumer<Boolean> stopEventHandler) {
        this.stopEventHandler = stopEventHandler;
    }

    @Override
    public void setErrorEventHandler(Consumer<String> errorEventHandler) {
        this.errorEventHandler = errorEventHandler;
    }

    private void invokeStartEventHandler() {
        if (this.startEventHandler != null) {
            this.startEventHandler.accept((Void) null);
        }
    }

    private void invokeTickHandler(int data) {
        if (this.tickHandler != null) {
            this.tickHandler.accept(data);
        }
    }

    private void invokeStopEventHandler(boolean hasError) {
        if (this.stopEventHandler != null) {
            this.stopEventHandler.accept(hasError);
        }
    }

    private void invokeExceptionHandler(String errorMessage) {
        if (this.errorEventHandler != null) {
            this.errorEventHandler.accept(errorMessage);
        }
    }
}
