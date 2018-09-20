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

import java.util.HashMap;
import java.util.Map;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONException;
import org.json.JSONObject;

public class NativeTimerPlugin extends CordovaPlugin {

    Map<String, NativeTimerAdapter> nativeTimerAdapters = new HashMap<String, NativeTimerAdapter>();

    @Override
    public boolean execute(String action, CordovaArgs args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("start")) {
            this.start(args, callbackContext);
        } else if (action.equals("stop")) {
            this.stop(args, callbackContext);
        } else {
            callbackContext.error(String.format("NativeTimerPlugin - invalid action:", action));
            return false;
        }
        return true;
    }

    private void start(CordovaArgs args, CallbackContext callbackContext) throws JSONException {
        String timerKey = args.getString(0);
        int delay = args.getInt(1);
        int interval = args.getInt(2);

        NativeTimerAdapter nativeTimerAdapter = new NativeTimerAdapterImpl();
        nativeTimerAdapter.setStopEventHandler(new StopEventHandler(timerKey));
        nativeTimerAdapter.setTickHandler(new TickConsumer(timerKey));
        nativeTimerAdapter.setErrorEventHandler(new ErrorEventHandler(timerKey));
        nativeTimerAdapter.setStartEventHandler(new StartEventHandler(timerKey, nativeTimerAdapter, callbackContext));

        nativeTimerAdapter.start(delay, interval);
    }

    private void stop(CordovaArgs args, CallbackContext callbackContext) throws JSONException {
        String timerKey = args.getString(0);
        NativeTimerAdapter nativeTimerAdapter = this.getNativeTimerAdapter(timerKey);
        nativeTimerAdapter.stop();
        callbackContext.success();
    }

    private NativeTimerAdapter getNativeTimerAdapter(String timerKey) {
        if (!this.nativeTimerAdapters.containsKey(timerKey)) {
            throw new IllegalStateException("NativeTimer isn't connected.");
        }
        return this.nativeTimerAdapters.get(timerKey);
    }

    private void dispatchEvent(JSONObject jsonEventObject) {
        this.webView.sendJavascript(String.format("window.nativeTimer.dispatchEvent(%s);", jsonEventObject.toString()));
    }

    private class StopEventHandler implements Consumer<Boolean> {
        private String timerKey;

        public StopEventHandler(String timerKey) {
            this.timerKey = timerKey;
        }

        @Override
        public void accept(Boolean hasError) {
            nativeTimerAdapters.remove(this.timerKey);

            try {
                JSONObject event = new JSONObject();
                event.put("type", "OnStop");
                event.put("hasError", hasError.booleanValue());
                event.put("timerKey", this.timerKey);

                dispatchEvent(event);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class TickConsumer implements Consumer<Integer> {
        private String timerKey;

        public TickConsumer(String timerKey) {
            this.timerKey = timerKey;
        }

        @Override
        public void accept(Integer tick) {
            try {
                JSONObject event = new JSONObject();
                event.put("type", "OnTick");
                event.put("data", tick);
                event.put("timerKey", timerKey);

                dispatchEvent(event);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class ErrorEventHandler implements Consumer<String> {
        private String timerKey;

        public ErrorEventHandler(String timerKey) {
            this.timerKey = timerKey;
        }

        @Override
        public void accept(String errorMessage) {
            try {
                JSONObject event = new JSONObject();
                event.put("type", "OnError");
                event.put("errorMessage", errorMessage);
                event.put("timerKey", timerKey);

                dispatchEvent(event);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class StartEventHandler implements Consumer<Void> {
        private String timerKey;
        private NativeTimerAdapter nativeTimerAdapter;
        private CallbackContext openCallbackContext;

        public StartEventHandler(String timerKey, NativeTimerAdapter nativeTimerAdapter, CallbackContext openCallbackContext) {
            this.timerKey = timerKey;
            this.nativeTimerAdapter = nativeTimerAdapter;
            this.openCallbackContext = openCallbackContext;
        }

        @Override
        public void accept(Void voidObject) {
            nativeTimerAdapters.put(timerKey, nativeTimerAdapter);
            this.openCallbackContext.success();
        }
    }
}
