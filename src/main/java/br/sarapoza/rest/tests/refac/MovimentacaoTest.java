package br.sarapoza.rest.tests.refac;

import static io.restassured.RestAssured.given;

import org.junit.Test;

import br.sarapoza.rest.core.BaseTest;
import br.sarapoza.rest.tests.Movimentacao;
import br.sarapoza.rest.tests.utils.DataUtils;
import io.restassured.RestAssured;

public class MovimentacaoTest extends BaseTest{
	
	public Integer getIdContaPeloNome(String nome) {
		return RestAssured.get("/contas?nome=" + nome).then().extract().path("id[0]");
	}
	
	@Test
	public void deveInserirMovimentacaoComSucesso() {
		
		Movimentacao mov = getMovimentacaoValida();
		
		given()
			.body(mov)
		.when()
			.post("/transacoes")
		.then()
			.statusCode(201)
		; 
	} 
	
	private Movimentacao getMovimentacaoValida() {
		Movimentacao mov = new Movimentacao();
		mov.setConta_id(getIdContaPeloNome("Conta para movimentacoes"));
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
