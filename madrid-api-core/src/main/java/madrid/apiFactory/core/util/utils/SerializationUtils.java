package madrid.apiFactory.core.util.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public final class SerializationUtils {
	private static final Logger log = LoggerFactory.getLogger(SerializationUtils.class);

	public static Object clone(Serializable object) {
		log.debug("Starting clone through serialization");
		return deserialize(serialize(object));
	}

	public static void serialize(Serializable obj, OutputStream outputStream) {
		if (outputStream == null) {
			throw new IllegalArgumentException("The OutputStream must not be null");
		}

		ObjectOutputStream out = null;
		try {
			out = new ObjectOutputStream(outputStream);
			out.writeObject(obj);
		} catch (IOException ex) {
			throw new RuntimeException("could not serialize", ex);
		} finally {
			try {
				if (out != null)
					out.close();
			} catch (IOException ignored) {
				throw new RuntimeException(ignored);
			}
		}
	}

	public static byte[] serialize(Serializable obj) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream(512);
		serialize(obj, baos);
		return baos.toByteArray();
	}

	public static Object deserialize(InputStream inputStream) {
		if (inputStream == null) {
			throw new IllegalArgumentException("The InputStream must not be null");
		}

		log.debug("Starting deserialization of object");

		CustomObjectInputStream in = null;
		try {
			in = new CustomObjectInputStream(inputStream);
			return in.readObject();
		} catch (ClassNotFoundException ex) {
			throw new RuntimeException("could not deserialize", ex);
		} catch (IOException ex) {
			throw new RuntimeException("could not deserialize", ex);
		} finally {
			try {
				if (in != null)
					in.close();
			} catch (IOException ex) {
				throw new RuntimeException(ex);
			}
		}
	}

	public static Object deserialize(byte[] objectData) {
		if (objectData == null) {
			throw new IllegalArgumentException("The byte[] must not be null");
		}
		ByteArrayInputStream bais = new ByteArrayInputStream(objectData);
		return deserialize(bais);
	}

	private static final class CustomObjectInputStream extends ObjectInputStream {
		public CustomObjectInputStream(InputStream in) throws IOException {
			super();
		}

		protected Class<?> resolveClass(ObjectStreamClass v) throws IOException, ClassNotFoundException {
			String className = v.getName();
			Class<?> resolvedClass = null;

			SerializationUtils.log.debug("Attempting to locate class [" + className + "]");

			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			try {
				resolvedClass = loader.loadClass(className);
				SerializationUtils.log.debug("Class resolved through context class loader");
			} catch (ClassNotFoundException e) {
				SerializationUtils.log.debug("Asking super to resolve");
				resolvedClass = super.resolveClass(v);
			}

			return resolvedClass;
		}
	}
}