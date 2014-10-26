package com.atomunion.core.util;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import java.util.Collection;
import java.util.Map;

/**
 * @author lico
 * @version 1.0.000 At Apr 10, 2013
 */
public abstract class Validate {

	public static boolean isTrue(boolean expression) {
		return expression;
	}

	public static boolean isNull(Object object) {
		return object == null;
	}

	public static boolean notNull(Object object) {
		return object != null;
	}

	public static boolean hasLength(String text) {
		return !StringUtils.isEmpty(text);
	}

	public static boolean hasText(String text) {
		return !StringUtils.isBlank(text);
	}

	public static boolean contain(String textToSearch, String substring) {
		return StringUtils.isNotEmpty(textToSearch)
				&& StringUtils.isNotEmpty(substring)
				&& textToSearch.indexOf(substring) != -1;
	}

	public static boolean caseSensitiveContain(String textToSearch,
			String substring) {
		return StringUtils.isNotEmpty(textToSearch)
				&& StringUtils.isNotEmpty(substring)
				&& textToSearch.toUpperCase().indexOf(substring.toUpperCase()) != -1;
	}

	public static boolean noNullElements(Object[] array) {
		if (array != null) {
			for (int i = 0; i < array.length; i++) {
				if (array[i] == null) {
					return false;
				}
			}
		}
		return true;
	}

	public static boolean notEmpty(Object[] array) {
		return !ArrayUtils.isEmpty(array);
	}

	public static boolean notEmpty(Collection<?> collection) {
		return !CollectionUtils.isEmpty(collection);
	}

	public static boolean notEmpty(Map<?, ?> map) {
		return !MapUtils.isEmpty(map);
	}

	public static boolean isInstanceOf(Class<?> type, Object obj) {
		if (notNull(type) && type.isInstance(obj)) {
			return true;
		}
		return false;
	}

	public static boolean isAssignable(Class<?> superType, Class<?> subType) {
		if (isNull(superType) || isNull(subType)
				|| !superType.isAssignableFrom(subType)) {
			return false;
		}
		return true;
	}

}
