package com.atomunion.core.reflex;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.atomunion.core.reflex.callback.ReflexCallBack;
import com.atomunion.core.util.Assert;
import com.atomunion.core.util.Validate;

/**
 * @author lico
 * @version 1.0.000 At Apr 10, 2013
 * */
public abstract class Reflex {
	public static final String STRICT = "STRICT";
	public static final String LOOSE = "LOOSE";

	public static Object reflexGetter(Object pojo, String key,
			ReflexCallBack<Object, Object, Object, Object> callback,
			String type, String... methods) throws IllegalArgumentException,
			IllegalAccessException, InvocationTargetException,
			SecurityException, NoSuchMethodException {
		Object value = pojo;
		if (value != null && key != null) {
			String[] keys = splitKey(key);
			if (keys != null) {
				Map<String, String> methodsInvoke = new HashMap<String, String>();
				if (methods != null && methods.length > 0) {
					for (String method : methods) {
						methodsInvoke.put(method, method);
					}
				}

				for (int i = 0; i < keys.length; i++) {
					String name = keys[i].trim();
					if ("".equals(name)) {
						if (!LOOSE.equals(type)) {
							throw new NoSuchGetterMethodException(
									"can't reflex object \"" + pojo
											+ "\" using the key \"" + key
											+ "\" of wrong style.");
						} else {
							continue;
						}
					}

					Boolean last = (i == keys.length - 1);
					Object previous = value;
					value = reflexGetter(value, name, methodsInvoke);
					if (last || (!last && value == null && LOOSE.equals(type))) {
						return (callback != null) ? callback.execute(value,
								previous, pojo, name, key) : value;
					}
					if (value == null) {
						break;
					}
				}
			}

		}
		if (!LOOSE.equals(type))
			throw new NoSuchGetterMethodException("can't reflex object \""
					+ pojo + "\" using the key \"" + key + "\"");
		else
			return value;
	}

	private static String validateKey(String key)
			throws NoSuchGetterMethodException {
		Assert.notNull(key);
		Character current = null, pre = null, point = null;
		StringBuffer emp = new StringBuffer();
		for (int i = 0; i < key.length(); i++) {
			current = key.charAt(i);
			switch (current) {
			case ' ':
			case '"':
			case '\'':
				break;
			case '[':
			case '.':
			case ']':
				if (!check(current, pre, point)) {
					throw new NoSuchGetterMethodException(
							"can't reflex object using the key \"" + key
									+ "\" of wrong style.");
				}
				point = current;
			default:
				pre = current;
				if (current.equals('[')) {
					emp.append('.');
				} else if (!current.equals(']')) {
					emp.append(current);
				}
				break;
			}
		}
		if (((Character) '.').equals(emp.charAt(emp.length() - 1))) {
			throw new NoSuchGetterMethodException(
					"can't reflex object using the key \"" + key
							+ "\" of wrong style.");
		}
		return emp.toString();
	}

	private static boolean check(Character current, Character pre,
			Character split) {
		if (pre == null) {
			return false;
		}
		if (current.equals(pre)) {
			return false;
		}
		if (((Character) '[').equals(split)
				&& ((Character) ']').equals(current) && pre.equals(split)) {
			return false;
		}
		if (((Character) '[').equals(split)
				&& ((Character) '.').equals(current)) {
			return false;
		}
		if (((Character) '.').equals(pre)
				&& (((Character) ']').equals(current) || ((Character) '[')
						.equals(current))) {
			return false;
		}
		return true;
	}

	public static String[] splitKey(String key)
			throws NoSuchGetterMethodException {
		key = validateKey(key);
		return key == null ? null : key.split("\\.");
	}

