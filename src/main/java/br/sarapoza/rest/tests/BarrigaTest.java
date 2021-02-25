package br.sarapoza.rest.tests;

import static org.hamcrest.Matchers.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import br.sarapoza.rest.core.BaseTest;
import br.sarapoza.rest.tests.utils.DataUtils;
import io.restassured.RestAssured;
import io.restassured.specification.FilterableRequestSpecification;

import static io.restassured.RestAssured.given;

import java.util.HashMap;
import java.util.Map;

public class BarrigaTest extends BaseTest{
	
	private static String CONTA_NOME = "conta " + System.nanoTime();
	private static Integer CONTA_ID;
	private static Integer MOV_ID;
	
	@BeforeClass
	public static void login() {
		Map<String, String> login = new HashMap<>();
		login.put("email", "sara@teste");
		login.put("senha", "123456");
		
		String token = given()
			.body(login)
		.when()
			.post("/signin")
		.then()
			.statusCode(200)
			.extract().path("token")
		;
		
		RestAssured.requestSpecification.header("Authorization", "JWT " + token);
	}
	
	@Test
	public void deveIncluirContaComSucesso() {
		CONTA_ID = given()
			.body("{ \"nome\" : \""+ CONTA_NOME + "\" }")
		.when()
			.post("/contas")
		.then()
			.statusCode(201)
			.extract().path("id")
		; 
	} 
	
	@Test
	public void deveAlterarContaComSucesso() {
		given()
			.body("{ \"nome\" : \""+ CONTA_NOME + " alterada\"}")
			.pathParam("id", CONTA_ID)
		.when()
			.put("/contas/{id}")
		.then()
			.statusCode(200)
			.body("nome", is(CONTA_NOME + " alterada"))
		; 
	} 
	
	@Test
	public void naoDeveIncluirContaComNomeRepetido() {
		given()
			.body("{ \"nome\" : \""+ CONTA_NOME + "conta alterada\"}")
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
		
		MOV_ID = given()
			.body(mov)
		.when()
			.post("/transacoes")
		.then()
			.statusCode(201)
			.extract().path("id")
		; 
	} 
	
	@Test
	public void deveValidarCamposObrigatoriosNaMovimentacao() {
		
		given()
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
		mov.setData_transacao(DataUtils.getDataDiferencaDias(2));
		
		given()
			.body(mov)
		.when()
			.post("/transacoes")
		.then()
			.statusCode(400)
			.body("$", hasSize(1))
			.body("msg", hasItem("Data da Movimentação deve ser menor ou igual à data atual"))
		; 
	}
	
	//teste de rota com erro não tratado (erro de integridade)
	@Test
	public void naoDeveRemoverContaComMovimentação() {
		
		given()
			.pathParam("id", CONTA_ID)
		.when()
			.delete("/contas/{id}")
		.then()
			.statusCode(500)
			.body("constraint", is("transacoes_conta_id_foreign"))
		; 
	}
	
	@Test
	public void deveCalcularSaldoContas() {
		
		given()
		.when()
			.get("/saldo")
		.then()
			.statusCode(200)
			.body("find{it.conta_id == "+CONTA_ID+"}.saldo", is("100.00"))
		; 
	}
	
	@Test
	public void deveRemoverMovimentação() {
		
		given()
			.pathParam("id", MOV_ID)
		.when()
			.delete("/transacoes/{id}")
		.then()
			.statusCode(204)	
		;
	}
	
	@Test
	public void naoDeveAcessarApiSemToken() {
		FilterableRequestSpecification req = (FilterableRequestSpecification) RestAssured.requestSpecification;
		req.removeHeader("Authorization");
		
		given()
		.when()
			.get("/contas")
		.then()
			.statusCode(401)
		; 
	} 
	
	private Movimentacao getMovimentacaoValida() {
		Movimentacao mov = new Movimentacao();
		mov.setConta_id(CONTA_ID);
//		mov.setUsuario_id(usuario_id);
		mov.setDescricao("descricao da movimentacao");
		mov.setEnvolvido("Envolvido na mov");
		mov.setTipo("REC");
		mov.setData_transacao(DataUtils.getDataDiferencaDias(-1));
		mov.setData_pagamento(DataUtils.getDataDiferencaDias(5));
		mov.setValor(100f);
		mov.setStatus(true);
		return mov;
	}
}