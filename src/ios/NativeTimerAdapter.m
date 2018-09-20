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

//#include <netdb.h>
//#include <arpa/inet.h>
//#include <math.h>
#import "NativeTimerAdapter.h"

NSTimer *timerInstance;

BOOL wasStarted = FALSE;
float delay = 0.0;
float interval = 1.0;

int tick = 0;

/**
 * interval - float
 */
 
@implementation NativeTimerAdapter

- (void)start:(NSNumber *)delay interval:(NSNumber*)interval {

    NSLog(@"[TIMER] START timer delay: %@ interval: %@", [delay stringValue], [interval stringValue]);
    
    tick = 0;
    double delayDouble = [delay doubleValue] / 1000;
    double intervalDouble = [interval doubleValue] / 1000;
    NSDate *fireDate = [NSDate dateWithTimeIntervalSinceNow:delayDouble];
    NSTimer *timer = [[NSTimer alloc] initWithFireDate: fireDate
                                          interval: intervalDouble
                                            target: self
                                          selector:@selector(onTimerTick:)
                                          userInfo:nil repeats:YES];
//    NSTimer *timer = [NSTimer timerWithTimeInterval:interval target:self selector:@selector(onTimerTick:) userInfo:nil repeats:YES];
    [[NSRunLoop mainRunLoop] addTimer:timer forMode:NSDefaultRunLoopMode];
    timerInstance = timer;
    self.startEventHandler();
    wasStarted = TRUE;
}

-(void)onTimerTick:(NSTimer *)timer {
    tick++;
    NSLog(@"[TIMER] On timer tick: %d", tick);
    NSNumber* tickNumber = [NSNumber numberWithInteger:tick];
    self.tickHandler(tickNumber);
}

- (void)stop {
    NSLog(@"[TIMER] timerInstance STOP");
    self.stopEventHandler(FALSE);
    if(timerInstance != nil){
        [timerInstance invalidate];
        timerInstance = nil;
        NSLog(@"[TIMER] timerInstance invalidated");
    }else{
        NSLog(@"[TIMER] timerInstance not found");
    }
}

@end