	public static Object reflexGetter(final Object val, String name,
			Map<String, String> methodsInvoke) throws IllegalArgumentException,
			IllegalAccessException, InvocationTargetException {
		if ("this".equals(name)) {
			return val;
		}
		Assert.notNull(val);

		if (Validate.isInstanceOf(Map.class, val)) {
			try {
				return ((Map<?, ?>) val).get(name);
			} catch (Exception e) {
				try {
					return ((Map<?, ?>) val).get(Integer.parseInt(name));
				} catch (Exception e1) {
					try {
						return ((Map<?, ?>) val).get(Long.parseLong(name));
					} catch (Exception e2) {
						try {
							return ((Map<?, ?>) val).get(Double.parseDouble(name));
						} catch (Exception e3) {
							try {
								return ((Map<?, ?>) val).get(Float.parseFloat(name));
							} catch (Exception e4) {
								try {
									return ((Map<?, ?>) val).get(Short.parseShort(name));
								} catch (Exception e5) {
									try {
										return ((Map<?, ?>) val).get(Byte.parseByte(name));
									} catch (Exception e6) {
										return null;
									}
								}
							}
						}
					}
				}
			}
		} else if (Validate.isInstanceOf(List.class, val)) {
			return ((List<?>) val).get(Integer.parseInt(name));
		} else if (Validate.isInstanceOf(Object[].class, val)) {
			return ((Object[]) val)[Integer.parseInt(name)];
		}

		methodsInvoke.put("getter", "get" + name.substring(0, 1).toUpperCase()
				+ name.substring(1));

		for (final String method : methodsInvoke.values()) {
			return reflexGeneration(val.getClass(),
					new ReflexCallBack<Class<?>, Class<?>, Class<?>, Object>() {
						@Override
						public Object execute(Class<?> value,
								Class<?> previous, Class<?> root, String name,
								String key) throws SecurityException,
								NoSuchMethodException,
								IllegalArgumentException,
								IllegalAccessException,
								InvocationTargetException {
							Method pmethod = value.getDeclaredMethod(method);
							return pmethod.invoke(val);
						}
					});
		}
		return null;
	}

	private static Object reflexGeneration(Class<?> pojo,
			ReflexCallBack<Class<?>, Class<?>, Class<?>, Object> callback) {
		Assert.notNull(pojo);
		Assert.notNull(callback);
		Class<?> pclass = pojo instanceof Class<?> ? (Class<?>) pojo : pojo
				.getClass();
		Class<?> previous = null;
		do {
			try {
				return callback.execute(pclass, previous, pojo, null, null);
			} catch (Exception e) {
				previous = pclass;
				pclass = pclass.getSuperclass();
			}
		} while (pclass != null && pclass != Object.class);
		return null;
	}

	private static Object reflexPackage(Class<?> pojo,
			final ReflexCallBack<Package, Class<?>, Class<?>, Object> callback) {
		Assert.notNull(pojo);
		Assert.notNull(callback);

		return reflexGeneration(pojo,
				new ReflexCallBack<Class<?>, Class<?>, Class<?>, Object>() {
					@Override
					public Object execute(Class<?> value, Class<?> previous,
							Class<?> root, String name, String key)
							throws SecurityException, IllegalArgumentException,
							NoSuchMethodException, IllegalAccessException,
							InvocationTargetException {
						Package pack = value.getPackage();
						return callback.execute(pack, value, root, null, null);
					}
				});
	}

	private static Object reflexMethods(Class<?> pojo,
			final ReflexCallBack<Method[], Class<?>, Class<?>, Object> callback) {
		Assert.notNull(pojo);
		Assert.notNull(callback);

		return reflexGeneration(pojo,
				new ReflexCallBack<Class<?>, Class<?>, Class<?>, Object>() {
					@Override
					public Object execute(Class<?> value, Class<?> previous,
							Class<?> root, String name, String key)
							throws SecurityException, IllegalArgumentException,
							NoSuchMethodException, IllegalAccessException,
							InvocationTargetException {
						Method[] pmethod = value.getDeclaredMethods();
						return callback.execute(pmethod, value, root, null,
								null);
					}
				});
	}

	private static Object reflexFields(Class<?> pojo,
			final ReflexCallBack<Field[], Class<?>, Class<?>, Object> callback) {
		Assert.notNull(pojo);
		Assert.notNull(callback);

		return reflexGeneration(pojo,
				new ReflexCallBack<Class<?>, Class<?>, Class<?>, Object>() {
					@Override
					public Object execute(Class<?> value, Class<?> previous,
							Class<?> root, String name, String key)
							throws SecurityException, IllegalArgumentException,
							NoSuchMethodException, IllegalAccessException,
							InvocationTargetException {
						Field[] pmethod = value.getDeclaredFields();
						return callback.execute(pmethod, value, root, null,
								null);
					}
				});
	}

