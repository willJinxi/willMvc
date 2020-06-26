package personal.will.servlet;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import personal.will.annotation.WillController;
import personal.will.annotation.WillQualifier;
import personal.will.annotation.WillRequestMapping;
import personal.will.annotation.WillService;
import personal.will.handler.ParamHandlerService;

public class DispatcherServlet extends HttpServlet {
	
	List<String> classNameList = new ArrayList<String>();
	Map<String, Object> beans = new ConcurrentHashMap<>();
	Map<String, Method> methodMappingMap = new ConcurrentHashMap<>();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String requestURI = request.getRequestURI();
		String contextUrl = request.getContextPath();
		Method method = methodMappingMap.get(requestURI.replace(contextUrl, ""));
		Object obj = beans.get("/" + (requestURI.replace(contextUrl, "").split("/")[1]));
		ParamHandlerService paramHandlerService = (ParamHandlerService)beans.get("paramHandlerService");
		Object[] args = paramHandlerService.handler(request, response, method, beans);;
		try {
			Object invoke = method.invoke(obj, args);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	}
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		doScanPackage("personal.will");
		for (String name : classNameList) {
			System.out.println(name);
		}
		doInstance();
		for (Map.Entry<String, Object> entry : beans.entrySet()) {
			System.out.println(entry.getValue().getClass().getName());
		}
		doDependention();
		doRequestMapping();
		for (Map.Entry<String, Method> methodMapping : methodMappingMap.entrySet()) {
			System.out.println("url : " + methodMapping.getKey() + " method : " + methodMapping.getValue().getName());
		}
	}
	
	private void doInstance() {
		if (classNameList.size() <= 0) {
			System.err.println("doscanFailed....");
			return ;
		}
		for (String className : classNameList) {
			String cn = className.replace(".class", "");
			try {
				Class<?> clazz = Class.forName(cn);
				if (clazz.isAnnotationPresent(WillController.class)) {
					WillController controller = clazz.getAnnotation(WillController.class);
					Object instancr = clazz.newInstance();
					WillRequestMapping requestMapping = clazz.getAnnotation(WillRequestMapping.class);
					String key = requestMapping.value();
					beans.put(key, instancr);
				}
				else if (clazz.isAnnotationPresent(WillService.class)) {
					WillService willService = clazz.getAnnotation(WillService.class);
					Object object = clazz.newInstance();
					beans.put(willService.value(), object);
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}
	
	// 扫描class类
	private void doScanPackage(String basePackage){
		// 扫描编译好的项目路径下的所有类
		URL url = this.getClass().getResource("/" + basePackage.replaceAll("\\.", "/"));
		String fileStr = url.getFile();
		File file = new File(fileStr);
		String[] subFilePathlList = file.list();
		for(String path : subFilePathlList) {
			File subFile = new File(fileStr + path);
			if (subFile.isDirectory()) {
				doScanPackage(basePackage + "." + path);
			} else {
				classNameList.add(basePackage+ "." + subFile.getName());
			}
		}
	}
	
	private void doDependention() {
		if (beans.size() <= 0 ) {
			System.out.println("doScan failed!");
			return ;
		}
		for (Map.Entry<String, Object> entry : beans.entrySet()) {
			Class<?> clazz = entry.getValue().getClass();
			if (clazz.isAnnotationPresent(WillController.class)) {
				Field[] declaredFields = clazz.getDeclaredFields();
				if (declaredFields != null && declaredFields.length > 0) {
					for (Field field : declaredFields) {
						if (field.isAnnotationPresent(WillQualifier.class)) {
							WillQualifier annotation = field.getAnnotation(WillQualifier.class);
							String value = annotation.value();
							field.setAccessible(true);
							try {
								field.set(entry.getValue(), beans.get(value));
							} catch (IllegalArgumentException | IllegalAccessException e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
		}
	}
	
	private void doRequestMapping() {
		if (beans == null || beans.size() <= 0) {
			System.err.println("doScan failed");
			return ;
		}
		for (Map.Entry<String, Object> entry : beans.entrySet()) {
			Class<?> clazz = entry.getValue().getClass();
			if (clazz.isAnnotationPresent(WillController.class)) {
				Method[] methods = clazz.getMethods();
				WillRequestMapping contronllerAnnotation = clazz.getAnnotation(WillRequestMapping.class);
				String controllerPath = contronllerAnnotation.value();
				if (methods == null || methods.length <= 0) {
					continue;
				}
				for (Method method : methods) {
					if (method.isAnnotationPresent(WillRequestMapping.class)) {
						WillRequestMapping methodAnnotation = method.getAnnotation(WillRequestMapping.class);
						String methodMapping = methodAnnotation.value();
						methodMappingMap.put(controllerPath + methodMapping, method);
					}
					
				}
			}
		}
	}

}
