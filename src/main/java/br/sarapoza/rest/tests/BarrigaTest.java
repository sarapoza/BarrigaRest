package br.sarapoza.rest.tests;

import static org.hamcrest.Matchers.*;
import org.junit.Before;
import org.junit.Test;

import br.sarapoza.rest.core.BaseTest;

import static io.restassured.RestAssured.given;

import java.util.HashMap;
import java.util.Map;

public class BarrigaTest extends BaseTest{
	
	private String token;
	
	@Before
	public void login() {
		Map<String, String> login = new HashMap<>();
		login.put("email", "sara@teste");
		login.put("senha", "123456");
		
		token = given()
			.body(login)
		.when()
			.post("/signin")
		.then()
			.statusCode(200)
			.extract().path("token")
		;
	}

	@Test
	public void naoDeveAcessarApiSemToken() {
		given()
		.when()
			.get("/contas")
		.then()
			.statusCode(401)
		; 
	} 
	
	@Test
	public void deveIncluirContaComSucesso() {
		given()
			.header("Authorization", "JWT " + token)
			.body("{ \"nome\" : \"conta qualquer\" }")
		.when()
			.post("/contas")
		.then()
			.statusCode(201)
		; 
	} 
	
	@Test
	public void deveAlterarContaComSucesso() {
		given()
			.header("Authorization", "JWT " + token)
			.body("{ \"nome\" : \"conta alterada\" }")
		.when()
			.put("/contas/419540")
		.then()
			.statusCode(200)
			.body("nome", is("conta alterada"))
		; 
	} 
	
	@Test
	public void naoDeveIncluirContaComNomeRepetido() {
		given()
			.header("Authorization", "JWT " + token)
			.body("{ \"nome\" : \"conta alterada\" }")
		.when()
			.post("/contas")
		.then()
			.statusCode(400)
			.body("error", is("Já existe uma conta com esse nome!"))
		; 
	} 
	
	@Test
	public void deveInserirMovimentacaoComSucesso() {
		
		Movimentacao mov = getMovimentacaoValida();
		
		given()
			.header("Authorization", "JWT " + token)
			.body(mov)
		.when()
			.post("/transacoes")
		.then()
			.statusCode(201)
		; 
	} 
	
	@Test
	public void deveValidarCamposObrigatoriosNaMovimentacao() {
		
		given()
			.header("Authorization", "JWT " + token)
			.body("{}")
		.when()
			.post("/transacoes")
		.then()
			.statusCode(400)
			.body("$", hasSize(8))
			.body("msg", hasItems(
					"Data da Movimentação é obrigatório",
					"Data do pagamento é obrigatório",
					"Descrição é obrigatório",
					"Interessado é obrigatório",
					"Valor é obrigatório",
					"Valor deve ser um número",
					"Conta é obrigatório",
					"Situação é obrigatório"
					))
		; 
	} 
	
	@Test
	public void naoDeveInserirMovimentacaoFutura() {
		
		Movimentacao mov = getMovimentacaoValida();
		mov.setData_transacao("25/01/2022");
		
		given()
			.header("Authorization", "JWT " + token)
			.body(mov)
		.when()
			.post("/transacoes")
		.then()
			.statusCode(400)
			.body("$", hasSize(1))
			.body("msg", hasItem("Data da Movimentação deve ser menor ou igual à data atual"))
		; 
	}
	
	private Movimentacao getMovimentacaoValida() {
		Movimentacao mov = new Movimentacao();
		mov.setConta_id(419540);
//		mov.setUsuario_id(usuario_id);
		mov.setDescricao("descricao da movimentacao");
		mov.setEnvolvido("Envolvido na mov");
		mov.setTipo("REC");
		mov.setData_transacao("01/01/2000");
		mov.setData_pagamento("01/01/2010");
		mov.setValor(100f);
		mov.setStatus(true);
		return mov;
	}
}