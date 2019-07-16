package madrid.apiFactory.core.util.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.*;
import java.math.BigDecimal;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

public class ClassUtils {
	private static final Logger log = LoggerFactory.getLogger(ClassUtils.class);
	public static final char PACKAGE_SEPARATOR_CHAR = '.';
	public static final String PACKAGE_SEPARATOR = String.valueOf('.');
	public static final char INNER_CLASS_SEPARATOR_CHAR = '$';
	public static final String INNER_CLASS_SEPARATOR = String.valueOf('$');

	private static final Map<Class<?>, Class<?>> primitiveWrapperTypeMap = new HashMap<Class<?>, Class<?>>(8);
	public static final String CLASS_SUFFIX = ".class";
	public static final String FILE_URL_PREFIX = "file:";
	public static final String URL_PROTOCOL_FILE = "file";
	public static final String URL_PROTOCOL_JAR = "jar";
	public static final String URL_PROTOCOL_ZIP = "zip";
	public static final String URL_PROTOCOL_WSJAR = "wsjar";
	public static final String JAR_URL_SEPARATOR = "!/";
	private static final String[] EMPTY_PARAMETER_NAMES;
	private static final Map<Class<?>, Object> defaults;
	private static final Set<Class<?>> types;
	private static final Pattern CLASS_PATTERN;

	static {
		primitiveWrapperTypeMap.put(Boolean.class, Boolean.TYPE);
		primitiveWrapperTypeMap.put(Byte.class, Byte.TYPE);
		primitiveWrapperTypeMap.put(Character.class, Character.TYPE);
		primitiveWrapperTypeMap.put(Double.class, Double.TYPE);
		primitiveWrapperTypeMap.put(Float.class, Float.TYPE);
		primitiveWrapperTypeMap.put(Integer.class, Integer.TYPE);
		primitiveWrapperTypeMap.put(Long.class, Long.TYPE);
		primitiveWrapperTypeMap.put(Short.class, Short.TYPE);

		EMPTY_PARAMETER_NAMES = new String[0];
		defaults = new HashMap<Class<?>, Object>();

		types = new HashSet<Class<?>>();

		CLASS_PATTERN = Pattern.compile("[.[^\\$]]+\\.class");

		types.add(Boolean.TYPE);
		types.add(Boolean.class);
		types.add(Character.TYPE);
		types.add(Character.class);
		types.add(Byte.TYPE);
		types.add(Byte.class);
		types.add(Short.TYPE);
		types.add(Short.class);
		types.add(Integer.TYPE);
		types.add(Integer.class);
		types.add(Long.TYPE);
		types.add(Long.class);
		types.add(Float.TYPE);
		types.add(Float.class);
		types.add(Double.TYPE);
		types.add(Double.class);
		types.add(String.class);
		types.add(BigDecimal.class);
	}

	public static boolean isPrimitiveOrWrapper(Class<?> clazz) {
		return (clazz.isPrimitive()) || (primitiveWrapperTypeMap.containsKey(clazz));
	}

	public static boolean isInstanceOf(Object target, String clazz) {
		Class<?> klazz = forName(clazz);
		boolean isInstance = false;
		if (target == null) {
			return isInstance;
		}
		Class<?> current = target.getClass();
		Class<?>[] interfaces = current.getInterfaces();

		for (Class<?> c : interfaces) {
			if (c.getName().equals(clazz)) {
				isInstance = true;
				break;
			}
		}
		while ((!isInstance) && (current != null)) {
			if (((klazz != null) && (klazz.isAssignableFrom(current))) || (current.getName().equals(clazz))) {
				isInstance = true;
				break;
			}
			current = current.getSuperclass();
		}
		return isInstance;
	}

	public static boolean isClassExists(String clazzName) {
		try {
			return forName(clazzName) != null;
		} catch (Exception e) {
		}
		return false;
	}

