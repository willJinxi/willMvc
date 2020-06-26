package personal.will.solver;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import personal.will.annotation.WillRequestParam;
import personal.will.annotation.WillService;

@WillService("wollRequestParamResolver")
public class WillRequestParamResolver implements ArgumentResolver {

	@Override
	public Boolean isSupport(Class<?> type, int index, Method method) {
		Annotation[][] parameterAnnotations = method.getParameterAnnotations();
		Annotation[] annotations = parameterAnnotations[index];
		if (annotations != null && annotations.length > 0) {
			for (Annotation annotation : annotations) {
				if (WillRequestParam.class.isAssignableFrom(annotation.getClass())) {
					return Boolean.TRUE;
				}
			}
		}
		return Boolean.FALSE;
	}

	@Override
	public Object getMethodParam(HttpServletRequest request, HttpServletResponse response, Class<?> type, int index,
			Method method) {
		Annotation[][] parameterAnnotations = method.getParameterAnnotations();
		Annotation[] annotations = parameterAnnotations[index];
		if (annotations != null && annotations.length > 0) {
			for (Annotation annotation : annotations) {
				if (WillRequestParam.class.isAssignableFrom(annotation.getClass())) {
					WillRequestParam willRequestParam = (WillRequestParam)annotation;
					String paramKey = willRequestParam.value();
					String parameter = request.getParameter(paramKey);
					if (type.toString().equals("class java.lang.Integer")) {
						return new Integer(request.getParameter(paramKey));
					}
					return request.getParameter(paramKey);
				}
			}
		}
		return null;
	}

}
