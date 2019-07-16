package madrid.apiFactory.core.util.exception;

/**
 * sql源的异常类
 */
@SuppressWarnings("serial")
public class SqlSourceException extends RuntimeException {
	public SqlSourceException(String message) {
		super(message);
	}

	public SqlSourceException(String message, Throwable e) {
		super(message, e);
	}
}


