package com.atomunion.core.reflex.callback;

import java.lang.reflect.InvocationTargetException;

/**
 * @author lico
 * @version 1.0.000 At Apr 10, 2013
 * */
public interface ReflexCallBack<P extends Object,PP extends Object,RP extends Object, R extends Object> {
	R execute(P value, PP previous,	RP root,String name,String key) throws SecurityException,
			IllegalArgumentException, NoSuchMethodException,
			IllegalAccessException, InvocationTargetException;
}
