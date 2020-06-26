package personal.will.solver;

import java.lang.reflect.Method;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import personal.will.annotation.WillService;

@WillService("httpResponseResolver")
public class HttpResponseResolver implements ArgumentResolver {

	@Override
	public Boolean isSupport(Class<?> type, int index, Method method) {
		return ServletResponse.class.isAssignableFrom(type);
	}

	@Override
	public Object getMethodParam(HttpServletRequest request, HttpServletResponse response, Class<?> type, int index,
			Method method) {
		return response;
	}

}
