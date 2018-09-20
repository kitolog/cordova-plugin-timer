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

package com.applurk.nativetimer;

public interface NativeTimerAdapter {
	public void start(int delay, int interval);
	public void stop();
	public void setStartEventHandler(Consumer<Void> startEventHandler);
	public void setTickHandler(Consumer<Integer> tickHandler);
	public void setStopEventHandler(Consumer<Boolean> stopEventHandler);
	public void setErrorEventHandler(Consumer<String> errorEventHandler);
}