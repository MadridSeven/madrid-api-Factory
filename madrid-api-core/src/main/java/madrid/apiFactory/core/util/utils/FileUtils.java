package madrid.apiFactory.core.util.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.Random;

public class FileUtils {
	private static final Logger log = LoggerFactory.getLogger(FileUtils.class);
	private static Properties properties;
	public static final String EXPORTED_FILE_NAME_ENCODING = "ISO-8859-1";

	public static File findFileInDir(File dir, String fileName, boolean ignoreCase) {
		File fileFound = null;
		File[] files = dir.listFiles();
		for (File f : files) {
			if (f.isFile()) {
				if (ignoreCase ? fileName.equalsIgnoreCase(f.getName()) : fileName.equals(f.getName())) {
					fileFound = f;
					break;
				}
			} else {
				fileFound = findFileInDir(f, fileName, ignoreCase);
				if (fileFound != null) {
					break;
				}
			}
		}
		return fileFound;
	}

	public static void deleteEmptyFolder(File root) {
		if (root.isDirectory()) {
			File[] files = root.listFiles();
			if (files.length == 0) {
				root.delete();
				if (log.isDebugEnabled())
					log.debug("folder " + root.getAbsolutePath() + " is deleted");
			} else {
				for (File child : files) {
					if (child.isDirectory()) {
						deleteEmptyFolder(child);
					}
				}
				if (root.listFiles().length == 0) {
					root.delete();
					if (log.isDebugEnabled())
						log.debug("folder " + root.getAbsolutePath() + " is deleted");
				}
			}
		}
	}

	public static String readStreamContent(InputStream in, String encoding) {
		String content = "";
		try {
			InputStreamReader reader = null;
			try {
				if (StringUtils.isEmpty(encoding))
					reader = new InputStreamReader(in);
				else {
					reader = new InputStreamReader(in, encoding);
				}

				StringBuffer sb = new StringBuffer();
				int b;
				while ((b = reader.read()) != -1) {
					sb.append((char) b);
				}
				content = sb.toString();
			} finally {
				try {
					reader.close();
				} catch (Exception localException) {
				}
			}
			try {
				reader.close();
			} catch (Exception localException1) {
			}
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			try {
				in.close();
			} catch (IOException localIOException1) {
			}
		} finally {
			try {
				in.close();
			} catch (IOException localIOException2) {
			}
		}
		return content;
	}

	public static String readFileContent(String filePath) throws FileNotFoundException {
		return readFileContent(filePath, null);
	}

	public static String readFileContent(String filePath, String fileEncoding) throws FileNotFoundException {
		filePath = StringUtils.replace(filePath, "\\", "/");
		if (fileEncoding == null) {
			fileEncoding = "GBK";
		}
		return readStreamContent(new FileInputStream(filePath), fileEncoding);
	}

	public static boolean deleteFile(String absolutePath) {
		File f = new File(absolutePath);
		if ((f.exists()) && (f.isFile())) {
			return f.delete();
		}
		return false;
	}

	public static boolean exists(String absolutePath) {
		File f = new File(absolutePath);
		return f.exists();
	}

	public static String getFileExtName(String fileName) {
		int extIndex = fileName.lastIndexOf(".");
		String fileExt = extIndex > 0 ? fileName.substring(extIndex + 1) : "";
		return fileExt;
	}

	public static String getFileNameElimExt(String filePath) {
		String fileName = getFileName(filePath).trim();
		if (fileName.length() > 0) {
			int extLength = getFileExtName(fileName).length();
			fileName = extLength > 0 ? fileName.substring(0, fileName.length() - extLength - 1) : fileName;
		}
		return fileName;
	}

	public static String getFileName(String filePath) {
		filePath = StringUtils.replace(filePath, "\\", "/");
		int lastFolderIndex = filePath.lastIndexOf("/");
		String fileName = lastFolderIndex > 0 ? filePath.substring(lastFolderIndex + 1) : filePath;
		return fileName;
	}

	public static String getFileFolder(String file) {
		file = StringUtils.replace(file, "\\", "/");
		if (file.indexOf('/') != -1) {
			return file.substring(0, file.lastIndexOf("/"));
		}
		return "";
	}

	public static void createFile(String file, InputStream in) throws IOException {
		createFolder(getFileFolder(file));
		FileOutputStream out = new FileOutputStream(file);
		try {
			byte[] bytes = new byte[1024];
			int n = -1;
			while ((n = in.read(bytes)) != -1)
				out.write(bytes, 0, n);
			return;
		} finally {
			try {
				IOUtils.closeQuietly(in);
				try {
					out.flush();
				} catch (IOException localIOException1) {
				}
				IOUtils.closeQuietly(out);
			} catch (Throwable localThrowable1) {
			}
		}
	}

	public static void createFile(String file, String content, String encoding) {
		createFolder(getFileFolder(file));

		FileOutputStream out = null;
		OutputStreamWriter writer = null;
		try {
			out = new FileOutputStream(file);
			writer = new OutputStreamWriter(out, encoding);
			writer.write(content);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			IOUtils.closeQuietly(writer);
		}
	}

	public static void appendToFile(String file, String content, String encoding) throws IOException {
		createFolder(getFileFolder(file));

		FileOutputStream out = new FileOutputStream(file, true);
		OutputStreamWriter writer = new OutputStreamWriter(out, encoding);
		try {
			writer.write(content);
		} finally {
			IOUtils.closeQuietly(writer);
		}
	}

