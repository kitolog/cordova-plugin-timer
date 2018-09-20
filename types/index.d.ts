
interface Window {
    nativeTimer: {
        start(
            delay: number,
            interval: number,
            successCallback: (status: number) => void,
            errorCallback?: (fileError: NativeTimerError) => void): void;
        stop(
            successCallback: (status: number) => void,
            errorCallback?: (fileError: NativeTimerError) => void): void;
        checkState(
            state: string,
            errorCallback?: (fileError: NativeTimerError) => void): boolean;
    }
}

interface NativeTimerError {
    /** Error code */
    code: number;
}