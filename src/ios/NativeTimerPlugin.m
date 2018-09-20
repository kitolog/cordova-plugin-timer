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

#import "NativeTimerPlugin.h"
#import "NativeTimerAdapter.h"
#import <cordova/CDV.h>
#import <Foundation/Foundation.h>

@implementation NativeTimerPlugin : CDVPlugin

- (void) start : (CDVInvokedUrlCommand*) command {
    
    NSLog(@"[TIMER] call");
    NSString *timerKey = [command.arguments objectAtIndex:0];
    NSNumber *delay = [command.arguments objectAtIndex:1];
    NSNumber *interval = [command.arguments objectAtIndex:2];

    NSLog(@"[TIMER] start %@", timerKey);
    if (timerAdapters == nil) {
        self->timerAdapters = [[NSMutableDictionary alloc] init];
    }

    __block NativeTimerAdapter* timerAdapter = [[NativeTimerAdapter alloc] init];
    timerAdapter.startEventHandler = ^ void () {
        [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK] callbackId:command.callbackId];

        [self->timerAdapters setObject:timerAdapter forKey:timerKey];

        timerAdapter = nil;
    };
    
    timerAdapter.startErrorEventHandler = ^ void (NSString *error){
        NSLog(@"[TIMER] startErrorEventHandler");
        [self.commandDelegate
         sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:error]
         callbackId:command.callbackId];
    };
    
    timerAdapter.errorEventHandler = ^ void (NSString *error, NSString *errorType){
        NSMutableDictionary *errorDictionaryData = [[NSMutableDictionary alloc] init];
        [errorDictionaryData setObject:@"OnError" forKey:@"type"];
        [errorDictionaryData setObject:errorType forKey:@"errorType"];
        [errorDictionaryData setObject:error forKey:@"errorMessage"];
        [errorDictionaryData setObject:timerKey forKey:@"timerKey"];

        [self dispatchEventWithDictionary:errorDictionaryData];
    };
    timerAdapter.tickHandler = ^ void (NSNumber* data) {
        NSMutableDictionary *dataDictionary = [[NSMutableDictionary alloc] init];
        [dataDictionary setObject:@"OnTick" forKey:@"type"];
        [dataDictionary setObject:data forKey:@"data"];
        [dataDictionary setObject:timerKey forKey:@"timerKey"];

        [self dispatchEventWithDictionary:dataDictionary];
    };
    timerAdapter.stopEventHandler = ^ void (BOOL hasErrors) {
        NSLog(@"[TIMER] stopEventHandler");
        NSMutableDictionary *closeDictionaryData = [[NSMutableDictionary alloc] init];
        [closeDictionaryData setObject:@"OnStop" forKey:@"type"];
        [closeDictionaryData setObject:(hasErrors == TRUE ? @"true": @"false") forKey:@"hasError"];
        [closeDictionaryData setObject:timerKey forKey:@"timerKey"];

        [self dispatchEventWithDictionary:closeDictionaryData];

        [self removeTimerAdapter:timerKey];
    };

    [self.commandDelegate runInBackground:^{
        @try {
            [timerAdapter start:delay interval:interval];
        }
        @catch (NSException *e) {
            [self.commandDelegate
             sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:e.reason]
             callbackId:command.callbackId];

            timerAdapter = nil;
        }
    }];
}

- (void) stop:(CDVInvokedUrlCommand *) command {

    NSString* timerKey = [command.arguments objectAtIndex:0];

    NativeTimerAdapter *timerAdapter = [self getTimerAdapter:timerKey];

    [self.commandDelegate runInBackground:^{
        @try {
            if (timerAdapter != nil) {
                [timerAdapter stop];
            }else{
                NSLog(@"[TIMER] Stop: timerAdapter is nil. TimerKey: %@", timerKey);
            }

            [self.commandDelegate
             sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK]
             callbackId:command.callbackId];
        }
        @catch (NSException *e) {
            [self.commandDelegate
             sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:e.reason]
             callbackId:command.callbackId];
        }
    }];
}

- (void) setOptions: (CDVInvokedUrlCommand *) command {
}

- (NativeTimerAdapter*) getTimerAdapter: (NSString*) timerKey {
    NativeTimerAdapter* timerAdapter = [self->timerAdapters objectForKey:timerKey];
    if (timerAdapter == nil) {
        NSLog(@"[TIMER] Cannot find timerKey: %@. Connection is probably closed.", timerKey);
        //NSString *exceptionReason = [NSString stringWithFormat:@"Cannot find timerKey: %@. Connection is probably closed.", timerKey];

        //@throw [NSException exceptionWithName:@"IllegalArgumentException" reason:exceptionReason userInfo:nil];
    }
    return timerAdapter;
}

- (void) removeTimerAdapter: (NSString*) timerKey {
    NSLog(@"[TIMER] Removing timer adapter from storage.");
    [self->timerAdapters removeObjectForKey:timerKey];
}

- (BOOL) timerAdapterExists: (NSString*) timerKey {
    NativeTimerAdapter* timerAdapter = [self->timerAdapters objectForKey:timerKey];
    return timerAdapter != nil;
}

- (void) dispatchEventWithDictionary: (NSDictionary*) dictionary {
    NSData *jsonData = [NSJSONSerialization dataWithJSONObject:dictionary options:0 error:nil];
    NSString *jsonString = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];

    [self dispatchEvent:jsonString];
}

- (void) dispatchEvent: (NSString *) jsonEventString {
    NSString *jsToEval = [NSString stringWithFormat : @"window.nativeTimer.dispatchEvent(%@);", jsonEventString];
    [self.commandDelegate evalJs:jsToEval];
}

@end
