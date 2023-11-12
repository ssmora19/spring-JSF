package co.edu.unbosque.service;

import java.util.List;

public interface CRUDOperation <T>{
	
	public boolean create(T data);

	public List<T> getAll();

}
