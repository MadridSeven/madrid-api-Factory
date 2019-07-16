package madrid.apiFactory.core.util.sql.parse;

public interface ISQLPaser {
	
	DynamicSQL parseDynamicSQL(String paramString, Object paramObject);
	
}
