package madrid.apiFactory.core.util.source;


import madrid.apiFactory.core.util.exception.SqlSourceException;

/**
 * <code>{@link ISqlSource}</code>
 * 定义SQL语句的来源，允许通过XML文件或DB存储SQL信息
 * 
 */
public interface ISqlSource {
	/**
	 * 根据SqlId获取具体的SQL语句
	 * @param sqlId sqlId或者SQL语句，如果sqlId中包含空格，则认为sqlId本身就是SQL
	 * @return 返回sqlId对应的SQL语句，如果没有对应的SQL语句则抛出SqlSourceException异常；如果sqlId本身传入的是SQL，则直接返回
	 */
	String getSql(String sqlId) throws SqlSourceException;
}

