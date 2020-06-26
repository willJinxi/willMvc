package personal.will.service.impl;

import personal.will.annotation.WillService;
import personal.will.service.PersonalService;

@WillService("personalService")
public class PersonalServiceImpl implements PersonalService {

	public String query(String name, Integer age) {
		return "name : " + name + "; age = " + age;
	}

	public String save(String name, String age) {
		return "save " + name + " successful!";
	}

	public String update(String name, String age) {
		return "update " + name + "successful!";
	}

}
