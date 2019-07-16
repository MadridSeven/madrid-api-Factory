package madrid.apiFactory.core.util.exception;

public class RestServiceException extends RuntimeException {

	private static final long serialVersionUID = -1440582893977999951L;

	public RestServiceException(String message, Object... args){
		super(String.format(message, args));
	}
	
	public RestServiceException(String message) {
		super(message);
	}

	public RestServiceException(String message, Throwable e) {
		super(message, e);
	}
}
