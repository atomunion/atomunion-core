package com.atomunion.core.util;

import java.util.WeakHashMap;

/**
 * 同步器
 * 
 * @author lico
 * @version 1.0.000 At Apr 10, 2013
 */
public class Synchronizer {

	private static final WeakHashMap<Object, Locker> LOCK_MAP = new WeakHashMap<Object, Locker>();

	private static class Locker {
		private Locker() {

		}
	}

	public static synchronized Object acquire(Object key) {
		Locker locker = LOCK_MAP.get(key);
		if (locker == null) {
			locker = new Locker();
			LOCK_MAP.put(key, locker);
		}
		return locker;
	}
}
