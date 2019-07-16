package madrid.apiFactory.core.util.utils;

import java.util.UUID;

public class UUIDUtils {
	public static String uuid() {
		String uuid = UUID.randomUUID().toString();
		return uuid.replace("-", "");
	}
}