	public static void createFolder(String folder) {
		if (StringUtils.isNotEmpty(folder)) {
			File file = new File(folder);
			if ((!file.exists()) && (!file.mkdirs()))
				throw new RuntimeException("createFolder " + folder + " error");
		}
	}

	public static String createFile(String filePath) {
		if (StringUtils.isEmpty(filePath)) {
			filePath = generateUniqueFileName("$$temp");
		}
		createFolder(getFileFolder(filePath));
		File f = new File(filePath);
		if (!f.exists()) {
			try {
				f.createNewFile();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return filePath;
	}

	public static String generateUniqueFileName(String fileName) {
		DateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		String formatDate = format.format(new Date());
		int random = new Random().nextInt(10000);
		return formatDate + "_" + random + "@" + fileName;
	}

	public static int copy(InputStream input, OutputStream output) throws IOException {
		byte[] buffer = new byte[1024];
		int count = 0;
		int n = 0;
		while (-1 != (n = input.read(buffer))) {
			output.write(buffer, 0, n);
			count += n;
		}
		return count;
	}

	public static void copyFolder(String fromFile, String toFile, boolean copyRootFolder, String[] ignoreFileNames) {
		copyFolder(new File(fromFile), toFile, copyRootFolder, ignoreFileNames);
	}

	public static void copyFolder(File fromFile, String toFile, boolean copyRootFolder, String[] ignoreFileNames) {
		String fileName = fromFile.getName();
		if (fileName.startsWith("ignore")) {
			return;
		}
		for (String fn : ignoreFileNames)
			if (fn.equals(fileName))
				return;
		FileInputStream fis;
		FileOutputStream fos;
		if (fromFile.isFile()) {
			toFile = toFile + "/" + fromFile.getName();
			File targetFile = new File(toFile);

			fis = null;
			fos = null;
			try {
				if (!targetFile.exists()) {
					createFile(targetFile.getAbsolutePath());
				}
				fis = new FileInputStream(fromFile);
				fos = new FileOutputStream(toFile);
				copy(fis, fos);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				((IOException) e).printStackTrace();
			} finally {
				IOUtils.closeQuietly(fis);
				IOUtils.closeQuietly(fos);
			}
		} else {
			if (copyRootFolder) {
				toFile = toFile + File.separator + fromFile.getName();
			}
			if (!new File(toFile).exists()) {
				new File(toFile).mkdirs();
			}
			File[] e = fromFile.listFiles();
			for (int i = 0; i < e.length; i++) {
				File f = e[i];
				copyFolder(f, toFile, true, ignoreFileNames);
			}
		}
	}

	public static String getFileMimeType(String fileName) throws IOException {
		if ((StringUtils.isEmpty(fileName)) || (fileName.indexOf(".") == -1)) {
			return null;
		}
		String extension = fileName.substring(fileName.lastIndexOf("."));

		if (StringUtils.isEmpty(extension)) {
			return null;
		}

		initMimeType();

		extension = extension.toLowerCase();

		if (!extension.startsWith(".")) {
			extension = "." + extension;
		}
		return properties.getProperty(extension);
	}

	public static String getFileMimeTypeByExtName(String extension) {
		if (StringUtils.isEmpty(extension)) {
			return null;
		}
		initMimeType();
		extension = extension.toLowerCase();

		if (!extension.startsWith(".")) {
			extension = "." + extension;
		}
		return properties.getProperty(extension);
	}

	public static String encodeFileName(String fileName) throws UnsupportedEncodingException {
		if ((fileName == null) || (fileName.trim().equals("")))
			return "";
		return new String(fileName.getBytes(), "ISO-8859-1");
	}

	private static synchronized void initMimeType() {
		if (properties == null) {
			properties = new Properties();
			InputStream is = FileUtils.class.getResourceAsStream("mimeTypes.properties");
			try {
				properties.load(is);
			} catch (IOException e) {
				properties.put("js", "text/javascript");
				properties.put("htm", "text/html");
				properties.put("html", "text/html");
				properties.put("css", "text/css");
				properties.put("gif", "image/gif");
				properties.put("jpg", "image/jpeg");
				properties.put("xml", "application/xml");
				properties.put("png", "image/png");
				properties.put("htc", "text/x-component");
				properties.put("swf", "application/x-shockwave-flash");
			}
			IOUtils.closeQuietly(is);
		}
	}

	public static String replace(String template, String placeholder, String replacement) {
		boolean wholeWords = false;
		int loc = template == null ? -1 : template.indexOf(placeholder);
		if (loc < 0) {
			return template;
		}

		boolean actuallyReplace = (!wholeWords) || (loc + placeholder.length() == template.length())
				|| (!Character.isJavaIdentifierPart(template.charAt(loc + placeholder.length())));
		String actualReplacement = actuallyReplace ? replacement : placeholder;
		return template.substring(0, loc) + actualReplacement
				+ replace(template.substring(loc + placeholder.length()), placeholder, replacement);
	}

	public static String getFilePath(String file) {
		if (!file.contains(":")) {
			File prjFile = new File(file);
			if ((prjFile.exists()) && (prjFile.isDirectory())) {
				return prjFile.getAbsolutePath();
			}
		}
		return file;
	}

	public static String getClassDiretory(Class<?> clazz) {
		return "/" + clazz.getPackage().getName().replaceAll("\\.", "/") + "/";
	}
}
