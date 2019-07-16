package madrid.apiFactory.core.util.utils;

import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.util.*;

public class StringUtils {
	
	public static final String EMPTY = "";
	private static final char[] PASSWORD_CHARS = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',
			'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z' };
//	private static final String FOLDER_SEPARATOR = "/";
//	private static final char EXTENSION_SEPARATOR = '.';
	
	public static boolean isEmpty(String string) {
		return string == null || string.length() == 0;
	}
	
	public static boolean isEmpty(Object s) {
		if ((s == null) || ("".equals(s.toString().trim()))) {
			return true;
		}
		return false;
	}

	public static boolean isNotEmpty(Object s) {
		return !isEmpty(s);
	}
	
	public static boolean isNotBlank(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return false;
        }
        for (int i = 0; i < strLen; i++) {
            if ((Character.isWhitespace(str.charAt(i)) == false)) {
                return true;
            }
        }
        return false;
    }

	public static String randomString(int length) {
		StringBuffer sb = new StringBuffer(length);
		int passwordCharsLength = PASSWORD_CHARS.length;

		SecureRandom random = new SecureRandom();
		for (int i = 0; i < length; i++) {
			sb.append(PASSWORD_CHARS[random.nextInt(passwordCharsLength)]);
		}
		return sb.toString();
	}

	public static String toUpperCase(String str) {
		return str == null ? null : str.toUpperCase();
	}

	public static String toLowerCase(String str) {
		return str == null ? null : str.toLowerCase();
	}

	public static String toDoubleCase(String input) {
		if (input != null) {
			char[] chars = input.toCharArray();
			for (int i = 0; i < chars.length; i++) {
				char c = chars[i];
				if (c == ' ')
					chars[i] = '　';
				else if ((c >= '!') && (c <= '~')) {
					chars[i] = ((char) (c + 65248));
				}
			}
			return new String(chars);
		}
		return input;
	}

	public static String reverse(String str) {
		if (str == null) {
			return null;
		}
		return new StringBuffer(str).reverse().toString();
	}

	public static String toSingleCase(String input) {
		if (input != null) {
			char[] chars = input.toCharArray();
			for (int i = 0; i < chars.length; i++) {
				char c = chars[i];
				if (c == '　')
					chars[i] = ' ';
				else if ((c >= 65281) && (c <= 65374)) {
					chars[i] = ((char) (c - 65248));
				}
			}
			return new String(chars);
		}
		return input;
	}

	public static String toUTF8(String source) {
		StringBuilder sb = new StringBuilder(source.length());
		for (char ch : source.toCharArray()) {
			sb.append("\\u").append(Integer.toHexString(ch));
		}
		return sb.toString();
	}

	public static String isoToUtf8(String source) {
		if(isEmpty(source)) return "";
		String result = "";
		try {
			result = new String(source.getBytes("ISO-8859-1"), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static String[] tokenizeToStringArray(String str, String delimiters) {
		return tokenizeToStringArray(str, delimiters, true, true);
	}

	public static String[] tokenizeToStringArray(String str, String delimiters, boolean trimTokens,
			boolean ignoreEmptyTokens) {
		StringTokenizer st = new StringTokenizer(str, delimiters);
		List<String> tokens = new ArrayList<String>();
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			if (trimTokens) {
				token = token.trim();
			}
			if ((!ignoreEmptyTokens) || (token.length() > 0)) {
				tokens.add(token);
			}
		}
		return toStringArray(tokens);
	}

	public static String[] toStringArray(Collection<?> collection) {
		if (collection == null) {
			return null;
		}
		return (String[]) collection.toArray(new String[collection.size()]);
	}

	public static String toDisplayString(Object[] array) {
		if (array != null) {
			StringBuilder builder = new StringBuilder();
			for (int i = 0; i < array.length; i++) {
				if (i > 0) {
					builder.append(",");
				}
				Object value = array[i];
				builder.append(value == null ? "null" : value.toString());
			}
			return builder.toString();
		}
		return null;
	}

	public static String toQuotedString(String[] array, String quote) {
		if ((array != null) && (array.length > 0)) {
			StringBuilder builder = new StringBuilder();
			for (int i = 0; i < array.length; i++) {
				if (i > 0) {
					builder.append(",");
				}
				String value = array[i];
				builder.append(quote);
				builder.append(value == null ? "" : value);
				builder.append(quote);
			}
			return builder.toString();
		}
		return quote + quote;
	}

	

	public static boolean equals(String a, String b) {
		if (a != null)
			return a.equals(b);
		if (b == null) {
			return true;
		}
		return false;
	}

	public static String safeGetString(String s) {
		return s == null ? "" : s;
	}

	public static String safeGetTrimString(String s) {
		return s == null ? "" : s.trim();
	}

	public static String toString(String[] array, String seperator) {
		StringBuffer buff = new StringBuffer();

		int i = 0;
		for (i = 0; i < array.length - 1; i++) {
			buff.append(array[i]).append(seperator);
		}
		buff.append(array[i]);

		return buff.toString();
	}

	public static String[] split(String seperators, String list) {
		return split(seperators, list, false);
	}

	public static String[] split(String seperators, String list, boolean include) {
		StringTokenizer tokens = new StringTokenizer(list, seperators, include);
		String[] result = new String[tokens.countTokens()];
		int i = 0;
		while (tokens.hasMoreTokens()) {
			result[(i++)] = tokens.nextToken();
		}
		return result;
	}

	public static int toInt(String value) {
		int intValue = 0;
		try {
			intValue = Integer.parseInt(value);
		} catch (Throwable localThrowable) {
		}
		return intValue;
	}

	public static long toLong(String value) {
		long longValue = 0L;
		try {
			longValue = Long.parseLong(value);
		} catch (Throwable localThrowable) {
		}
		return longValue;
	}

	public static boolean toBoolean(String value, boolean defaultValue) {
		if ("1".equals(value))
			return true;
		if ("0".equals(value))
			return false;
		if ("true".equalsIgnoreCase(value))
			return true;
		if ("false".equalsIgnoreCase(value)) {
			return false;
		}
		return defaultValue;
	}

//	public static String getString(InputStream in, String encoding) throws Exception {
//		String returnString = "";
//		InputStreamReader isr = null;
//		isr = new InputStreamReader(in, encoding);
//
//		StringBuffer b = new StringBuffer();
//		int ic;
//		while ((ic = isr.read()) != -1) {
//			int ic;
//			b.append((char) ic);
//		}
//		returnString = b.toString();
//		return returnString;
//	}

	public static String replaceAll(String text, String repl, String with) {
		if ((text == null) || (repl == null) || (with == null) || (repl.length() == 0)) {
			return text;
		}

		StringBuffer buf = new StringBuffer(text.length());
		int searchFrom = 0;
		while (true) {
			int foundAt = text.indexOf(repl, searchFrom);
			if (foundAt == -1) {
				break;
			}

			buf.append(text.substring(searchFrom, foundAt)).append(with);
			searchFrom = foundAt + repl.length();
		}
		buf.append(text.substring(searchFrom));

		return buf.toString();
	}

	public static boolean hasLength(String str) {
		return (str != null) && (str.length() > 0);
	}

	public static boolean hasText(String str) {
		if (!hasLength(str)) {
			return false;
		}
		int strLen = str.length();
		for (int i = 0; i < strLen; i++) {
			if (!Character.isWhitespace(str.charAt(i))) {
				return true;
			}
		}
		return false;
	}

	public static boolean containsWhitespace(String str) {
		if (!hasLength(str)) {
			return false;
		}
		int strLen = str.length();
		for (int i = 0; i < strLen; i++) {
			if (Character.isWhitespace(str.charAt(i))) {
				return true;
			}
		}
		return false;
	}

	public static String trimWhitespace(String str) {
		if (!hasLength(str)) {
			return str;
		}
		StringBuffer buf = new StringBuffer(str);
		do {
			buf.deleteCharAt(0);

			if (buf.length() <= 0)
				break;
		} while (Character.isWhitespace(buf.charAt(0)));

		while ((buf.length() > 0) && (Character.isWhitespace(buf.charAt(buf.length() - 1)))) {
			buf.deleteCharAt(buf.length() - 1);
		}
		return buf.toString();
	}

	public static String trimLeadingWhitespace(String str) {
		if (!hasLength(str)) {
			return str;
		}
		StringBuffer buf = new StringBuffer(str);
		while ((buf.length() > 0) && (Character.isWhitespace(buf.charAt(0)))) {
			buf.deleteCharAt(0);
		}
		return buf.toString();
	}

	public static String trimTrailingWhitespace(String str) {
		if (!hasLength(str)) {
			return str;
		}
		StringBuffer buf = new StringBuffer(str);
		while ((buf.length() > 0) && (Character.isWhitespace(buf.charAt(buf.length() - 1)))) {
			buf.deleteCharAt(buf.length() - 1);
		}
		return buf.toString();
	}

	public static String trimAllWhitespace(String str) {
		if (!hasLength(str)) {
			return str;
		}
		StringBuffer buf = new StringBuffer(str);
		int index = 0;
		while (buf.length() > index) {
			if (Character.isWhitespace(buf.charAt(index))) {
				buf.deleteCharAt(index);
			} else {
				index++;
			}
		}
		return buf.toString();
	}

	public static boolean startsWithIgnoreCase(String str, String prefix) {
		if ((str == null) || (prefix == null)) {
			return false;
		}
		if (str.startsWith(prefix)) {
			return true;
		}
		if (str.length() < prefix.length()) {
			return false;
		}
		String lcStr = str.substring(0, prefix.length()).toLowerCase();
		String lcPrefix = prefix.toLowerCase();
		return lcStr.equals(lcPrefix);
	}

	public static boolean endsWithIgnoreCase(String str, String suffix) {
		if ((str == null) || (suffix == null)) {
			return false;
		}
		if (str.endsWith(suffix)) {
			return true;
		}
		if (str.length() < suffix.length()) {
			return false;
		}

		String lcStr = str.substring(str.length() - suffix.length()).toLowerCase();
		String lcSuffix = suffix.toLowerCase();
		return lcStr.equals(lcSuffix);
	}

	public static int countOccurrencesOf(String str, String sub) {
		if ((str == null) || (sub == null) || (str.length() == 0) || (sub.length() == 0)) {
			return 0;
		}
		int count = 0;
		int pos = 0;
		int idx = 0;
		while ((idx = str.indexOf(sub, pos)) != -1) {
			count++;
			pos = idx + sub.length();
		}
		return count;
	}

	public static String replace(String inString, String oldPattern, String newPattern) {
		if (inString == null) {
			return null;
		}
		if ((oldPattern == null) || (newPattern == null)) {
			return inString;
		}

		StringBuffer sbuf = new StringBuffer();

		int pos = 0;
		int index = inString.indexOf(oldPattern);

		int patLen = oldPattern.length();
		while (index >= 0) {
			sbuf.append(inString.substring(pos, index));
			sbuf.append(newPattern);
			pos = index + patLen;
			index = inString.indexOf(oldPattern, pos);
		}
		sbuf.append(inString.substring(pos));

		return sbuf.toString();
	}

	public static String delete(String inString, String pattern) {
		return replace(inString, pattern, "");
	}

	public static String deleteAny(String inString, String charsToDelete) {
		if ((inString == null) || (charsToDelete == null)) {
			return inString;
		}
		StringBuffer out = new StringBuffer();
		for (int i = 0; i < inString.length(); i++) {
			char c = inString.charAt(i);
			if (charsToDelete.indexOf(c) == -1) {
				out.append(c);
			}
		}
		return out.toString();
	}

	public static String quote(String str) {
		return str != null ? "'" + str + "'" : null;
	}

	public static Object quoteIfString(Object obj) {
		return (obj instanceof String) ? quote((String) obj) : obj;
	}

	public static String unqualify(String qualifiedName) {
		return unqualify(qualifiedName, '.');
	}

	public static String unqualify(String qualifiedName, char separator) {
		return qualifiedName.substring(qualifiedName.lastIndexOf(separator) + 1);
	}

	public static String capitalize(String str) {
		return changeFirstCharacterCase(str, true);
	}

	public static String uncapitalize(String str) {
		return changeFirstCharacterCase(str, false);
	}

	private static String changeFirstCharacterCase(String str, boolean capitalize) {
		if ((str == null) || (str.length() == 0)) {
			return str;
		}
		StringBuffer buf = new StringBuffer(str.length());
		if (capitalize) {
			buf.append(Character.toUpperCase(str.charAt(0)));
		} else {
			buf.append(Character.toLowerCase(str.charAt(0)));
		}
		buf.append(str.substring(1));
		return buf.toString();
	}

	public static String getFilename(String path) {
		if (path == null) {
			return null;
		}
		int separatorIndex = path.lastIndexOf("/");
		return separatorIndex != -1 ? path.substring(separatorIndex + 1) : path;
	}

	public static String getFilenameExtension(String path) {
		if (path == null) {
			return null;
		}
		int sepIndex = path.lastIndexOf('.');
		return sepIndex != -1 ? path.substring(sepIndex + 1) : null;
	}

	public static String stripFilenameExtension(String path) {
		if (path == null) {
			return null;
		}
		int sepIndex = path.lastIndexOf('.');
		return sepIndex != -1 ? path.substring(0, sepIndex) : path;
	}

	public static String applyRelativePath(String path, String relativePath) {
		int separatorIndex = path.lastIndexOf("/");
		if (separatorIndex != -1) {
			String newPath = path.substring(0, separatorIndex);
			if (!relativePath.startsWith("/")) {
				newPath = newPath + "/";
			}
			return newPath + relativePath;
		}

		return relativePath;
	}

	public static String join(Object[] array, String separator) {
		if (array == null) {
			return null;
		}
		return join(array, separator, 0, array.length);
	}

	public static String join(Object[] array, String separator, int startIndex, int endIndex) {
		if (array == null) {
			return null;
		}
		if (separator == null) {
			separator = "";
		}

		int bufSize = endIndex - startIndex;
		if (bufSize <= 0) {
			return "";
		}

		bufSize = bufSize
				* ((array[startIndex] == null ? 16 : array[startIndex].toString().length()) + separator.length());

		StringBuffer buf = new StringBuffer(bufSize);

		for (int i = startIndex; i < endIndex; i++) {
			if (i > startIndex) {
				buf.append(separator);
			}
			if (array[i] != null) {
				buf.append(array[i]);
			}
		}
		return buf.toString();
	}

	public static String replaceOnce(String text, String repl, String with) {
		return replace(text, repl, with, 1);
	}

	public static String replace(String text, String repl, String with, int max) {
		if ((isEmpty(text)) || (isEmpty(repl)) || (with == null) || (max == 0)) {
			return text;
		}
		int start = 0;
		int end = text.indexOf(repl, start);
		if (end == -1) {
			return text;
		}
		int replLength = repl.length();
		int increase = with.length() - replLength;
		increase = increase < 0 ? 0 : increase;
		increase *= (max > 64 ? 64 : max < 0 ? 16 : max);
		StringBuffer buf = new StringBuffer(text.length() + increase);
		while (end != -1) {
			buf.append(text.substring(start, end)).append(with);
			start = end + replLength;
			max--;
			if (max == 0) {
				break;
			}
			end = text.indexOf(repl, start);
		}
		buf.append(text.substring(start));
		return buf.toString();
	}

	public static String join(Iterable<?> itable, String sep) {
		StringBuilder builder = new StringBuilder();
		Iterator<?> it = itable.iterator();
		while (it.hasNext()) {
			builder.append(it.next()).append(sep);
		}
		if (builder.length() > sep.length()) {
			builder.delete(builder.length() - sep.length(), builder.length());
		}
		return builder.toString();
	}

}
