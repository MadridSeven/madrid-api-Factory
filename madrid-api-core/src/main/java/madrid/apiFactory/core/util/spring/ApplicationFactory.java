package madrid.apiFactory.core.util.spring;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Map;

public class ApplicationFactory implements ApplicationContextAware {
	private static ApplicationContext ac = null;
	
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		ac = applicationContext;
	}

	public static boolean containsBean(String bean) {
//		System.out.println("------" + bean + "--------");
		return ac.containsBean(bean);
	}

	public static Object getBean(String bean) {
		return ac.getBean(bean);
	}

	public static <T> T getBean(String bean, Class<T> requiredType) {
		return ac.getBean(bean, requiredType);
	}

	public static <T> T getFirstBeanOfType(Class<T> requiredType) {
		String[] beans = ac.getBeanNamesForType(requiredType);
		for (String bean : beans) {
			return ac.getBean(bean, requiredType);
		}
		return null;
	}
	
	public static <T> Map<String, T> getBeansOfTypes(Class<T> requiredType) {
		 return ac.getBeansOfType(requiredType);
	}
}

