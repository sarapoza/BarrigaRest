package br.sarapoza.rest.core;

import io.restassured.http.ContentType;

public interface Constantes {

	String APP_BASE_URL = "https://barrigarest.wcaquino.me";
	Integer APP_PORT = 443; //porta 443 para https
	String APP_BASE_PATH = "";
	
	ContentType APP_CONTENTE_TYPE = ContentType.JSON;
	
	Long MAX_TIMEOUT = 5000L;
}
