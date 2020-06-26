package personal.will.solver;

import java.lang.reflect.Method;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import personal.will.annotation.WillService;

@WillService("requestResoler")
public class RequestResoler implements ArgumentResolver{

	@Override
	public Boolean isSupport(Class<?> type, int index, Method method) {
		return ServletRequest.class.isAssignableFrom(type);
	}

	@Override
	public Object getMethodParam(HttpServletRequest request, HttpServletResponse response, Class<?> type, int index,
			Method method) {
		return request;
	}

}
