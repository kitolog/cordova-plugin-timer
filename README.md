cordova-plugin-timer
============================
Cordova native timer plugin

### Installation
cordova plugin add cordova-plugin-timer

// you may also install directly from this repo
cordova plugin add https://github.com/kitolog/cordova-plugin-timer
 
## Sample usage
Here is simple example of how to connect to remote server, consume data from it and close the connection.

Create instance of Socket type:
```
var timer = new window.nativeTimer();
```

Set data consumer, error and close handlers:
```
timer.onTick = function(tick) {
  // invoked on tick
};
timer.onError = function(errorMessage) {
  // invoked after error occurs
};
timer.onStop during connection = function(hasError) {
  // invoked after stop
};
```
Start timer with delay 1ms and repeat 1000ms
```
timer.start(
  1,
  1000,
  function() {
    // invoked after successful start
  },
  function(errorMessage) {
    // invoked after unsuccessful start
  });
```

stop the timer
```
timer.stop();
```

## API
### Event handlers
#### `onTick: (data: int) => void`
Invoked after new tick is received by the timer.

#### `onStop: (hasError: boolean) => void`
Invoked after timer was stopped.

#### `onError: (message: string) => void`
Invoked when some error occurs during timer process.

#### `on: (eventName: string, callback: function) => void`
Syntax sugar for the event handlers (onTick, onStop, onError)
eventName: `error`, `tick`, `stop` 

### Methods
#### `start(delay, interval, onSuccess?, onError?): void`
Establishes connection with the remote host.

| parameter   | type          | description |
| ----------- |-----------------------------|--------------|
| `delay`     | `number`                    | timer delay | |
| `interval`  | `number`                    | timer tick interval |
| `onSuccess` | `() => void`                | Success callback - called after successfull timer start. (optional)|
| `onError`   | `(message: string) => void` | Error callback - called when some error occurs during timer start. (optional)|


#### `stop(onSuccess?, onError?): void`
Closes the connection. `onClose` event handler is called when connection is successfuly closed.

| parameter   | type          | description |
| ----------- |-----------------------------|--------------|
| `onSuccess` | `() => void`                | Success callback, called after timer was stopped. (optional)|
| `onError`   | `(message: string) => void` | Error callback, called when some error occurs during this procedure. (optional)|



## What's new
 - 1.0.0 - initial code
 - 1.0.1 - added common event handler