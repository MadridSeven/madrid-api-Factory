package madrid.apiFactory.core.util.sql.parse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class SQLPaser implements ISQLPaser {

	private final static Logger log = LoggerFactory.getLogger(SQLPaser.class);
	private boolean ignoreNull = true;
	private boolean ignoreEmpty = true;

	public void setIgnoreEmpty(boolean ignoreEmpty) {
		this.ignoreEmpty = ignoreEmpty;
	}

	public void setIgnoreNull(boolean ignoreNull) {
		this.ignoreNull = ignoreNull;
	}

	public DynamicSQL parseDynamicSQL(String rawSQL, Object paramObject) {
		StringBuilder builder = new StringBuilder();
		DynamicSQL dynamicSQL = new DynamicSQL();
		for (int i = 0; i < rawSQL.length(); i++) {
			char c = rawSQL.charAt(i);

			if (c == '{') {
				int fs = i + 1;
				int fe = rawSQL.indexOf('}', fs);
				String fragment = rawSQL.substring(fs, fe);
				int length = fragment.length();
				boolean append = false;
				StringBuilder fragmentBuffer = new StringBuilder();
				for (int j = 0; j < length; j++) {
					int k = 0;
					for (k = j; k < length; k++) {
						char fc = fragment.charAt(k);
						if (fc == '#') {
							int ps = k + 1;
							int pe = 0;
							pe = fragment.indexOf('#', ps);
							String pname = fragment.substring(ps, pe);
							fragmentBuffer.append("?");
							Parameter p = getParam(pname, paramObject);

							if ((append) || (shouldAppend(p, this.ignoreNull, this.ignoreEmpty))) {
								dynamicSQL.addParameter(p);
								append = true;
							} else {
								append = false;
							}
							k = pe;
						} else if (fc == '$') {
							int ps = k + 1;
							int pe = 0;
							pe = fragment.indexOf('$', ps);
							String pname = fragment.substring(ps, pe);
							Parameter p = getParam(pname, paramObject);
							if ((append) || (shouldAppend(p, this.ignoreNull, this.ignoreEmpty))) {
								fragmentBuffer.append(getParam(pname, paramObject).getValue());
								append = true;
							} else {
								append = false;
							}
							k = pe;
						} else {
							fragmentBuffer.append(fc);
						}
					}
					if (append) {
						builder.append(fragmentBuffer);
					}
					j = k; 
				}
				i = fe;
			} else if (c == '#') {
				int ps = i + 1;
				int pe = rawSQL.indexOf('#', ps);
				String pname = rawSQL.substring(ps, pe);
				builder.append("?");
				Parameter p = getParam(pname, paramObject);
				dynamicSQL.addParameter(p);
				i = pe;
			} else if (c == '$') {
				int ps = i + 1;
				int pe = rawSQL.indexOf('$', ps);
				String pname = rawSQL.substring(ps, pe);
				builder.append(getParam(pname, paramObject).getValue());
				i = pe;
			} else {
				builder.append(c);
			}
		}
		dynamicSQL.setSql(builder.toString());
		if (log.isDebugEnabled())
			try {
				log.debug(dynamicSQL.getSql());
			} catch (Exception localException) {
			}
		return dynamicSQL;
	}

	private boolean shouldAppend(Parameter p, boolean ignoreNull, boolean ignoreEmpty) {
		boolean append = true;
		if (p.isNull())
			append = !ignoreNull;
		else if (p.isEmpty()) {
			append = !ignoreEmpty;
		}
		return append;
	}

	public Parameter getParam(String property, Object bean) {
		return new Parameter(property, bean);
	}

}
