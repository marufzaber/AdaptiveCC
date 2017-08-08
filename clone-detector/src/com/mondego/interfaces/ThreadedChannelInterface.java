package com.mondego.interfaces;

import java.lang.reflect.InvocationTargetException;

public interface ThreadedChannelInterface<E> {
	
	
	public void send(E e) throws InstantiationException, IllegalAccessException,
    IllegalArgumentException, InvocationTargetException,
    NoSuchMethodException, SecurityException;
	
	public void shutdown();

}
