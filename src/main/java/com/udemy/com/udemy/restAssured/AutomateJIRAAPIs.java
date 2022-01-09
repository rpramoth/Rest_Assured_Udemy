package com.udemy.com.udemy.restAssured;

import static io.restassured.RestAssured.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.testng.annotations.Test;


import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;

public class AutomateJIRAAPIs {

	static String userName, password,issueId,cookieID,idOfComment;

	public static void createSessionId() throws IOException {
		Properties prop = new Properties();
		FileInputStream fis = new FileInputStream("C:\\Users\\Pramod\\Desktop\\New folder\\com.udemy.restAssured\\Config\\JiraLogin.properties");
		prop.load(fis);
		userName =prop.getProperty("userName");
		password=prop.getProperty("password");

		RestAssured.baseURI="http://localhost:8080";
		String responseOfSessionResponse = given().log().all().header("Content-Type","application/json").body("{ \r\n" + 
				"    \"username\": \""+userName+"\", \r\n" + 
				"    \"password\": \""+password  +"\" \r\n" + 
				"    }").when().post("/rest/auth/1/session").then().log().all().assertThat().statusCode(200).extract().response().asString();

		JsonPath js = new JsonPath(responseOfSessionResponse);
		cookieID = js.get("session.name")+"= "+js.get("session.value");
	}

	public static void createBug(String issueName) {
		System.out.println("Cookie ID is "+cookieID);
		String createIssueResponse=	given().header("Cookie",cookieID).header("Content-Type","application/json").body(" { \r\n" + 
				"      \"fields\": {\r\n" + 
				"        \"project\": \r\n" + 
				"        {\r\n" + 
				"            \"key\": \"RPM\"\r\n" + 
				"        },\r\n" + 
				"        \"summary\": \"Create Bug Through Automation -- "+issueName+"\",\r\n" + 
				"        \"description\": \"Createion of Bug through Automation\",\r\n" + 
				"        \"issuetype\": \r\n" + 
				"        {\r\n" + 
				"            \"name\": \"Bug\"\r\n" + 
				"        }\r\n" + 
				"    }\r\n" + 
				"}")
				.when().post("/rest/api/2/issue").then().log().all().extract().response().asString();

		JsonPath js1 = new JsonPath(createIssueResponse);
		issueId=js1.get("id");
		System.out.println("Created Bug id is "+issueId);
	}

	public static void addComment() {
		String addCommentResponse=given().pathParam("issueId", issueId).header("Content-Type","application/json").header("Cookie",cookieID)
				.body("{\r\n" + 
						"    \"body\": \"Adding comment to the bug created through Automation\",\r\n" + 
						"    \"visibility\": {\r\n" + 
						"        \"type\": \"role\",\r\n" + 
						"        \"value\": \"Administrators\"\r\n" + 
						"    }\r\n" + 
						"}").when().post("/rest/api/2/issue/{issueId}/comment").then().assertThat().statusCode(201).extract().response().asString();

		JsonPath js= new JsonPath(addCommentResponse);
		idOfComment=js.get("id");

	}


	public static void deleteBug() {
		given().header("Cookie",cookieID).when().delete("/rest/api/2/issue/"+issueId).then().log().all().assertThat().statusCode(204);
	}


	public static void addAttachment() {

		given().log().all().header("X-Atlassian-Token","no-check").header("Content-Type","multipart/form-data")
		.header("Cookie",cookieID).multiPart("file",new File("C:\\Users\\Pramod\\Desktop\\New folder\\com.udemy.restAssured\\Config\\JiraLogin.properties"))
		.pathParam("issueId", issueId)
		.when().post("rest/api/2/issue/{issueId}/attachments").then().log().all().assertThat().statusCode(200);
 
	}

	public static void getIssueDetails() {

		String getIssueResponse=given().pathParam("issueId",issueId).queryParam("fields", "comment").header("Cookie",cookieID).when().get("/rest/api/2/issue/{issueId}")
				.then().log().all().assertThat().statusCode(200).extract().response().asString();

		JsonPath js = new JsonPath(getIssueResponse);
		int commentsSize=js.getInt("fields.comment.comments.size()");
		for(int i =0;i<commentsSize;i++) {
			if(js.getInt("fields.comment.comments["+i+"].id")==Integer.parseInt(idOfComment)) {
				if(js.getString("fields.comment.comments["+i+"].body").equals("Adding comment to the bug created through Automation")) {
					System.out.println(js.getString("fields.comment.comments["+i+"].body"));
					System.out.println("Comments that are added are present");
				}
				else {
					System.out.println("Comments that are added are not present");

				}
			}
		}

	}




	public static void main(String[] args) throws IOException {

		createSessionId();
		createBug("Get Issue");
		addComment();
		addAttachment();
		getIssueDetails();
		//	deleteBug();





	}




}
