package co.edu.unbosque.beans;

import java.io.IOException;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import co.edu.unbosque.util.AESUtil;
import jakarta.annotation.ManagedBean;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;

@ManagedBean
@RequestScoped
public class LoginBean {
	private Long id = 0L;
	private String nombre = "";
	private String nota1 = "";
	private String nota2 = "";
	private String nota3 = "";
	private String promedio = "";
	private String info = "";

	private static final HttpClient httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1)
			.connectTimeout(Duration.ofSeconds(10)).build();

	public LoginBean() {
		// TODO Auto-generated constructor stub
	}

	// https://mkyong.com/java/how-to-send-http-request-getpost-in-java/

	public void addMessage(FacesMessage.Severity severity, String summary, String detail) {
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
	}

	
	
	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNota1() {
		return nota1;
	}

	public void setNota1(String nota1) {
		this.nota1 = nota1;
	}

	public String getNota2() {
		return nota2;
	}

	public void setNota2(String nota2) {
		this.nota2 = nota2;
	}

	public String getNota3() {
		return nota3;
	}

	public void setNota3(String nota3) {
		this.nota3 = nota3;
	}

	public String getPromedio() {
		return promedio;
	}

	public void setPromedio(String promedio) {
		this.promedio = promedio;
	}


	public void showSticky() {
		FacesContext.getCurrentInstance().addMessage("Su promedio es mayor a 3,0",
				new FacesMessage(FacesMessage.SEVERITY_INFO, "Sticky Message", "Message Content"));
	}

	public void showStickyGreen() {
		FacesContext.getCurrentInstance().addMessage("Su promedio es igual a 3,0",
				new FacesMessage(FacesMessage.SEVERITY_WARN, "Sticky Message", "Message Content"));
	}
	
	public void showStickyError() {
		FacesContext.getCurrentInstance().addMessage("Su promedio es manor a 3,0",
				new FacesMessage(FacesMessage.SEVERITY_ERROR, "Sticky Message", "Message Content"));
	}
	
	public void showStickyFatal() {
		FacesContext.getCurrentInstance().addMessage("ERROR",
				new FacesMessage(FacesMessage.SEVERITY_FATAL, "Sticky Message", "Message Content"));
	}

	public void create() {
		String jsonEntrada = "{\"nombre\": \"" + nombre+"\"," +"nota 1\": \"" + nota1 + "\"," + "\"nota2\": \"" + nota2 + "\"," + "\"nota3\": \"" + nota3 +"\"}";
		String jsonEncrypt = AESUtil.encrypt(jsonEntrada);
		String respuesta= doPost("http://localhost:8081/user/createuserjason",
				"{\"nombre\": \"" + nombre+"\"," +"nota 1\": \"" + nota1 + "\"," + "\"nota2\": \"" + nota2 + "\"," + "\"nota3\": \"" + nota3 +"\"}");
		double promedio= springPromedio(respuesta);
		
			if(promedio >30) {
				showSticky();
			}else if(promedio == 30) {
				showStickyGreen();
			}else if(promedio< 30) {
				showStickyError();
			}
			else {
				showStickyFatal();
			}
		showStickyGreen();

	}
	
	public double springPromedio(String response) {
		JsonObject json = JsonParser.parseString(response).getAsJsonObject();
		return json.get("promedio").getAsDouble();
	}

	public void mostrar() {
		System.out.println(doGet("http://localhost:8081/user/getall"));

		info = doGet("http://localhost:8081/user/getall");
		showStickyGreen();

	}

	public static String doGet(String url) {
		HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create(url))
				.setHeader("User-Agent", "Java 11 HttpClient Bot").build();

		HttpResponse<String> response = null;
		try {
			response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		System.out.println("status code -> " + response.statusCode());

		String uglyJson = response.body();
		return prettyPrintUsingGson(uglyJson);
	}

	

	public static String mostrarJason(String url) {
		HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create(url))
				.setHeader("User-Agent", "Java 11 HttpClient Bot").build();

		HttpResponse<String> response = null;
		try {
			response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		System.out.println("status code -> " + response.statusCode());

		String uglyJson = response.body();
		return prettyPrintUsingGson(uglyJson);
	}

	public static String prettyPrintUsingGson(String uglyJson) {
		Gson gson = new GsonBuilder().setLenient().setPrettyPrinting().create();
		JsonElement jsonElement = JsonParser.parseString(uglyJson);
		String prettyJsonString = gson.toJson(jsonElement);
		return prettyJsonString;
	}


	public static String doPost(String url, String json) {

		// add json header
		HttpRequest request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(json))
				.uri(URI.create(url)).setHeader("User-Agent", "Java 11 HttpClient Bot") // add request header
				.header("Content-Type", "application/json").build();

		HttpResponse<String> response = null;
		try {
			response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("status code -> " + response.statusCode());

		return response.body();
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

}
