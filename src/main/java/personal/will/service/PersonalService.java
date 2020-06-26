package personal.will.service;

public interface PersonalService {
	
	String query(String name, Integer age);
	
	String save(String name, String age);
	
	String update(String name, String age);

}