	public static boolean isPrimitiveTypeOrString(Class<?> clazz) {
		return (clazz == String.class) || (clazz.isPrimitive()) || (primitiveWrapperTypeMap.containsKey(clazz));
	}

	public static InputStream getResourceAsStream(String xmlFile) {
		ClassLoader cl = getDefaultClassLoader();
		if (cl == null) {
			cl = ClassUtils.class.getClassLoader();
		}
		if (xmlFile.startsWith("/")) {
			xmlFile = xmlFile.substring(1);
		}
		InputStream stream = cl.getResourceAsStream(xmlFile);

		return stream;
	}

	public static URL getResource(String xmlFile) {
		if (StringUtils.isEmpty(xmlFile)) {
			return null;
		}
		ClassLoader cl = getDefaultClassLoader();
		if (cl == null) {
			cl = ClassUtils.class.getClassLoader();
		}
		if (xmlFile.startsWith("/")) {
			xmlFile = xmlFile.substring(1);
		}
		URL url = cl.getResource(xmlFile);

		return url;
	}

	public static Field getDeclaredField(Class<?> beanClass, String fieldName) {
		List<Field> fields = getDeclaredFields(beanClass);
		Field targetField = null;
		for (Field f : fields) {
			if (f.getName().equals(fieldName)) {
				targetField = f;
			}
		}
		return targetField;
	}

	public static String getWapperClassShortName(Class<?> clazz) {
		String clazzName = "";
		if (clazz.isPrimitive()) {
			if (clazz == Boolean.TYPE)
				clazzName = "Boolean";
			else if (clazz == Byte.TYPE)
				clazzName = "Byte";
			else if (clazz == Character.TYPE)
				clazzName = "Character";
			else if (clazz == Double.TYPE)
				clazzName = "Double";
			else if (clazz == Float.TYPE)
				clazzName = "Float";
			else if (clazz == Integer.TYPE)
				clazzName = "Integer";
			else if (clazz == Long.TYPE)
				clazzName = "Long";
			else if (clazz == Short.TYPE)
				clazzName = "Short";
		} else {
			throw new RuntimeException("Primitive class is required ...");
		}

		return clazzName;
	}

	public static Object createArrayInstance(Class<?> componentType, int size) {
		if (String.class == componentType)
			return new String[size];
		if (Integer.class == componentType)
			return new Integer[size];
		if (Long.class == componentType)
			return new Long[size];
		if (Short.class == componentType)
			return new Short[size];
		if (Byte.class == componentType)
			return new Byte[size];
		if (Boolean.class == componentType)
			return new Boolean[size];
		if (Double.class == componentType)
			return new Double[size];
		if (Float.class == componentType) {
			return new Float[size];
		}
		return null;
	}

	public static ClassLoader getDefaultClassLoader() {
		ClassLoader cl = null;
		try {
			cl = Thread.currentThread().getContextClassLoader();
		} catch (Throwable ex) {
			log.debug("Cannot access thread context ClassLoader - falling back to system class loader", ex);
		}
		if (cl == null) {
			cl = ClassUtils.class.getClassLoader();
		}
		return cl;
	}

	public static Object newInstance(String className) {
		return newInstance(forName(className));
	}

	public static List<Method> getMethods(Class<?> clazz, String methodName) throws NoSuchMethodException {
		Method[] methods = clazz.getMethods();
		List<Method> methodsWithTheSameName = new ArrayList<Method>();
		for (int i = 0; i < methods.length; i++) {
			Method method = methods[i];
			if (method.getName().equals(methodName)) {
				methodsWithTheSameName.add(method);
			}
		}
		if (methodsWithTheSameName.size() == 0) {
			throw new NoSuchMethodException(methodName);
		}
		return methodsWithTheSameName;
	}

