package madrid.apiFactory.core.util.utils;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.*;
import java.io.Serializable;
import java.lang.reflect.*;
import java.math.BigDecimal;
import java.sql.Clob;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.*;

public class BeanUtils extends org.apache.commons.beanutils.BeanUtils {
	private static final Logger log = LoggerFactory.getLogger(BeanUtils.class);
	//private static final String NUMBER_FORMAT = "############0.00";
	private static final NumberFormat nf = new DecimalFormat("############0.00");

	public static Boolean valueOfBoolean(Object obj) {
		return Boolean.valueOf(String.valueOf(obj));
	}

	public static Object cloneBean(Object bean) {
		if (bean == null)
			return null;
		Object target = null;
		if ((bean instanceof Cloneable)) {
			target = safeInvokeMethod(bean, "clone", new Object[0]);
		}
		if ((target == null) && ((bean instanceof Serializable))) {
			target = SerializationUtils.clone((Serializable) bean);
		}
		if (target == null) {
			try {
				target = org.apache.commons.beanutils.BeanUtils.cloneBean(bean);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return target;
	}

	public static Object safeInvokeMethod(Object context, String methodName, Object[] args) {
		try {
			return invokeMethod(context, methodName, args);
		} catch (Exception e) {
		}
		return null;
	}

	public static Object invokeMethod(Object context, String methodName, Object[] args) {
		if (context == null) {
			throw new IllegalArgumentException("invoke conext is null");
		}
		Object result = null;
		try {
			Method method = getMethod(context, methodName, args);
			result = method.invoke(context, args);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return result;
	}

	public static Method getMethod(Object context, String methodName, Object[] args)
			throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		Method method = null;
		Class<?> clazz = (context instanceof Class) ? (Class<?>) context : context.getClass();
		if ((args != null) && (args.length > 0)) {
			List<Class<?>> typeList = new ArrayList<Class<?>>(args.length);
			for (Object o : args) {
				if (o == null)
					typeList.add(null);
				else {
					typeList.add(o.getClass());
				}
			}
			Method[] ms = clazz.getMethods();

			for (Method m : ms)
				if (m.getDeclaringClass() != Class.class) {
					Class<?>[] paramTypes = m.getParameterTypes();
					if ((methodName.equals(m.getName())) && (paramTypes.length == args.length)) {
						int i = 0;
						for (Class<?> c : paramTypes) {
							Class<?> realParamType = (Class<?>) typeList.get(i++);
							if ((realParamType != null) && (!c.isAssignableFrom(realParamType))) {
								break;
							}
						}
						method = m;
						break;
					}
				}
		} else {
			method = clazz.getMethod(methodName, new Class[0]);
		}
		return method;
	}

	public static Object convert(String value, Class<?> clazz) {
		return convert(clazz, value);
	}

	public static PropertyDescriptor getPropertyDescriptor(Object bean, String property) {
		try {
			return PropertyUtils.getPropertyDescriptor(bean, property);
		} catch (Exception e) {
		}
		throw new RuntimeException();
	}

	public static void copyProperties(Object dest, Object src, boolean ignoreNull) {
		try {
			Set<String> fromPropertys = describe(src).keySet();
			Set<String> toPropertys = describe(dest).keySet();
			for (Iterator<String> localIterator = fromPropertys.iterator(); localIterator.hasNext();) {
				Object oPropertyName = localIterator.next();
				String propertyName = oPropertyName.toString();
				if ((!propertyName.equals("class")) && (toPropertys.contains(propertyName))) {
					Object valueToBeCopied = PropertyUtils.getNestedProperty(src, propertyName);
					Class<?> srcDataType = PropertyUtils.getPropertyDescriptor(src, propertyName).getPropertyType();
					Class<?> destDataType = PropertyUtils.getPropertyDescriptor(dest, propertyName).getPropertyType();
					if (valueToBeCopied == null) {
						if (!ignoreNull) {
							PropertyUtils.setNestedProperty(dest, propertyName, null);
						}

					} else if (srcDataType == destDataType) {
						PropertyUtils.setNestedProperty(dest, propertyName, valueToBeCopied);
					} else {
						if (srcDataType == String.class)
							valueToBeCopied = ConvertUtils.convert((String) valueToBeCopied, destDataType);
						else if (destDataType == String.class)
							valueToBeCopied = ConvertUtils.convert(valueToBeCopied);
						else if ((ClassUtils.isPrimitiveOrWrapper(srcDataType))
								|| (Number.class.isAssignableFrom(destDataType))) {
							valueToBeCopied = ConvertUtils.convert(valueToBeCopied.toString(), destDataType);
						}
						if (valueToBeCopied == null) {
							PropertyUtils.setNestedProperty(dest, propertyName, null);
						} else if (destDataType.isInstance(valueToBeCopied))
							PropertyUtils.setNestedProperty(dest, propertyName, valueToBeCopied);
						else
							log.error("属性[" + propertyName + "]源数据类型[" + valueToBeCopied.getClass().getName()
									+ "]和目标数据类型[" + destDataType.getName() + "]不匹配,忽略此次赋值");
					}
				}
			}
		} catch (Exception e) {
			throw new RuntimeException("复制属性出错 ...", e);
		}
	}

	public static void copyProperties(Object dest, Object src, boolean ignoreNull, String[] forceInjectProps) {
		try {
			Set<String> fromPropertys = describe(src).keySet();
			Set<String> toPropertys = describe(dest).keySet();
			String includeProps = StringUtils.join(forceInjectProps, ",");
			for (Iterator<String> localIterator = fromPropertys.iterator(); localIterator.hasNext();) {
				Object oPropertyName = localIterator.next();
				String propertyName = oPropertyName.toString();
				if ((!propertyName.equals("class")) && (toPropertys.contains(propertyName))) {
					Object valueToBeCopied = PropertyUtils.getNestedProperty(src, propertyName);
					Class<?> srcDataType = PropertyUtils.getPropertyDescriptor(src, propertyName).getPropertyType();
					Class<?> destDataType = PropertyUtils.getPropertyDescriptor(dest, propertyName).getPropertyType();
					if (valueToBeCopied == null) {
						if ((!ignoreNull) || (includeProps.contains(propertyName))) {
							PropertyUtils.setNestedProperty(dest, propertyName, null);
						}

					} else if (srcDataType == destDataType) {
						PropertyUtils.setNestedProperty(dest, propertyName, valueToBeCopied);
					} else {
						if (srcDataType == String.class)
							valueToBeCopied = ConvertUtils.convert((String) valueToBeCopied, destDataType);
						else if (destDataType == String.class)
							valueToBeCopied = ConvertUtils.convert(valueToBeCopied);
						else if ((ClassUtils.isPrimitiveOrWrapper(srcDataType))
								|| (Number.class.isAssignableFrom(destDataType))) {
							valueToBeCopied = ConvertUtils.convert(valueToBeCopied.toString(), destDataType);
						}
						if (valueToBeCopied == null) {
							if ((!ignoreNull) || (includeProps.contains(propertyName))) {
								PropertyUtils.setNestedProperty(dest, propertyName, null);
							}

						} else if (destDataType.isInstance(valueToBeCopied))
							PropertyUtils.setNestedProperty(dest, propertyName, valueToBeCopied);
						else
							log.error("属性[" + propertyName + "]源数据类型[" + valueToBeCopied.getClass().getName()
									+ "]和目标数据类型[" + destDataType.getName() + "]不匹配,忽略此次赋值");
					}
				}
			}
		} catch (Exception e) {
			throw new RuntimeException("复制属性出错 ...", e);
		}
	}

	public static void copyProperties(Object dest, Object src, boolean ignoreNull, String ignoredPropertis) {
		try {
			Set<String> fromPropertys = describe(src).keySet();
			Set<String> toPropertys = describe(dest).keySet();
			for (Iterator<String> localIterator = fromPropertys.iterator(); localIterator.hasNext();) {
				Object oPropertyName = localIterator.next();
				String propertyName = oPropertyName.toString();
				if ((!propertyName.equals("class")) && (!ignoredPropertis.contains(propertyName))
						&& (toPropertys.contains(propertyName))) {
					Object valueToBeCopied = PropertyUtils.getNestedProperty(src, propertyName);
					Class<?> srcDataType = PropertyUtils.getPropertyDescriptor(src, propertyName).getPropertyType();
					Class<?> destDataType = PropertyUtils.getPropertyDescriptor(dest, propertyName).getPropertyType();
					if (valueToBeCopied == null) {
						if (!ignoreNull) {
							PropertyUtils.setNestedProperty(dest, propertyName, null);
						}

					} else if (srcDataType == destDataType) {
						PropertyUtils.setNestedProperty(dest, propertyName, valueToBeCopied);
					} else {
						if (srcDataType == String.class)
							valueToBeCopied = ConvertUtils.convert((String) valueToBeCopied, destDataType);
						else if (destDataType == String.class)
							valueToBeCopied = ConvertUtils.convert(valueToBeCopied);
						else if ((ClassUtils.isPrimitiveOrWrapper(srcDataType))
								|| (Number.class.isAssignableFrom(destDataType))) {
							valueToBeCopied = ConvertUtils.convert(valueToBeCopied.toString(), destDataType);
						}
						if (valueToBeCopied == null) {
							PropertyUtils.setNestedProperty(dest, propertyName, null);
						} else if (destDataType.isInstance(valueToBeCopied))
							PropertyUtils.setNestedProperty(dest, propertyName, valueToBeCopied);
						else
							log.error("属性[" + propertyName + "]源数据类型[" + valueToBeCopied.getClass().getName()
									+ "]和目标数据类型[" + destDataType.getName() + "]不匹配,忽略此次赋值");
					}
				}
			}
		} catch (Exception e) {
			throw new RuntimeException("复制属性出错 ...", e);
		}
	}

	public static void copyProperties(Object dest, Object src)
			throws IllegalAccessException, InvocationTargetException {
		copyProperties(dest, src, true);
	}

	public static Integer getInt(Object src, Integer defaultValue) {
		if ((src == null) || (src.toString().trim().equals(""))) {
			return defaultValue;
		}
		Integer result = null;
		try {
			result = Integer.valueOf(src.toString());
		} catch (Exception e) {
			result = defaultValue;
		}

		return result;
	}

	public static String getString(Object src, String defaultValue) {
		return src == null ? defaultValue : src.toString();
	}

	public static Long getLong(Object src, Long defaultValue) {
		if ((src == null) || (src.toString().trim().equals(""))) {
			return defaultValue;
		}
		Long result = null;
		try {
			result = Long.valueOf(src.toString());
		} catch (Exception e) {
			result = defaultValue;
		}

		return result;
	}

	public static Float getFloat(Object src, Float defaultValue) {
		if ((src == null) || (src.toString().trim().equals(""))) {
			return defaultValue;
		}
		Float result = null;
		try {
			result = Float.valueOf(src.toString());
		} catch (Exception e) {
			result = defaultValue;
		}
		return result;
	}

	public static Double getDouble(Object src, Double defaultValue) {
		if ((src == null) || (src.toString().trim().equals(""))) {
			return defaultValue;
		}
		Double result = null;
		try {
			result = Double.valueOf(src.toString());
		} catch (Exception e) {
			result = defaultValue;
		}
		return result;
	}

	public static Integer getInt(Object src) {
		return getInt(src, Integer.valueOf(0));
	}

	public static Long getLong(Object src) {
		return getLong(src, Long.valueOf(0L));
	}

	public static Float getFloat(Object src) {
		return getFloat(src, Float.valueOf(0.0F));
	}

	public static Double getDouble(Object src) {
		return getDouble(src, Double.valueOf(0.0D));
	}

	public static String getNotNullString(Object src) {
		return src == null ? "" : String.valueOf(src);
	}

	public static String getProperty(Object bean, String name) {
		try {
			return BeanUtilsBean.getInstance().getProperty(bean, name);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("rawtypes")
	public static Object getParam(Object context, String paramName) {
		Object paramValue = null;
		if (context == null)
			log.warn("paramBean is null ...");
		else if ((context instanceof Map))
			paramValue = ((Map) context).get(paramName);
		else if (context.getClass().isArray()) {
			if ((NumberUtils.isNumber(paramName))
					&& (((Object[]) context).length > Integer.valueOf(paramName).intValue() - 1))
				paramValue = ((Object[]) context)[(Integer.valueOf(paramName).intValue() - 1)];
		} else {
			try {
				paramValue = getRawProperty(context, paramName);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return paramValue;
	}

	/*public static Map<String, Object> describe(Object bean)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		if (bean == null) {
			return Collections.emptyMap();
		}

		if (log.isDebugEnabled()) {
			log.debug("Describing bean: " + bean.getClass().getName());
		}

		Map<String, Object> description = new HashMap<String, Object>();
		if ((bean instanceof DynaBean)) {
			DynaProperty[] descriptors = ((DynaBean) bean).getDynaClass().getDynaProperties();
			for (int i = 0; i < descriptors.length; i++) {
				String name = descriptors[i].getName();
				if (!"class".equals(name))
					description.put(name, getRawProperty(bean, name));
			}
		} else {
			PropertyDescriptor[] descriptors = PropertyUtils.getPropertyDescriptors(bean);
			for (int i = 0; i < descriptors.length; i++) {
				String name = descriptors[i].getName();
				if ((!"class".equals(name)) && (descriptors[i].getReadMethod() != null))
					description.put(name, getRawProperty(bean, name));
			}
		}
		return description;
	}*/

	public static <T> T map2Object(Map<String, Object> map, T target)
			throws IllegalAccessException, InvocationTargetException {
		populate(target, map);
		return target;
	}

	public static <T> T map2ObjectIgnoreCase(Map<String, Object> map, T target) {
		if ((target == null) || (map == null)) {
			return null;
		}
		Iterator<String> names = map.keySet().iterator();
		PropertyDescriptor[] descriptors = PropertyUtils.getPropertyDescriptors(target);
		while (names.hasNext()) {
			String name = (String) names.next();
			if (!StringUtils.isEmpty(name)) {
				for (PropertyDescriptor des : descriptors)
					if ((des.getName().equalsIgnoreCase(name))
							|| (des.getName().equalsIgnoreCase(name.replaceAll("_", "")))) {
						Object value = map.get(name);
						if (value == null) {
							setProperty(target, des.getName(), null);
							break;
						}
						Class<?> propertyType = des.getPropertyType();
						if (propertyType == value.getClass()) {
							setProperty(target, des.getName(), value);
							break;
						}
						if ((value instanceof String)) {
							setProperty(target, des.getName(), ConvertUtils.convert((String) value, propertyType));
							break;
						}
						if (propertyType == String.class) {
							setProperty(target, des.getName(), ConvertUtils.convert(value));
							break;
						}
						if ((ClassUtils.isPrimitiveOrWrapper(propertyType)) || ((value instanceof Number))) {
							setProperty(target, des.getName(),
									ConvertUtils.convert(String.valueOf(value), propertyType));
							break;
						}
						if ((propertyType == Calendar.class) || ((value instanceof Date))) {
							Calendar c = Calendar.getInstance();
							c.setTime((Date) value);
							setProperty(target, des.getName(), c);
							break;
						}
						if ((propertyType == Date.class) || ((value instanceof Calendar))) {
							setProperty(target, des.getName(), ((Calendar) value).getTime());
							break;
						}
						log.warn("Map 中" + name + "的属性类型[" + value.getClass().getName() + "]和目标对象中的数据类型["
								+ propertyType.getName() + "]不匹配,忽略此次赋值...");

						break;
					}
			}
		}
		return target;
	}

	public static <T> T map2ObjectIgnoreCase(Map<String, Object> map, T target, String keyFilterPrefix)
			throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		if ((target == null) || (map == null)) {
			return null;
		}
		Map<String, Object> subMap = new HashMap<String, Object>();
		int prefixLengh = keyFilterPrefix == null ? 0 : keyFilterPrefix.length();
		for (Iterator<String> it = map.keySet().iterator(); it.hasNext();) {
			String key = (String) it.next();
			if (key.startsWith(keyFilterPrefix)) {
				subMap.put(key.substring(prefixLengh), map.get(key));
			}
		}
		map2ObjectIgnoreCase(subMap, target);
		return target;
	}

	public static String formatNumber(Object number) {
		if ((number != null) && ((number instanceof Number))) {
			return nf.format(number);
		}
		return "";
	}

	public static String convertToString(Object value) {
		if (value == null)
			return "";
		if ((value instanceof String))
			return (String) value;
		if ((value instanceof Date))
			return DateUtils.formatDate((Date) value);
		if ((value instanceof Double))
			return formatNumber(value);
		if ((value instanceof Clob)) {
			Clob v = (Clob) value;
			try {
				return v.getSubString(1L, (int) v.length());
			} catch (SQLException e) {
				log.debug("读取SQL的Clob字段时发生错误", e);
				throw new RuntimeException("读取SQL的Clob字段时发生错误", e);
			}
		}
		String result = String.valueOf(value);
		return result;
	}

	public static String join(Object[] array, String separator) {
		return StringUtils.join(array, separator == null ? "," : separator);
	}

	public static Long getNotNullLong(Long src) {
		return Long.valueOf(src == null ? 0L : src.longValue());
	}

	public static String filter(String value) {
		if ((value == null) || (value.length() == 0)) {
			return value;
		}

		StringBuffer result = null;
		String filtered = null;
		for (int i = 0; i < value.length(); i++) {
			filtered = null;
			switch (value.charAt(i)) {
			case '<':
				filtered = "&lt;";
				break;
			case '>':
				filtered = "&gt;";
			case '=':
			}

			if (result == null) {
				if (filtered != null) {
					result = new StringBuffer(value.length() + 50);
					if (i > 0) {
						result.append(value.substring(0, i));
					}
					result.append(filtered);
				}
			} else if (filtered == null)
				result.append(value.charAt(i));
			else {
				result.append(filtered);
			}
		}

		return result == null ? value : result.toString();
	}

	public static void setProperty(Object bean, String property, Object value) {
		try {
			BeanProperties beanPropeties = BeanProperties.forClass(bean.getClass());

			Class<?> type = null;
			Method method = beanPropeties.findSetterMethod(property, value);

			if (method != null)
				type = method.getParameterTypes()[0];
			else if (Character.isUpperCase(property.charAt(0))) {
				method = beanPropeties
						.findSetterMethod(Character.toLowerCase(property.charAt(0)) + property.substring(1), value);
			}

			if ((type != null) && (method != null)) {
				if (!Modifier.isPublic(method.getModifiers())) {
					method.setAccessible(true);
				}

				value = convert(type, value);

				method.invoke(bean, new Object[] { value });
			} else {
				if (log.isDebugEnabled()) {
					log.debug("no setter found for property '" + property + "' in " + bean.getClass().getName()
							+ " use Field to set value");
				}
				Field field = ClassUtils.getDeclaredField(bean.getClass(), property);
				if ((field != null) && (!field.isAccessible())) {
					field.setAccessible(true);
				}
				field.set(bean, value);
			}
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public static Object getRawProperty(Object bean, String property) {
		try {
			BeanProperties beanProperties = BeanProperties.forClass(bean.getClass());

			Method method = beanProperties.findGetterMethod(property);
			if (method != null)
				return method.invoke(bean, new Object[0]);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static <T> T convert(Class<T> type, Object value) {
		if (value == null)
			return null;
		if (type.isAssignableFrom(value.getClass())) {
			return (T) value;
		}
		if (((value instanceof String)) && (type != String.class)) {
			String string = (String) value;
			if ((type == Integer.TYPE) || (type == Integer.class)) {
				value = Integer.valueOf(Integer.parseInt(string));
			} else if ((type == Short.TYPE) || (type == Short.class)) {
				value = Short.valueOf(Short.parseShort(string));
			} else if ((type == Long.TYPE) || (type == Long.class)) {
				value = Long.valueOf(Long.parseLong(string));
			} else if ((type == Boolean.TYPE) || (type == Boolean.class)) {
				value = Boolean.valueOf(Boolean.parseBoolean(string));
			} else if ((type == Float.TYPE) || (type == Float.class)) {
				value = Float.valueOf(Float.parseFloat(string));
			} else if ((type == Double.TYPE) || (type == Double.class)) {
				value = Double.valueOf(Double.parseDouble(string));
			} else if ((type == Character.TYPE) || ((type == Character.class) && (string.length() == 0))) {
				value = Character.valueOf(string.charAt(0));
			} else if ((type == Byte.TYPE) || (type == Byte.class)) {
				value = Byte.valueOf(Byte.parseByte(string));
			} else if (type.isAssignableFrom(Date.class)) {
				value = parseDate(string);
				if (value == null) {
					throw new RuntimeException("invalid date string '" + string + "'");
				}
			}
		} else if (type == String.class) {
			if ((value instanceof Date))
				value = DateUtils.formatDate((Date) value);
			else
				value = value.toString();
		} else if ((type == BigDecimal.class) && (value.getClass() != BigDecimal.class)) {
			value = new BigDecimal(value.toString());
		} else if (type.isArray()) {
			try {
				String arrayClassName = getArrayClassName(type);
				Class<?> arrayClass = createClassByName(arrayClassName);
				value = convertArrayType((Object[]) value, arrayClass);
			} catch (ClassNotFoundException e) {
				throw new RuntimeException(e.getMessage(), e);
			}
		} else if (!type.equals(value.getClass())) {
			if ((type == Integer.TYPE) || (type == Integer.class))
				value = Integer.valueOf(Integer.parseInt(value.toString()));
			else if ((type == Short.TYPE) || (type == Short.class))
				value = Short.valueOf(Short.parseShort(value.toString()));
			else if ((type == Long.TYPE) || (type == Long.class))
				value = Long.valueOf(Long.parseLong(value.toString()));
			else if ((type == Boolean.TYPE) || (type == Boolean.class))
				value = Boolean.valueOf(Boolean.parseBoolean(value.toString()));
			else if ((type == Float.TYPE) || (type == Float.class))
				value = Float.valueOf(Float.parseFloat(value.toString()));
			else if ((type == Double.TYPE) || (type == Double.class)) {
				value = Double.valueOf(Double.parseDouble(value.toString()));
			}
		}

		return (T)value;
	}

	public static Method findMethod(Class<?> clazz, String methodName, Class<?>[] paramTypes) {
		try {
			return clazz.getMethod(methodName, paramTypes);
		} catch (NoSuchMethodException ex) {
		}
		return findDeclaredMethod(clazz, methodName, paramTypes);
	}

	public static Method findDeclaredMethod(Class<?> clazz, String methodName, Class<?>[] paramTypes) {
		try {
			return clazz.getDeclaredMethod(methodName, paramTypes);
		} catch (NoSuchMethodException ex) {
			if (clazz.getSuperclass() != null)
				return findDeclaredMethod(clazz.getSuperclass(), methodName, paramTypes);
		}
		return null;
	}

	public static Method findDeclaredMethodWithMinimalParameters(Class<?> clazz, String methodName)
			throws IllegalArgumentException {
		Method targetMethod = doFindMethodWithMinimalParameters(clazz.getDeclaredMethods(), methodName);
		if ((targetMethod == null) && (clazz.getSuperclass() != null)) {
			return findDeclaredMethodWithMinimalParameters(clazz.getSuperclass(), methodName);
		}
		return targetMethod;
	}

	private static Method doFindMethodWithMinimalParameters(Method[] methods, String methodName)
			throws IllegalArgumentException {
		Method targetMethod = null;
		int numMethodsFoundWithCurrentMinimumArgs = 0;
		for (int i = 0; i < methods.length; i++) {
			if (methods[i].getName().equals(methodName)) {
				int numParams = methods[i].getParameterTypes().length;
				if ((targetMethod == null) || (numParams < targetMethod.getParameterTypes().length)) {
					targetMethod = methods[i];
					numMethodsFoundWithCurrentMinimumArgs = 1;
				} else if (targetMethod.getParameterTypes().length == numParams) {
					numMethodsFoundWithCurrentMinimumArgs++;
				}
			}
		}

		if (numMethodsFoundWithCurrentMinimumArgs > 1) {
			throw new IllegalArgumentException("Cannot resolve method '" + methodName
					+ "' to a unique method. Attempted to resolve to overloaded method with "
					+ "the least number of parameters, but there were " + numMethodsFoundWithCurrentMinimumArgs
					+ " candidates.");
		}
		return targetMethod;
	}

	public static Date parseDate(String dateString) {
		Date d = toDate(dateString, "yyyy-MM-dd HH:mm:ss");
		if (d == null) {
			d = toDate(dateString, "yyyy-MM-dd");
		}
		return d;
	}

	private static Object convertArrayType(Object[] array, Class<?> converToType) {
		Object object = Array.newInstance(converToType, array.length);
		Object[] newArray = (Object[]) object;
		for (int i = 0; i < newArray.length; i++) {
			newArray[i] = array[i];
		}
		return object;
	}

	private static Class<?> createClassByName(String className) throws ClassNotFoundException {
		return ClassUtils.forName(className);
	}

	private static String getArrayClassName(Class<?> type) {
		String name = type.getName();
		return name.substring(2, name.length() - 1);
	}

	private static Date toDate(String date, String format) {
		if (date == null) {
			return null;
		}

		Date d = null;
		SimpleDateFormat formater = new SimpleDateFormat(format);
		try {
			ParsePosition pos = new ParsePosition(0);
			formater.setLenient(false);
			d = formater.parse(date, pos);
			if ((d != null) && (pos.getIndex() != date.length()))
				d = null;
		} catch (Exception e) {
			d = null;
		}
		return d;
	}

	@SuppressWarnings("unchecked")
	public static <T> T fillBean(Class<T> targetObject) {
		Object target = ClassUtils.newInstance(targetObject);
		List<Field> fields = ClassUtils.getDeclaredFields(targetObject);
		for (Field field : fields) {
			try {
				field.setAccessible(true);
				Object obj = field.get(target);
				if (obj == null)
					if (field.getType() == String.class) {
						if (field.getName().toLowerCase().startsWith("is"))
							field.set(target, "Y");
						else
							field.set(target, field.getName());
					} else if (field.getType() == Long.class)
						field.set(target, Long.valueOf(1L));
					else if (field.getType() == Integer.class)
						field.set(target, Integer.valueOf(1));
					else if (field.getType() == Date.class)
						field.set(target, new Date());
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return (T) target;
	}

	public static final class BeanProperties {
		private final BeanInfo beanInfo;
		private final Map<String, PropertyDescriptor> propertiyDescriptors;
		private final Map<String, Method> setterMethods;
		private final Map<String, Method> getterMethods;
		private static Map<Class<?>, BeanProperties> cache = new HashMap<Class<?>, BeanProperties>();

		public static BeanProperties forClass(Class<?> clazz) throws IntrospectionException {
			BeanProperties bean = (BeanProperties) cache.get(clazz);
			if (bean == null) {
				bean = new BeanProperties(clazz);
				cache.put(clazz, bean);
			}
			return bean;
		}

		private BeanProperties(Class<?> clazz) throws IntrospectionException {
			this.beanInfo = Introspector.getBeanInfo(clazz);
			this.propertiyDescriptors = new HashMap<String, PropertyDescriptor>();
			this.setterMethods = new HashMap<String, Method>();
			this.getterMethods = new HashMap<String, Method>();

			Class<?> classToFlush = clazz;
			do {
				Introspector.flushFromCaches(classToFlush);
				classToFlush = classToFlush.getSuperclass();
			} while (classToFlush != null);

			PropertyDescriptor[] pds = this.beanInfo.getPropertyDescriptors();
			for (int i = 0; i < pds.length; i++) {
				PropertyDescriptor pd = pds[i];
				this.propertiyDescriptors.put(pd.getName(), pd);
			}

			MethodDescriptor[] mds = this.beanInfo.getMethodDescriptors();
			for (int i = 0; i < mds.length; i++) {
				MethodDescriptor md = mds[i];
				Method method = md.getMethod();
				String name = md.getName();

				if ((name.startsWith("set")) && (name.length() > 3) && (method.getParameterTypes() != null)
						&& (method.getParameterTypes().length == 1)) {
					String property = name.substring(3);

					property = property.substring(0, 1).toLowerCase() + property.substring(1);

					if (this.setterMethods.containsKey(property)) {
						property = property + "-" + i;
						this.setterMethods.put(property, method);
					} else {
						this.setterMethods.put(property, method);
					}
				} else if ((method.getParameterTypes() == null) || (method.getParameterTypes().length == 0)) {
					String property = null;
					if ((name.startsWith("get")) && (name.length() > 3))
						property = name.substring(3);
					else if ((name.startsWith("is")) && (name.length() > 2)) {
						property = name.substring(2);
					}
					if (property != null) {
						property = property.substring(0, 1).toLowerCase() + property.substring(1);
						this.getterMethods.put(property, method);
					}
				}
			}
		}

		public Method findSetterMethod(String property) {
			return (Method) this.setterMethods.get(property);
		}

		public Method findSetterMethod(String property, Object value) {
			if (value == null)
				return findSetterMethod(property);

			Set<String> keys = this.setterMethods.keySet();
			for (String key : keys) {
				if ((key.equalsIgnoreCase(property)) || (key.startsWith(property + "-"))) {
					Method method = findSetterMethod(key);
					if (value.getClass().isAssignableFrom(method.getParameterTypes()[0])) {
						return method;
					}
				}
			}
			return findSetterMethod(property);
		}

		public Method findGetterMethod(String property) {
			return (Method) this.getterMethods.get(property);
		}
	}
}
