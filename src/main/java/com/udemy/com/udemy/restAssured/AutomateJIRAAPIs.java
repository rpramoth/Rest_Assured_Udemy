package com.udemy.com.udemy.restAssured;

import static io.restassured.RestAssured.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.testng.annotations.Test;


import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;

public class AutomateJIRAAPIs {


public static void main(String[] args) throws IOException {

/*	Create a Session ID in JIRA*/
	
		Properties prop = new Properties();
		FileInputStream fis = new FileInputStream("C:\\Users\\Pramod\\Desktop\\New folder\\com.udemy.restAssured\\Config\\JiraLogin.properties");
		prop.load(fis);

		String userName =prop.getProperty("userName");
		String password=prop.getProperty("password");

		/*Create a bug in JIRA*/
		RestAssured.baseURI="http://localhost:8080";
		String responseOfSessionResponse = given().log().all().header("Content-Type","application/json").body("{ \r\n" + 
				"    \"username\": \""+userName+"\", \r\n" + 
				"    \"password\": \""+password  +"\" \r\n" + 
				"    }").when().post("/rest/auth/1/session").then().log().all().assertThat().statusCode(200).extract().response().asString();

		JsonPath js = new JsonPath(responseOfSessionResponse);
		String cookieID = js.get("session.name")+"= "+js.get("session.value");
		System.out.println("Cookie ID is "+cookieID);
		String createIssueResponse=	given().header("Cookie",cookieID).header("Content-Type","application/json").body(" { \r\n" + 
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
				"}")
				.when().post("/rest/api/2/issue").then().log().all().extract().response().asString();

		JsonPath js1 = new JsonPath(createIssueResponse);
		String issueId=js1.get("id");
		System.out.println("Created Bug id is "+issueId);
		
		/*Add comment to the created bug in JIRA*/
		
		given().pathParam("issueId", issueId).header("Content-Type","application/json").header("Cookie",cookieID)
		.body("{\r\n" + 
				"    \"body\": \"Adding comment to the bug created through Automation\",\r\n" + 
				"    \"visibility\": {\r\n" + 
				"        \"type\": \"role\",\r\n" + 
				"        \"value\": \"Administrators\"\r\n" + 
				"    }\r\n" + 
				"}").when().post("/rest/api/2/issue/{issueId}/comment").then().assertThat().statusCode(201);
		
		
		
		/*Delete the created bug in JIRA*/
		
	//	given().header("Cookie",cookieID).when().delete("/rest/api/2/issue/"+issueId).then().log().all().assertThat().statusCode(204);
		
		
	}




}