	public static List<String> getResources(ClassLoader classLoader, String packagePath, Pattern filter)
			throws IOException {
		List<String> result = new ArrayList<String>();
		if (classLoader == null) {
			classLoader = getDefaultClassLoader();
			if (classLoader == null) {
				classLoader = ClassUtils.class.getClassLoader();
			}
		}
		packagePath = packagePath.replace('.', '/') + "/";
		Enumeration<URL> urls = classLoader.getResources(packagePath);

		Set<String> paths = new HashSet<String>();
		while (urls.hasMoreElements()) {
			URL url = (URL) urls.nextElement();
			if (!paths.contains(url.getPath())) {
				paths.add(url.getPath());
				scanURL(packagePath, result, url, classLoader, filter);
			} else {
				log.info("url aleady scanned,ignore it");
			}
		}

		paths.clear();

		return result;
	}

	private static void scanURL(String packagePath, List<String> resources, URL url, ClassLoader classLoader,
			Pattern filter) throws IOException {
		if (isJarURL(url)) {
			log.debug("url is [jar or weblogic zip or websphere wsjar],scan the classes in jar file");

			URLConnection connection = url.openConnection();

			JarFile jarFile = null;
			if ((connection instanceof JarURLConnection)) {
				jarFile = ((JarURLConnection) connection).getJarFile();
			} else {
				String urlFile = url.getFile();
				int separatorIndex = urlFile.indexOf("!/");
				String jarFileUrl = urlFile.substring(0, separatorIndex);
				if (jarFileUrl.startsWith("file:")) {
					jarFileUrl = jarFileUrl.substring("file:".length());
				}

				log.debug("jar file url is " + jarFileUrl + ",create jar file");

				jarFile = new JarFile(jarFileUrl);
			}

			scanJarConnection(packagePath, resources, jarFile, filter);
		} else {
			Stack<Queued> queue = new Stack<Queued>();

			queue.push(new Queued(url, packagePath));

			while (!queue.isEmpty()) {
				Queued queued = (Queued) queue.pop();
				scan(queued._packagePath, queued._packageURL, resources, queue, classLoader, filter);
			}
		}
	}

	private static void scan(String packagePath, URL packageURL, List<String> classList, Stack<Queued> queue,
			ClassLoader classLoader, Pattern filter) throws IOException {
		InputStream is = null;
		try {
			is = new BufferedInputStream(packageURL.openStream());
		} catch (FileNotFoundException ex) {
			log.info("FielNotFoundException for url " + packagePath, ex);

			return;
		}

		Reader reader = new InputStreamReader(is);

		LineNumberReader lineReader = new LineNumberReader(reader);
		try {
			String line;
			String resourcePath;
			do {
				line = lineReader.readLine();

				if (line == null) {
					break;
				}
				resourcePath = packagePath + line;
				if ((filter == null) || (filter.matcher(resourcePath).matches())) {
					classList.add(resourcePath);
				}
			} while (!StringUtils.isEmpty(FileUtils.getFileExtName(resourcePath)));

			URL newURL = new URL(packageURL.toExternalForm() + line + "/");
			String newPackagePath = packagePath + line + "/";
			queue.push(new Queued(newURL, newPackagePath));
		} finally {
			if (lineReader != null)
				try {
					lineReader.close();
				} catch (IOException localIOException) {
				}
		}
	}

	private static void scanJarConnection(String packageName, List<String> resources, JarFile jarFile, Pattern filter)
			throws IOException {
		String packagePath = nameToPath(packageName);
		try {
			Enumeration<JarEntry> en = jarFile.entries();
			while (en.hasMoreElements()) {
				JarEntry je = (JarEntry) en.nextElement();
				String name = je.getName();
				if ((name.startsWith(packagePath)) && (filter.matcher(packagePath).matches()))
					resources.add(name);
			}
		} finally {
			if (jarFile != null)
				try {
					jarFile.close();
				} catch (IOException localIOException) {
				}
		}
	}

	private static String nameToPath(String className) {
		return className.replace('.', '/');
	}

	public static boolean isSimpleTypeOrString(Class<?> clazz) {
		return types.contains(clazz);
	}

