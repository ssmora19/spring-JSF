package co.edu.unbosque.service;

import java.util.ArrayList;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import co.edu.unbosque.model.User;
import co.edu.unbosque.repository.UserRepository;
import co.edu.unbosque.util.AESUtil;

public class UserService implements CRUDOperation<User>{
	@Autowired
	private UserRepository userRepo;

	public UserService() {
		// TODO Auto-generated constructor stub
	}

	public User decrypt(User us) {

		User temp = new User();
		temp.setId(us.getId());
		temp.setUsername(AESUtil.decrypt(us.getUsername()));
		temp.setNota1(AESUtil.decrypt(us.getNota1()));
		temp.setNota2(AESUtil.decrypt(us.getNota2()));
		temp.setNota3(AESUtil.decrypt(us.getNota3()));

		return temp;

	}

	public String calcularPromedio(User us) {
		double nota1, nota2, nota3, promedio;
		String resultado;
		User aux = decrypt(us);
		nota1 = Double.parseDouble(aux.getNota1());
		nota2 = Double.parseDouble(aux.getNota2());
		nota3 = Double.parseDouble(aux.getNota3());
		promedio = (nota1 + nota2 + nota3) / 3.0;
		
		JsonObject json = new JsonObject();
		json.addProperty("promedio", promedio);
		
		 return json.toString();
	}

	

	@Override
	public List getAll() {
		List<User> database= userRepo.findAll(); 
		List<User> decrypt = new ArrayList<>();
		
		for(User us: database) {
			
			User temp = new User();
			temp.setId(us.getId());
			temp.setUsername(AESUtil.decrypt(us.getUsername()));
			temp.setNota1(AESUtil.decrypt(us.getNota1()));
			temp.setNota2(AESUtil.decrypt(us.getNota2()));
			temp.setNota3(AESUtil.decrypt(us.getNota3()));
			
			decrypt.add(temp);
		}
		return decrypt;
	}

	@Override
	public boolean create(User us) {
		try {
			User temp = new User();
			temp.setId(us.getId());
			temp.setUsername(AESUtil.encrypt(us.getUsername()));
			temp.setNota1(AESUtil.encrypt(us.getNota1()));
			temp.setNota2(AESUtil.encrypt(us.getNota2()));
			temp.setNota3(AESUtil.encrypt(us.getNota3()));
			String promedio = calcularPromedio(temp);
			temp.setPromedio(AESUtil.encrypt(promedio));

			userRepo.save(temp);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	

}
