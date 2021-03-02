package br.sarapoza.rest.tests.refac.suite;

import static io.restassured.RestAssured.given;

import java.util.HashMap;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import br.sarapoza.rest.core.BaseTest;
import br.sarapoza.rest.tests.refac.ContasTest;
import br.sarapoza.rest.tests.refac.MovimentacaoTest;
import io.restassured.RestAssured;

@RunWith(Suite.class)
@SuiteClasses({
	ContasTest.class,
	MovimentacaoTest.class
	
})
public class Suit extends BaseTest{
	
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
		
		RestAssured.get("/reset").then().statusCode(200);
	}

}
