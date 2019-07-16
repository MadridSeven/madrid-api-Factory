package madrid.apiFactory.core.util.utils;

import java.math.BigDecimal;

public class NumberUtils {
	public static double add(double v1, double v2) {
		BigDecimal b1 = new BigDecimal(v1);
		BigDecimal b2 = new BigDecimal(v2);
		return b1.add(b2).doubleValue();
	}

	public static double subtract(double v1, double v2) {
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.subtract(b2).doubleValue();
	}

	public static double multiply(double v1, double v2) {
		BigDecimal b1 = new BigDecimal(v1);
		BigDecimal b2 = new BigDecimal(v2);
		return b1.multiply(b2).doubleValue();
	}

	public static double multiply(double v1, double v2, int scale) {
		if (scale < 0) {
			throw new IllegalArgumentException("The scale must be a positive integer or zero");
		}
		BigDecimal b1 = new BigDecimal(v1);
		BigDecimal b2 = new BigDecimal(v2);
		return b1.multiply(b2).setScale(scale, 4).doubleValue();
	}

	public static double divide(double v1, double v2) {
		BigDecimal b1 = new BigDecimal(v1);
		BigDecimal b2 = new BigDecimal(v2);
		return b1.divide(b2).doubleValue();
	}

	public static double divide(double v1, double v2, int scale) {
		if (scale < 0) {
			throw new IllegalArgumentException("The scale must be a positive integer or zero");
		}

		BigDecimal b1 = new BigDecimal(v1);
		BigDecimal b2 = new BigDecimal(v2);
		return b1.divide(b2, scale, 4).doubleValue();
	}

	public static double round(double v, int scale) {
		if (scale < 0) {
			throw new IllegalArgumentException("The scale must be a positive integer or zero");
		}
		BigDecimal b = new BigDecimal(v);
		return b.setScale(scale, 4).doubleValue();
	}

	public static boolean isNumber(String str) {
		if (StringUtils.isEmpty(str)) {
			return false;
		}
		char[] chars = str.toCharArray();
		int sz = chars.length;
		boolean hasExp = false;
		boolean hasDecPoint = false;
		boolean allowSigns = false;
		boolean foundDigit = false;

		int start = chars[0] == '-' ? 1 : 0;
		if ((sz > start + 1) && (chars[start] == '0') && (chars[(start + 1)] == 'x')) {
			int i = start + 2;
			if (i == sz) {
				return false;
			}

			for (; i < chars.length; i++) {
				if (((chars[i] < '0') || (chars[i] > '9')) && ((chars[i] < 'a') || (chars[i] > 'f'))
						&& ((chars[i] < 'A') || (chars[i] > 'F'))) {
					return false;
				}
			}
			return true;
		}

		sz--;

		int i = start;

		while ((i < sz) || ((i < sz + 1) && (allowSigns) && (!foundDigit))) {
			if ((chars[i] >= '0') && (chars[i] <= '9')) {
				foundDigit = true;
				allowSigns = false;
			} else if (chars[i] == '.') {
				if ((hasDecPoint) || (hasExp)) {
					return false;
				}
				hasDecPoint = true;
			} else if ((chars[i] == 'e') || (chars[i] == 'E')) {
				if (hasExp) {
					return false;
				}
				if (!foundDigit) {
					return false;
				}
				hasExp = true;
				allowSigns = true;
			} else if ((chars[i] == '+') || (chars[i] == '-')) {
				if (!allowSigns) {
					return false;
				}
				allowSigns = false;
				foundDigit = false;
			} else {
				return false;
			}
			i++;
		}
		if (i < chars.length) {
			if ((chars[i] >= '0') && (chars[i] <= '9')) {
				return true;
			}
			if ((chars[i] == 'e') || (chars[i] == 'E')) {
				return false;
			}
			if ((!allowSigns) && ((chars[i] == 'd') || (chars[i] == 'D') || (chars[i] == 'f') || (chars[i] == 'F'))) {
				return foundDigit;
			}
			if ((chars[i] == 'l') || (chars[i] == 'L')) {
				return (foundDigit) && (!hasExp);
			}

			return false;
		}

		return (!allowSigns) && (foundDigit);
	}
}
