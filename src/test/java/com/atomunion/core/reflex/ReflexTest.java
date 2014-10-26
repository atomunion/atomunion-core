package com.atomunion.core.reflex;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.atomunion.core.reflex.Reflex;
import com.atomunion.core.reflex.callback.ReflexCallBack;

/**
 * @author lico
 * @version 1.0.000 At Apr 10, 2013
 * */
public class ReflexTest {

	private String strict;
	private List<String> list;
	private String[] array;
	private Map<String, String> map;

	@Before
	public void setUp() throws Exception {
		strict = Reflex.STRICT;
		list = new ArrayList<String>();
		list.add("1111");
		list.add("2222");
		list.add("3333");

		array = new String[] { "aaaa", "bbbb", "cccc" };

		map = new HashMap<String, String>();
		map.put("1", "yayaya");
		map.put("2", "mumumu");
		map.put("3", "pipipi");
	}

	@Ignore
	@Test
	public void testReflex() throws Exception {

		System.out.println(Reflex.reflexGetter(list, "this[1]",
				new ReflexCallBack<Object, Object, Object, Object>() {
					public Object execute(Object value, Object previous,
							Object root, String name, String key) {
						System.out.println(key + "&&&&&&&&&&&&&&" + value);
						return value;
					}
				}, strict));
		System.out.println(Reflex.reflexGetter(array, "this[1]",
				new ReflexCallBack<Object, Object, Object, Object>() {
					public Object execute(Object value, Object previous,
							Object root, String name, String key) {
						System.out.println(key + "&&&&&&&&&&&&&&" + value);
						return value;
					}
				}, strict));
		System.out.println(Reflex.reflexGetter(map, "this[1]",
				new ReflexCallBack<Object, Object, Object, Object>() {
					public Object execute(Object value, Object previous,
							Object root, String name, String key) {
						System.out.println(key + "&&&&&&&&&&&&&&" + value);
						return value;
					}
				}, strict));
	}

	@Ignore
	@Test
	public void testGetAnnotation() throws Exception {

		Reflex.reflexAnnotation(new Bean().getClass(), ATest.class,
				new ReflexCallBack<ATest, Object, Class<?>, Object>() {
					@Override
					public Object execute(ATest value, Object previous,
							Class<?> root, String name, String key) {
						System.out.println(value);
						return value;
					}
				});

	}

	@Test
	public void testSplitKey() throws Exception {
		println(Reflex.splitKey("this.a.b[\"c\"].d"));
		println(Reflex.splitKey("a.b[\"c\"].d"));
		println(Reflex.splitKey("   this.a.b[\"c\"].d   "));

	}

	private void println(String[] keys) {
		for (String key : keys) {
			System.out.print(key+"\t");
		}
		
		System.out.println();
	}
}

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@interface ATest {
}

@ATest
class Bean {

}