package madrid.apiFactory.core.util.utils;

import com.alibaba.fastjson.JSON;

import java.util.List;


public class JSONUtils {
	
	public static <T> String encode(T t){
		return JSON.toJSONString(t);
	}
	
	public static <T> T decode(String json, Class<T> requiredType){
		return JSON.parseObject(json, requiredType);
	}
	
	public static <T> List<T> decodeArray(String json, Class<T> requiredType){
		return JSON.parseArray(json, requiredType);
	}
	
}
