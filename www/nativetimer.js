/**
 * Copyright (c) 2018, kitolog
 * All rights reserved.
 *
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

var exec = require('cordova/exec');
var TIMER_EVENT = "NATIVE_TIMER_EVENT";
var CORDOVA_SERVICE_NAME = "NativeTimer";

NativeTimer.State = {};
NativeTimer.State[NativeTimer.State.STARTED = 1] = "STARTED";
NativeTimer.State[NativeTimer.State.STARTING = 2] = "STARTING";
NativeTimer.State[NativeTimer.State.STOPPING = 3] = "STOPPING";
NativeTimer.State[NativeTimer.State.STOPPED = 0] = "STOPPED";

NativeTimer.ErrorType = {};
NativeTimer.ErrorType[NativeTimer.ErrorType.GENERAL = 0] = "general";

function NativeTimer() {
    this._state = NativeTimer.State.STOPPED;
    this.timerKey = guid();
    this.onTick = function () {
        console.log('On timer tick');
    };
    this.onStop = function () {
        console.log('On timer stop');
    };
    this.onError = function (e) {
        console.error('On timer error');
        console.error(e);
    };

    this.on = function (eventName, callback) {
        if ((typeof eventName === 'string') && (typeof callback === 'function')) {
            switch (eventName) {
                case 'tick':
                    this.onTick = function (e) {
                        callback(e);
                    };
                    break;
                case 'stop':
                    this.onStop = function (e) {
                        callback(e);
                    };
                    break;
                case 'error':
                    this.onError = function (e) {
                        callback(e);
                    };
                    break;
            }
        }
    };
}

NativeTimer.prototype.start = function (delay, interval, success, error) {
    success = success || function () {
    };
    error = error || function () {
    };

    if ((typeof delay === 'undefined') || delay === null) {
        delay = 0;
    }

    if ((typeof interval === 'undefined') || interval === null) {
        interval = 1000;
    }

    if (!this.checkState(NativeTimer.State.STOPPED, error)) {
        return;
    }

    var _that = this;

    function timerEventHandler(event) {
        var payload = event.payload;
        if (payload.timerKey !== _that.timerKey) {
            return;
        }

        switch (payload.type) {
            case "OnStop":
                _that._state = NativeTimer.State.STOPPED;
                window.document.removeEventListener(TIMER_EVENT, timerEventHandler);
                _that.onStop(payload.hasError);
                break;
            case "OnTick":
                _that.onTick(payload.data);
                break;
            case "OnError":
                _that.onError(payload);
                break;
            default:
                console.error("NativeTimer: Unknown event type " + payload.type + ", timer key: " + payload.timerKey);
                break;
        }
    }

    _that._state = NativeTimer.State.STARTING;

    exec(
        function () {
            _that._state = NativeTimer.State.STARTED;
            window.document.addEventListener(TIMER_EVENT, timerEventHandler);
            success();
        },
        function (errorMessage) {
            _that._state = NativeTimer.State.STOPPED;
            error(errorMessage);
        },
        CORDOVA_SERVICE_NAME,
        "start",
        [
            this.timerKey,
            delay,
            interval
        ]);
};

NativeTimer.prototype.stop = function (success, error) {

    success = success || function () {
    };
    error = error || function () {
    };

    if (!this.checkState(NativeTimer.State.STARTED, error)) {
        return;
    }

    this._state = NativeTimer.State.STOPPING;

    exec(
        success,
        error,
        CORDOVA_SERVICE_NAME,
        "stop",
        [this.timerKey]);
};

Object.defineProperty(NativeTimer.prototype, "state", {
    get: function () {
        return this._state;
    },
    enumerable: true,
    configurable: true
});

NativeTimer.prototype.checkState = function (requiredState, errorCallback) {
    var state = this._state;
    if (state != requiredState) {
        errorCallback = errorCallback || function () {
        };
        window.setTimeout(function () {
            errorCallback("Invalid operation for this timer state: " + NativeTimer.State[state]);
        });
        return false;
    }
    else {
        return true;
    }
};

NativeTimer.dispatchEvent = function (event) {
    var eventReceive = document.createEvent('Events');
    eventReceive.initEvent(TIMER_EVENT, true, true);
    eventReceive.payload = event;

    document.dispatchEvent(eventReceive);
};

var guid = (function () {
    function s4() {
        return Math.floor((1 + Math.random()) * 0x10000)
            .toString(16)
            .substring(1);
    }

    return function () {
        return s4() + s4() + '-' + s4() + '-' + s4() + '-' +
            s4() + '-' + s4() + s4() + s4();
    };
})();

module.exports = NativeTimer;