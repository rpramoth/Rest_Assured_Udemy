package com.udemy.com.udemy.restAssured;

import static io.restassured.RestAssured.given;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.testng.annotations.Test;


import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;

public class AutomateJIRAAPIs {


	@Test
	public static String createSessionID() throws IOException {

		Properties prop = new Properties();
		FileInputStream fis = new FileInputStream("/com.udemy.restAssured/Config/JiraLogin.properties");
		prop.load(fis);

		String userName =prop.getProperty("userName");
		String password=prop.getProperty("password");


		RestAssured.baseURI="http://localhost:8080/";
		String responseOfSessionResponse = given().body("{ \r\n" + 
				"    \"username\": \""+userName+"\", \r\n" + 
				"    \"password\": \""+password  +"\" \r\n" + 
				"    }").when().post().then().log().all().assertThat().statusCode(200).extract().response().asString();

		JsonPath js = new JsonPath(responseOfSessionResponse);
		String cookieID = js.get("session.name")+"= "+js.get("session.value");

		return cookieID;
	}

	@Test
	public static void createBug() throws IOException {

		String createIssueResponse=	given().header("Cookie",createSessionID()).body("  { \r\n" + 
				"      \"fields\": {\r\n" + 
				"        \"project\": \r\n" + 
				"        {\r\n" + 
				"            \"key\": \"RPM\"\r\n" + 
				"        },\r\n" + 
				"        \"summary\": \"Create Bug Through Automation\",\r\n" + 
				"        \"description\": \"Createion of Bug through Automation\",\r\n" + 
				"        \"issuetype\": \r\n" + 
				"        {\r\n" + 
				"            \"name\": \"Bug\"\r\n" + 
				"        }\r\n" + 
				"    }\r\n" + 
				"}").when().post("rest/api/2/issue").then().log().all().extract().response().asString();

		JsonPath js = new JsonPath(createIssueResponse);
		String id=js.get("id");

		System.out.println("Created Bug id is "+id);
		
		
	}




}
