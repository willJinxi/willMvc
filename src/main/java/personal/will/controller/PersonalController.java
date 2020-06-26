package personal.will.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import personal.will.annotation.WillController;
import personal.will.annotation.WillQualifier;
import personal.will.annotation.WillRequestMapping;
import personal.will.annotation.WillRequestParam;
import personal.will.service.PersonalService;

@WillController
@WillRequestMapping("/personal")
public class PersonalController {

	@WillQualifier("personalService")
	private PersonalService personalService;
	
	@WillRequestMapping("/will")
	public void query(HttpServletRequest request, HttpServletResponse response, @WillRequestParam("name") String name,
			@WillRequestParam("age") Integer age) throws IOException {
		response.getWriter().write(personalService.query(name, age));

	}
}