	public static Object getDefaultValue(Class<?> primitiveType) {
		return defaults.get(primitiveType);
	}

	@SuppressWarnings("rawtypes")
	public static Object newInstance(Class<?> clazz) {
		try {
			if (clazz.isInterface()) {
				if (clazz.isAssignableFrom(ArrayList.class))
					return new ArrayList();
				if (clazz.isAssignableFrom(HashMap.class)) {
					return new HashMap();
				}
				return null;
			}
			if (Modifier.isAbstract(clazz.getModifiers())) {
				return null;
			}
			return clazz.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static ClassLoader getContextClassLoader() {
		return Thread.currentThread().getContextClassLoader();
	}

	public static String extractPackageName(String className) {
		String pkgName = "";

		int lastDotIndex = className.lastIndexOf(".");

		if (lastDotIndex > 0) {
			pkgName = className.substring(0, lastDotIndex);
		}

		return pkgName;
	}

	public static Class<?> forName(String name) {
		Class<?> clazz = null;
		Throwable ex = null;
		try {
			clazz = getContextClassLoader().loadClass(name);
		} catch (ClassNotFoundException e) {
			ex = e;
		}
		if (clazz == null) {
			try {
				clazz = ClassUtils.class.getClassLoader().loadClass(name);
			} catch (ClassNotFoundException e) {
				ex = e;
			}
		}
		if (clazz == null) {
			throw new RuntimeException("error while loading class " + name, ex);
		}
		return clazz;
	}

	public static String[] getMethodParameterNames(Method method) {
		Class<?>[] types = method.getParameterTypes();

		if (types.length > 0) {
			try {
				Class<?> clazz = method.getDeclaringClass();
				ClassReader cr = null;
				try {
					cr = new ClassReader(clazz);

					return cr.getParameterNames(method);
				} finally {
					if (cr != null) {
						cr.close();
						cr = null;
					}
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return EMPTY_PARAMETER_NAMES;
	}

	public static boolean isStatic(Method method) {
		return Modifier.isStatic(method.getModifiers());
	}

	public static Method findMethodIgnoreCase(Class<?> clazz, String name) throws IOException {
		while (!clazz.getName().equals("java.lang.Object")) {
			for (Method m : clazz.getDeclaredMethods()) {
				if ((m.getName().equalsIgnoreCase(name)) && (Modifier.isPublic(m.getModifiers()))) {
					return m;
				}
			}
			clazz = clazz.getSuperclass();
		}
		return null;
	}

	public static String findClassNameIgnoreCase(String pkgName, String name) {
		if (StringUtils.isEmpty(name)) {
			return null;
		}
		if (pkgName == null) {
			pkgName = extractPackageName(name);
		}
		int idx = 0;
		if ((idx = name.indexOf('.')) != -1) {
			name = name.substring(idx + 1, name.length());
		}
		name = name.replace('.', '/');
		if (!name.endsWith(".class")) {
			name = name + ".class";
		}
		List<String> classes = findAllClassNames(pkgName);
		for (String className : classes) {
			if (FileUtils.getFileName(className).equalsIgnoreCase(name)) {
				return FileUtils.getFileNameElimExt(className.replace('/', '.'));
			}
		}
		return null;
	}

	public static List<String> findAllClassNames(String pkgName) {
		try {
			return getResources(getContextClassLoader(), pkgName, CLASS_PATTERN);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static boolean isJarURL(URL url) {
		String protocol = url.getProtocol();

		return ("jar".equals(protocol)) || ("zip".equals(protocol)) || ("wsjar".equals(protocol));
	}

	public static List<Field> getDeclaredFields(Class<?> clazz) {
		return getDeclaredFields(clazz, null);
	}

	public static List<Field> getDeclaredFields(Class<?> clazz, Class<?> stopClass) {
		List<Field> fields = new ArrayList<Field>();
		for (Class<?> c = clazz; c != null; c = c.getSuperclass()) {
			if ((stopClass != null) && (c == stopClass)) {
				break;
			}
			fields.addAll((Collection<Field>) Arrays.asList(c.getDeclaredFields()));
		}

		return fields;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T> Constructor<T> getConstructor(Class<T> targetClass, Class<?>[] argTypes) {
		Constructor<T> result = null;
		Constructor[] cs = targetClass.getConstructors();

		for (Constructor c : cs) {
			Class<?>[] types = c.getParameterTypes();
			if (types.length == argTypes.length) {
				int i = 0;
				for (Class t : types) {
					if (!t.isAssignableFrom(argTypes[(i++)])) {
						break;
					}
				}
				if (i == types.length) {
					result = c;
					break;
				}
			}
		}
		return result;
	}

	public static boolean isAssignable(Class<?> cls, Class<?> toClass) {
		if (toClass == null) {
			return false;
		}

		if (cls == null) {
			return !toClass.isPrimitive();
		}
		if (cls.equals(toClass)) {
			return true;
		}
		if (cls.isPrimitive()) {
			if (!toClass.isPrimitive()) {
				return false;
			}
			if (Integer.TYPE.equals(cls)) {
				return (Long.TYPE.equals(toClass)) || (Float.TYPE.equals(toClass)) || (Double.TYPE.equals(toClass));
			}
			if (Long.TYPE.equals(cls)) {
				return (Float.TYPE.equals(toClass)) || (Double.TYPE.equals(toClass));
			}
			if (Boolean.TYPE.equals(cls)) {
				return false;
			}
			if (Double.TYPE.equals(cls)) {
				return false;
			}
			if (Float.TYPE.equals(cls)) {
				return Double.TYPE.equals(toClass);
			}
			if (Character.TYPE.equals(cls)) {
				return (Integer.TYPE.equals(toClass)) || (Long.TYPE.equals(toClass)) || (Float.TYPE.equals(toClass))
						|| (Double.TYPE.equals(toClass));
			}
			if (Short.TYPE.equals(cls)) {
				return (Integer.TYPE.equals(toClass)) || (Long.TYPE.equals(toClass)) || (Float.TYPE.equals(toClass))
						|| (Double.TYPE.equals(toClass));
			}
			if (Byte.TYPE.equals(cls)) {
				return (Short.TYPE.equals(toClass)) || (Integer.TYPE.equals(toClass)) || (Long.TYPE.equals(toClass))
						|| (Float.TYPE.equals(toClass)) || (Double.TYPE.equals(toClass));
			}

			return false;
		}
		return toClass.isAssignableFrom(cls);
	}

	public static String getShortClassName(String className) {
		if (className == null) {
			return "";
		}
		if (className.length() == 0) {
			return "";
		}
		char[] chars = className.toCharArray();
		int lastDot = 0;
		for (int i = 0; i < chars.length; i++) {
			if (chars[i] == '.')
				lastDot = i + 1;
			else if (chars[i] == '$') {
				chars[i] = '.';
			}
		}
		return new String(chars, lastDot, chars.length - lastDot);
	}
	
	@SuppressWarnings("unused")
	public static class ClassReader extends ByteArrayInputStream {
		private static final int CONSTANT_Class = 7;
		private static final int CONSTANT_Fieldref = 9;
		private static final int CONSTANT_Methodref = 10;
		private static final int CONSTANT_InterfaceMethodref = 11;
		private static final int CONSTANT_String = 8;
		private static final int CONSTANT_Integer = 3;
		private static final int CONSTANT_Float = 4;
		private static final int CONSTANT_Long = 5;
		private static final int CONSTANT_Double = 6;
		private static final int CONSTANT_NameAndType = 12;
		private static final int CONSTANT_Utf8 = 1;
		private static Map<String, Method> attrMethods = findAttributeReaders(ClassReader.class);
		private int[] cpoolIndex;
		private Object[] cpool;
		private String methodName;
		private Map<String, MethodInfo> methods = new HashMap<String, MethodInfo>();
		private Class<?>[] paramTypes;

		protected ClassReader(Class<?> c) throws IOException {
			this(getBytes(c));
		}

		protected ClassReader(byte[] b) throws IOException {
			super(b);

			if (readInt() != -889275714) {
				throw new IOException(
						"Error looking for paramter names in bytecode: input does not appear to be a valid class file");
			}

			readShort();
			readShort();

			readCpool();

			readShort();
			readShort();
			readShort();

			int count = readShort();
			for (int i = 0; i < count; i++) {
				readShort();
			}

			count = readShort();
			for (int i = 0; i < count; i++) {
				readShort();
				readShort();
				readShort();
				skipAttributes();
			}

			count = readShort();
			for (int i = 0; i < count; i++) {
				readShort();
				int m = readShort();
				String name = resolveUtf8(m);
				int d = readShort();
				this.methodName = (name + resolveUtf8(d));
				readAttributes();
			}
		}

		protected static byte[] getBytes(Class<?> c) throws IOException {
			InputStream fin = c.getResourceAsStream('/' + c.getName().replace('.', '/') + ".class");
			if (fin == null) {
				throw new IOException("Unable to load bytecode for class " + c.getName());
			}
			try {
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				byte[] buf = new byte[1024];
				int actual;
				do {
					actual = fin.read(buf);
					if (actual > 0)
						out.write(buf, 0, actual);
				} while (actual > 0);
				return out.toByteArray();
			} finally {
				fin.close();
			}
		}

		static String classDescriptorToName(String desc) {
			return desc.replace('/', '.');
		}

		protected static Map<String,Method> findAttributeReaders(Class<?> c) {
			HashMap<String, Method> map = new HashMap<String, Method>();
			Method[] methods = c.getMethods();

			for (int i = 0; i < methods.length; i++) {
				String name = methods[i].getName();
				if ((name.startsWith("read")) && (methods[i].getReturnType() == Void.TYPE)) {
					map.put(name.substring(4), methods[i]);
				}
			}

			return map;
		}

		protected static String getSignature(Member method, Class<?>[] paramTypes) {
			StringBuffer b = new StringBuffer((method instanceof Method) ? method.getName() : "<init>");
			b.append('(');

			for (int i = 0; i < paramTypes.length; i++) {
				addDescriptor(b, paramTypes[i]);
			}

			b.append(')');
			if ((method instanceof Method))
				addDescriptor(b, ((Method) method).getReturnType());
			else if ((method instanceof Constructor)) {
				addDescriptor(b, Void.TYPE);
			}

			return b.toString();
		}

		private static void addDescriptor(StringBuffer b, Class<?> c) {
			if (c.isPrimitive()) {
				if (c == Void.TYPE)
					b.append('V');
				else if (c == Integer.TYPE)
					b.append('I');
				else if (c == Boolean.TYPE)
					b.append('Z');
				else if (c == Byte.TYPE)
					b.append('B');
				else if (c == Short.TYPE)
					b.append('S');
				else if (c == Long.TYPE)
					b.append('J');
				else if (c == Character.TYPE)
					b.append('C');
				else if (c == Float.TYPE)
					b.append('F');
				else if (c == Double.TYPE)
					b.append('D');
			} else if (c.isArray()) {
				b.append('[');
				addDescriptor(b, c.getComponentType());
			} else {
				b.append('L').append(c.getName().replace('.', '/')).append(';');
			}
		}

		protected final int readShort() {
			return read() << 8 | read();
		}

		protected final int readInt() {
			return read() << 24 | read() << 16 | read() << 8 | read();
		}

		protected void skipFully(int n) throws IOException {
			while (n > 0) {
				int c = (int) skip(n);
				if (c <= 0)
					throw new EOFException("Error looking for paramter names in bytecode: unexpected end of file");
				n -= c;
			}
		}

		protected final Member resolveMethod(int index)
				throws IOException, ClassNotFoundException, NoSuchMethodException {
			int oldPos = this.pos;
			try {
				Member m = (Member) this.cpool[index];
				//Member localMember1;
				if (m == null) {
					this.pos = this.cpoolIndex[index];
					Class<?> owner = resolveClass(readShort());
					NameAndType nt = resolveNameAndType(readShort());
					String signature = nt.name + nt.type;
					if (nt.name.equals("<init>")) {
						Constructor<?>[] ctors = owner.getConstructors();
						for (int i = 0; i < ctors.length; i++) {
							String sig = getSignature(ctors[i], ctors[i].getParameterTypes());
							if (sig.equals(signature)) {
								Constructor<?> tmp141_140 = ctors[i];
								m = tmp141_140;
								this.cpool[index] = tmp141_140;
								return m;
							}
						}
					} else {
						Method[] methods = owner.getDeclaredMethods();
						for (int i = 0; i < methods.length; i++) {
							String sig = getSignature(methods[i], methods[i].getParameterTypes());
							if (sig.equals(signature)) {
								Method tmp220_219 = methods[i];
								m = tmp220_219;
								this.cpool[index] = tmp220_219;
								return m;
							}
						}
					}
					throw new NoSuchMethodException(signature);
				}
				return m;
			} finally {
				this.pos = oldPos;
			}
		}

		protected final Field resolveField(int i) throws IOException, ClassNotFoundException, NoSuchFieldException {
			int oldPos = this.pos;
			try {
				Field f = (Field) this.cpool[i];
				if (f == null) {
					this.pos = this.cpoolIndex[i];
					Class<?> owner = resolveClass(readShort());
					NameAndType nt = resolveNameAndType(readShort());
					Field tmp64_61 = owner.getDeclaredField(nt.name);
					f = tmp64_61;
					this.cpool[i] = tmp64_61;
				}
				return f;
			} finally {
				this.pos = oldPos;
			}
		}

		protected final NameAndType resolveNameAndType(int i) throws IOException {
			int oldPos = this.pos;
			try {
				NameAndType nt = (NameAndType) this.cpool[i];
				if (nt == null) {
					this.pos = this.cpoolIndex[i];
					String name = resolveUtf8(readShort());
					String type = resolveUtf8(readShort());
					NameAndType tmp65_62 = new NameAndType(name, type);
					nt = tmp65_62;
					this.cpool[i] = tmp65_62;
				}
				return nt;
			} finally {
				this.pos = oldPos;
			}
		}

		protected final Class<?> resolveClass(int i) throws IOException, ClassNotFoundException {
			int oldPos = this.pos;
			try {
				Class<?> c = (Class<?>) this.cpool[i];
				if (c == null) {
					this.pos = this.cpoolIndex[i];
					String name = resolveUtf8(readShort());
					Class<?> tmp52_49 = Class.forName(classDescriptorToName(name));
					c = tmp52_49;
					this.cpool[i] = tmp52_49;
				}
				return c;
			} finally {
				this.pos = oldPos;
			}
		}

		protected final String resolveUtf8(int i) throws IOException {
			int oldPos = this.pos;
			try {
				String s = (String) this.cpool[i];
				if (s == null) {
					this.pos = this.cpoolIndex[i];
					int len = readShort();
					skipFully(len);
					String tmp69_66 = new String(this.buf, this.pos - len, len, "utf-8");
					s = tmp69_66;
					this.cpool[i] = tmp69_66;
				}
				return s;
			} finally {
				this.pos = oldPos;
			}
		}

		protected final void readCpool() throws IOException {
			int count = readShort();
			this.cpoolIndex = new int[count];
			this.cpool = new Object[count];
			for (int i = 1; i < count; i++) {
				int c = read();
				this.cpoolIndex[i] = this.pos;
				switch (c) {
				case 9:
				case 10:
				case 11:
				case 12:
					readShort();
				case 7:
				case 8:
					readShort();
					break;
				case 5:
				case 6:
					readInt();

					i++;
				case 3:
				case 4:
					readInt();
					break;
				case 1:
					int len = readShort();
					skipFully(len);
					break;
				case 2:
				default:
					throw new IllegalStateException(
							"Error looking for paramter names in bytecode: unexpected bytes in file");
				}
			}
		}

		protected final void skipAttributes() throws IOException {
			int count = readShort();
			for (int i = 0; i < count; i++) {
				readShort();
				skipFully(readInt());
			}
		}

		protected final void readAttributes() throws IOException {
			int count = readShort();
			for (int i = 0; i < count; i++) {
				int nameIndex = readShort();
				int attrLen = readInt();
				int curPos = this.pos;

				String attrName = resolveUtf8(nameIndex);

				Method m = (Method) attrMethods.get(attrName);

				if (m != null) {
					try {
						m.invoke(this, new Object[0]);
					} catch (IllegalAccessException e) {
						this.pos = curPos;
						skipFully(attrLen);
					} catch (InvocationTargetException e) {
						try {
							throw e.getTargetException();
						} catch (Error ex) {
							throw ex;
						} catch (RuntimeException ex) {
							throw ex;
						} catch (IOException ex) {
							throw ex;
						} catch (Throwable ex) {
							this.pos = curPos;
							skipFully(attrLen);
						}
					}
				} else
					skipFully(attrLen);
			}
		}

		public void readCode() throws IOException {
			readShort();
			int maxLocals = readShort();

			MethodInfo info = new MethodInfo(maxLocals);
			if ((this.methods != null) && (this.methodName != null)) {
				this.methods.put(this.methodName, info);
			}

			skipFully(readInt());
			skipFully(8 * readShort());

			readAttributes();
		}

		public String[] getParameterNames(Constructor<?> ctor) {
			this.paramTypes = ctor.getParameterTypes();
			return getParameterNames(ctor, this.paramTypes);
		}

		public String[] getParameterNames(Method method) {
			this.paramTypes = method.getParameterTypes();
			return getParameterNames(method, this.paramTypes);
		}

		protected String[] getParameterNames(Member member, Class<?>[] paramTypes) {
			MethodInfo info = (MethodInfo) this.methods.get(getSignature(member, paramTypes));

			if (info != null) {
				String[] paramNames = new String[paramTypes.length];
				int j = Modifier.isStatic(member.getModifiers()) ? 0 : 1;

				boolean found = false;
				for (int i = 0; i < paramNames.length; i++) {
					if (info.names[j] != null) {
						found = true;
						paramNames[i] = info.names[j];
					}
					j++;
					if ((paramTypes[i] == Double.TYPE) || (paramTypes[i] == Long.TYPE)) {
						j++;
					}
				}

				if (found) {
					return paramNames;
				}
				return null;
			}

			return null;
		}

		private MethodInfo getMethodInfo() {
			MethodInfo info = null;
			if ((this.methods != null) && (this.methodName != null)) {
				info = (MethodInfo) this.methods.get(this.methodName);
			}
			return info;
		}

		public void readLocalVariableTable() throws IOException {
			int len = readShort();
			MethodInfo info = getMethodInfo();
			for (int j = 0; j < len; j++) {
				readShort();
				readShort();
				int nameIndex = readShort();
				readShort();
				int index = readShort();
				if (info != null)
					info.names[index] = resolveUtf8(nameIndex);
			}
		}

		private static class MethodInfo {
			String[] names;

			public MethodInfo(int maxLocals) {
				this.names = new String[maxLocals];
			}
		}

		private static class NameAndType {
			String name;
			String type;

			public NameAndType(String name, String type) {
				this.name = name;
				this.type = type;
			}
		}
	}

	static class Queued {
		final URL _packageURL;
		final String _packagePath;

		public Queued(URL packageURL, String packagePath) {
			this._packageURL = packageURL;
			this._packagePath = packagePath;
		}
	}
}
