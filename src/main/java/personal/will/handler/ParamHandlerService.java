package personal.will.handler;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import personal.will.annotation.WillService;
import personal.will.solver.ArgumentResolver;

@WillService("paramHandlerService")
public class ParamHandlerService {
	
	public Object[] handler(HttpServletRequest request, HttpServletResponse response, Method method, Map<String, Object> beans) {
		Class<?>[] parameterTypes = method.getParameterTypes();
		Object[] objects = new Object[parameterTypes.length];
		Map<String, Object> resolerMap = getResolverByType(beans, ArgumentResolver.class);
		int index = 0;
		int i = 0;
		for (Class<?> clazz : parameterTypes) {
			for (Entry<String, Object> entry : resolerMap.entrySet()) {
				ArgumentResolver resolver = (ArgumentResolver)entry.getValue();
				if (resolver.isSupport(clazz, index, method)) {
					objects[i++] = resolver.getMethodParam(request, response, clazz, index, method);
				}
			}
			index++;
		}
		
		return objects;
	}

	private Map<String, Object> getResolverByType(Map<String, Object> beans, Class<ArgumentResolver> clazz) {
		Map<String, Object> resoler = new HashMap<>();
		for (Map.Entry<String, Object> entry : beans.entrySet()) {
			Class<?>[] interfaces = entry.getValue().getClass().getInterfaces();
			if (interfaces == null || interfaces.length <= 0) {
				continue;
			}
			for (Class<?> interfaceClazz : interfaces) {
				if (interfaceClazz.isAssignableFrom(clazz)) {
					resoler.put(entry.getKey(), entry.getValue());
				}
			}
			
		}
		return resoler;
	}

}
