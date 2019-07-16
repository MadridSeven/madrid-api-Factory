package madrid.apiFactory.core.util.source;


import madrid.apiFactory.core.util.exception.SqlSourceException;
import madrid.apiFactory.core.util.spring.ApplicationFactory;

public class SQLProvider {

	public static ISqlSource xmlSqlSource;
	public static ISqlSource dbSqlSource;

	/**
	 * 从xml中读取sql文件
	 */
	public static String getFromXml(String sqlId) {
		return getXmlSource().getSql(sqlId);
	}

	/**
	 * 从db中读取sql文件
	 */
	public static String getFromDb(String sqlId) {
		return getDbSource().getSql(sqlId);
	}

	/**
	 * xml代表从xml文件中读sql，db代表从数据库红读sql <code>{@link SourceType}</code>
	 * 
	 * @param key
	 * @return
	 */
	public static ISqlSource get(String key) {
		if (key.equals(SourceType.xml)) {
			return getXmlSource();
		} else if (key.equals(SourceType.db)) {
			return getDbSource();
		} else {
			throw new SqlSourceException(String.format("不支持%s，只支持xml或者db", key));
		}
	}

	private static ISqlSource getXmlSource() {
		if (xmlSqlSource == null) {
			xmlSqlSource = ApplicationFactory.getBean("xmlSqlSource", ISqlSource.class);
		}
		return xmlSqlSource;
	}

	private static ISqlSource getDbSource() {
		if (dbSqlSource == null) {
			dbSqlSource = ApplicationFactory.getBean("dbSqlSource", ISqlSource.class);
		}
		return dbSqlSource;
	}

	public class SourceType {
		public final static String xml = "xml";
		public final static String db = "db";
	}

}
