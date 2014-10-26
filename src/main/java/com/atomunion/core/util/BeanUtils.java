package com.atomunion.core.util;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.atomunion.core.reflex.NoSuchGetterMethodException;
import com.atomunion.core.reflex.Reflex;

/**
 * @author lico
 * @version 1.0.001 At Oct 21, 2013 3:20:14 PM
 */
public class BeanUtils extends org.apache.commons.beanutils.BeanUtils {
	private static Object convert(Object obj) {
		return obj;
	}

	public void copyProperties(Object dest, Object orig, String map, String key) {
		/**
		 * TODO
		 */
	}

	public static void copyProperties(Map<String, Object> container, String containerKey,
			Object orig, String[] subKeyNames) throws IllegalArgumentException, SecurityException,
			IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		copyProperties(container, containerKey, orig, subKeyNames, subKeyNames);
	}

	public static void copyProperties(Map<String, Object> container, String containerKey,
			Object orig, String[] subMappingNames, String[] subKeyNames)
			throws IllegalArgumentException, SecurityException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException {
		if (Validate.isNull(container) || Validate.isNull(containerKey)) {
			throw new IllegalArgumentException(
					"you give me a illegal container or illegal containerKey.");
		}
		if (Validate.notNull(orig) && Validate.notEmpty(subKeyNames)) {
			if (Validate.isInstanceOf(Collection.class, orig)
					|| Validate.isInstanceOf(Object[].class, orig)) {

				Object[] objs = null;
				if (Validate.isInstanceOf(Collection.class, orig)) {
					objs = new Object[((Collection<Object>) orig).size()];
					objs = ((Collection<Object>) orig).toArray(objs);
				} else {
					objs = (Object[]) orig;
				}

				List<Map<String, Object>> list = null;
				if (Validate.isNull(container.get(containerKey))) {
					list = new ArrayList<Map<String, Object>>();
					container.put(containerKey, list);
				} else {
					list = (List<Map<String, Object>>) container.get(containerKey);
				}

				for (Object obj : objs) {
					list.add(covertProperties(obj, subKeyNames, subMappingNames));
				}
			} else {
				Map<String, Object> map = null;
				if (Validate.isNull(container.get(containerKey))) {
					map = new HashMap<String, Object>();
					container.put(containerKey, map);
				} else {
					map = (Map<String, Object>) container.get(containerKey);
				}
				map.putAll(covertProperties(orig, subKeyNames, subMappingNames));
			}
		}

	}

	private static Map<String, Object> covertProperties(Object pojo, String[] subMapping,
			String[] subConfig) throws IllegalArgumentException, SecurityException,
			IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Map<String, Object> container = null;
		if (Validate.notNull(pojo) && Validate.notNull(subMapping)) {
			container = new HashMap<String, Object>();
			for (int i = 0; i < subMapping.length; i++) {
				((Map<String, Object>) container).put(
						(Validate.notNull(subConfig) && subConfig.length > i) ? subConfig[i]
								: subMapping[i], convert(Reflex.reflexGetter(pojo, subMapping[i],
								null, Reflex.LOOSE)));
			}
		} else {
			throw new NoSuchGetterMethodException("can't reflex object \"" + pojo
					+ "\" using the key \"" + subMapping + "\"");
		}
		return container;
	}

	public static void copyProperties(Map<String, Object> container, String containerKey,
			Object orig, String key, String[] subKeyNames) throws IllegalArgumentException,
			SecurityException, IllegalAccessException, InvocationTargetException,
			NoSuchMethodException {
		copyProperties(container, containerKey, orig, key, key, subKeyNames, subKeyNames);
	}

	public static void copyProperties(Map<String, Object> container, String containerKey,
			Object orig, String mapping, String key, String[] subMappingNames, String[] subKeyNames)
			throws IllegalArgumentException, SecurityException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException {

		if (Validate.isNull(container) || Validate.isNull(containerKey)) {
			throw new IllegalArgumentException(
					"you give me a illegal container or illegal containerKey.");
		}

		String[] keys = Reflex.splitKey(key);
		String[] mappings = Reflex.splitKey(mapping);

		copyProperties(container, containerKey, null, orig, keys, mappings, subKeyNames,
				subMappingNames, 0);

	}

	public static void recursionCopyProperties(Map<String, Object> container, String containerKey,
			Object orig, String key, String[] subKeyNames) throws IllegalArgumentException,
			SecurityException, IllegalAccessException, InvocationTargetException,
			NoSuchMethodException {
		recursionCopyProperties(container, containerKey, orig, key, key, subKeyNames, subKeyNames);
	}

	public static void recursionCopyProperties(Map<String, Object> container, String containerKey,
			Object orig, String mapping, String key, String[] subMappingNames, String[] subKeyNames)
			throws IllegalArgumentException, SecurityException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException {

		if (Validate.isNull(container) || Validate.isNull(containerKey)) {
			throw new IllegalArgumentException(
					"you give me a illegal container or illegal containerKey.");
		}
		recursionCopyProperties(container, containerKey, null, orig, key, mapping, subKeyNames,
				subMappingNames, 0);

	}

	private static void recursionCopyProperties(Map<String, Object> c, String key,
			Object container, Object pojo, String keys, String configs, String[] subMapping,
			String[] subConfig, int j) throws IllegalArgumentException, SecurityException,
			IllegalAccessException, InvocationTargetException, NoSuchMethodException {

		if (Validate.isNull(pojo)) {
			return;
		}
		if (Validate.isNull(container)) {

			container = c.get(key);
			if (Validate.isNull(container)) {
				if (Validate.isInstanceOf(Collection.class, pojo)
						|| Validate.isInstanceOf(Object[].class, pojo)) {
					container = new ArrayList<Object>();
				} else {
					container = new HashMap<String, Object>();
				}
				c.put(key, container);
			}
		}

		// TODO 判断集合类型 Validate.isInstanceOf(Object[].class, value)
		if (Validate.isInstanceOf(Collection.class, pojo)
				|| Validate.isInstanceOf(Object[].class, pojo)) {

			Object[] objs = null;
			if (Validate.isInstanceOf(Collection.class, pojo)) {
				objs = new Object[((Collection<Object>) pojo).size()];
				objs = ((Collection<Object>) pojo).toArray(objs);
			} else {
				objs = (Object[]) pojo;
			}

			for (Object obj : objs) {
				Object map = (((List<Object>) container).size() > j) ? ((List<Object>) container)
						.get(j) : null;
				if (Validate.isNull(map)) {
					if (Validate.isInstanceOf(Collection.class, obj)
							|| Validate.isInstanceOf(Object[].class, obj)) {
						map = new ArrayList<Object>();
					} else {
						map = new HashMap<String, Object>();
					}
					((Collection) container).add(map);
				}

				recursionCopyProperties(c, key, map, obj, keys, configs, subMapping, subConfig, j++);
			}
		} else {

			Object value = null;
			if (Validate.isInstanceOf(Map.class, pojo)) {
				value = ((Map) pojo).get(keys);
			} else {
				Map<String, String> methodsInvoke = new HashMap<String, String>();
				methodsInvoke.put("getter",
						"get" + keys.substring(0, 1).toUpperCase() + keys.substring(1));
				value = Reflex.reflexGetter(pojo, keys, methodsInvoke);
			}

			((Map<String, Object>) container).putAll(covertProperties(pojo, subMapping, subConfig));

			Object map2 = ((Map<String, Object>) container).get(configs);
			if (Validate.isNull(map2)) {
				if (Validate.isInstanceOf(Collection.class, value)
						|| Validate.isInstanceOf(Object[].class, value)) {
					map2 = new ArrayList<Object>();
				} else {
					map2 = new HashMap<String, Object>();
				}
				((Map<String, Object>) container).put(configs, map2);
			}
			recursionCopyProperties(c, key, map2, value, keys, configs, subMapping, subConfig, 0);
		}

	}

	private static void copyProperties(Map<String, Object> c, String key, Object container,
			Object pojo, String[] keys, String[] configs, String[] subMapping, String[] subConfig,
			int j) throws IllegalArgumentException, SecurityException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException {

		if (Validate.notNull(configs) && Validate.notNull(keys) && configs.length < keys.length) {
			throw new IllegalArgumentException(
					"please comfim you subMappingName and subConfigName.");
		}

		if (Validate.isNull(container)) {

			container = c.get(key);
			if (Validate.isNull(container)) {
				if (Validate.isInstanceOf(Collection.class, pojo)
						|| Validate.isInstanceOf(Object[].class, pojo)) {
					container = new ArrayList<Object>();
				} else {
					container = new HashMap<String, Object>();
				}
				c.put(key, container);
			}
		}

		// TODO 判断集合类型 Validate.isInstanceOf(Object[].class, value)
		if (Validate.isInstanceOf(Collection.class, pojo)
				|| Validate.isInstanceOf(Object[].class, pojo)) {
			Object[] objs = null;
			if (Validate.isInstanceOf(Collection.class, pojo)) {
				objs = new Object[((Collection<Object>) pojo).size()];
				objs = ((Collection<Object>) pojo).toArray(objs);
			} else {
				objs = (Object[]) pojo;
			}

			for (Object obj : objs) {
				Object map = (((List<Object>) container).size() > j) ? ((List<Object>) container)
						.get(j) : null;
				if (Validate.isNull(map)) {
					if (Validate.isInstanceOf(Collection.class, obj)
							|| Validate.isInstanceOf(Object[].class, obj)) {
						map = new ArrayList<Object>();
					} else {
						map = new HashMap<String, Object>();
					}
					((Collection) container).add(map);
				}

				String[] ks = null, cs = null;
				if (Validate.notEmpty(keys)) {
					ks = keys;
					cs = configs;
				}

				copyProperties(c, key, map, obj, ks, cs, subMapping, subConfig, j++);
			}
		} else {
			if (Validate.notEmpty(keys)) {
				String[] ks = null, cs = null;
				if (keys.length > 1) {
					ks = Arrays.copyOfRange(keys, 1, keys.length);
					cs = Arrays.copyOfRange(configs, 1, configs.length);
				}
				String name = keys[0].trim();
				String mapName = configs[0].trim();
				Object value = null;
				if (Validate.isInstanceOf(Map.class, pojo)) {
					value = ((Map) pojo).get(name);
				} else {
					Map<String, String> methodsInvoke = new HashMap<String, String>();
					methodsInvoke.put("getter",
							"get" + name.substring(0, 1).toUpperCase() + name.substring(1));
					value = Reflex.reflexGetter(pojo, name, methodsInvoke);
				}

				Object map2 = ((Map<String, Object>) container).get(mapName);
				if (Validate.isNull(map2)) {
					if (Validate.isInstanceOf(Collection.class, value)
							|| Validate.isInstanceOf(Object[].class, value)) {
						map2 = new ArrayList<Object>();
					} else {
						map2 = new HashMap<String, Object>();
					}
					((Map<String, Object>) container).put(mapName, map2);
				}
				copyProperties(c, key, map2, value, ks, cs, subMapping, subConfig, 0);
			} else {
				((Map<String, Object>) container).putAll(covertProperties(pojo, subMapping,
						subConfig));
			}
		}
	}

}