	private static Object reflexConstructors(
			Class<?> pojo,
			final ReflexCallBack<Constructor<?>[], Class<?>, Class<?>, Object> callback) {
		Assert.notNull(pojo);
		Assert.notNull(callback);

		return reflexGeneration(pojo,
				new ReflexCallBack<Class<?>, Class<?>, Class<?>, Object>() {
					@Override
					public Object execute(Class<?> value, Class<?> previous,
							Class<?> root, String name, String key)
							throws SecurityException, IllegalArgumentException,
							NoSuchMethodException, IllegalAccessException,
							InvocationTargetException {
						Constructor<?>[] pmethod = value.getConstructors();
						return callback.execute(pmethod, value, root, null,
								null);
					}
				});
	}

	public static <T extends Annotation> void reflexAnnotation(
			final Class<?> pojo, final Class<T> annotationClass,
			final ReflexCallBack<T, Object, Class<?>, Object> callback) {
		if (Validate.isNull(pojo) || Validate.isNull(annotationClass)
				|| Validate.isInstanceOf(Method.class, pojo)
				|| Validate.isInstanceOf(Field.class, pojo)
				|| Validate.isInstanceOf(Constructor.class, pojo)
				|| Validate.isInstanceOf(Package.class, pojo)
				|| Validate.isInstanceOf(Annotation.class, pojo)) {
			return;
		}

		Target target = annotationClass.getAnnotation(Target.class);

		for (ElementType type : target.value()) {
			switch (type) {
			case METHOD:
				reflexMethods(
						pojo,
						new ReflexCallBack<Method[], Class<?>, Class<?>, Object>() {
							@Override
							public Object execute(Method[] value,
									Class<?> previous, Class<?> root,
									String name, String key)
									throws SecurityException,
									IllegalArgumentException,
									NoSuchMethodException,
									IllegalAccessException,
									InvocationTargetException {
								for (Method m : value) {
									T annotation = m
											.getAnnotation(annotationClass);
									if (annotation != null) {
										callback.execute(annotation, m, root,
												name, key);
									}
								}
								throw new NoSuchAnnotationException();
							}
						});
				break;

			case TYPE:
				reflexGeneration(
						pojo,
						new ReflexCallBack<Class<?>, Class<?>, Class<?>, Object>() {
							@Override
							public Object execute(Class<?> value,
									Class<?> previous, Class<?> root,
									String name, String key)
									throws SecurityException,
									IllegalArgumentException,
									NoSuchMethodException,
									IllegalAccessException,
									InvocationTargetException {
								T annotation = value
										.getAnnotation(annotationClass);
								callback.execute(annotation, value, root, name,
										key);
								throw new NoSuchAnnotationException();
							}
						});
				break;
			case CONSTRUCTOR:
				reflexConstructors(
						pojo,
						new ReflexCallBack<Constructor<?>[], Class<?>, Class<?>, Object>() {
							@Override
							public Object execute(Constructor<?>[] value,
									Class<?> previous, Class<?> root,
									String name, String key)
									throws SecurityException,
									IllegalArgumentException,
									NoSuchMethodException,
									IllegalAccessException,
									InvocationTargetException {
								for (Constructor<?> m : value) {
									T annotation = m
											.getAnnotation(annotationClass);
									if (annotation != null) {
										callback.execute(annotation, m, root,
												name, key);
									}
								}
								throw new NoSuchAnnotationException();
							}
						});
				break;
			case FIELD:
				reflexFields(
						pojo,
						new ReflexCallBack<Field[], Class<?>, Class<?>, Object>() {
							@Override
							public Object execute(Field[] value,
									Class<?> previous, Class<?> root,
									String name, String key)
									throws SecurityException,
									IllegalArgumentException,
									NoSuchMethodException,
									IllegalAccessException,
									InvocationTargetException {
								for (Field m : value) {
									T annotation = m
											.getAnnotation(annotationClass);
									if (annotation != null) {
										callback.execute(annotation, m, root,
												name, key);
									}
								}
								throw new NoSuchAnnotationException();
							}
						});
				break;
			case PACKAGE:
				reflexPackage(
						pojo,
						new ReflexCallBack<Package, Class<?>, Class<?>, Object>() {
							@Override
							public Object execute(Package value,
									Class<?> previous, Class<?> root,
									String name, String key)
									throws SecurityException,
									IllegalArgumentException,
									NoSuchMethodException,
									IllegalAccessException,
									InvocationTargetException {
								T annotation = value
										.getAnnotation(annotationClass);
								callback.execute(annotation, value, root, name,
										key);
								throw new NoSuchAnnotationException();
							}
						});
				break;
			case ANNOTATION_TYPE:
			case PARAMETER:
			case LOCAL_VARIABLE:
			default:
				break;
			}
		}
	}
}
