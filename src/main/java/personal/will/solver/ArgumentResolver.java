package personal.will.solver;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface ArgumentResolver {

	public Boolean isSupport(Class<?> type, int index, Method method);
	
	public Object getMethodParam(HttpServletRequest request, HttpServletResponse response, Class<?> type, int index, Method method);
}
