package madrid.apiFactory.core.util.sql.parse;

/**
 * @author baiyh
 */
public class SQLPaserFactory {
	private static ISQLPaser sqlPaser;
	
	public static ISQLPaser get(){
		if(sqlPaser == null){
			sqlPaser = new SQLPaser();
		}
		return sqlPaser;
	}
}
