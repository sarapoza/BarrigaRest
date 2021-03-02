package br.sarapoza.rest.tests.refac;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import org.junit.Test;

import br.sarapoza.rest.core.BaseTest;
import io.restassured.RestAssured;

public class ContasTest extends BaseTest{
	
	@Test
	public void deveIncluirContaComSucesso() {
		given()
			.body("{ \"nome\" : \"Conta Inserida\" }")
		.when()
			.post("/contas")
		.then()
			.statusCode(201)
		; 
	} 
	
	@Test
	public void deveAlterarContaComSucesso() {
		Integer CONTA_ID = getIdContaPeloNome("Conta para alterar");
		
		given()
			.body("{ \"nome\" : \"Conta alterada\"}")
			.pathParam("id", CONTA_ID)
		.when()
			.put("/contas/{id}")
		.then()
			.statusCode(200)
			.body("nome", is("Conta alterada"))
		; 
	} 
	
	public Integer getIdContaPeloNome(String nome) {
		return RestAssured.get("/contas?nome=" + nome).then().extract().path("id[0]");
	}
}
