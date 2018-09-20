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
Invoked after new batch of data is received by the client. Data are represented as typed array of bytes (`Uint8Array`).

#### `onStop: (hasError: boolean) => void`
Invoked after connection close. Native resources are released after this handler is invoked. Parameter `hasError` indicates whether connection was closed as a result of some error.

#### `onError: (message: string) => void`
Invoked when some error occurs during connection.


### Methods
#### `start(host, port, onSuccess?, onError?): void`
Establishes connection with the remote host.

| parameter   | type          | description |
| ----------- |-----------------------------|--------------|
| `delay`     | `number`                    | delay | |
| `interval`  | `number`                    | interval |
| `onSuccess` | `() => void`                | Success callback - called after successfull connection to the remote host. (optional)|
| `onError`   | `(message: string) => void` | Error callback - called when some error occurs during connecting to the remote host. (optional)|


#### `stop(onSuccess?, onError?): void`
Closes the connection. `onClose` event handler is called when connection is successfuly closed.

| parameter   | type          | description |
| ----------- |-----------------------------|--------------|
| `onSuccess` | `() => void`                | Success callback, called after connection is successfully closed. `onClose` event handler is called before that callback. (optional)|
| `onError`   | `(message: string) => void` | Error callback, called when some error occurs during this procedure. (optional)|



## What's new
 - 1.0.0 - initial code