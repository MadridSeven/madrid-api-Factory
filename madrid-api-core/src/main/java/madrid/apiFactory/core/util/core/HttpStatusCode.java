package madrid.apiFactory.core.util.core;

public enum HttpStatusCode {
	SUCCESS(200), EXCEPTION(500), NOT_FOUND(400), AUTH_FAIL(403);

	private int value;

	HttpStatusCode(int value) {
		this.value = value;
	}
	
	public int getValue(){
		return this.value;
	}
}
