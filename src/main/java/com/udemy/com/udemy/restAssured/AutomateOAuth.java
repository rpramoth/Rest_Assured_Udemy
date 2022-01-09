package com.udemy.com.udemy.restAssured;
import static io.restassured.RestAssured.*;

import io.restassured.path.json.JsonPath;

public class AutomateOAuth {

	public static String accessToken,code;

	public static void getAuthCode() {

		String authURL="https://rahulshettyacademy.com/getCourse.php?code=4%2F0AX4XfWjQ-0HtuBejWEebBTfCED3J4B_CwlXfPJKCVr-7ralylI63V56aJDrEC1O1xofekA&scope=email+openid+https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.email&authuser=2&prompt=none#";
		String partialCode=authURL.split("code=")[1];
		code=partialCode.split("&scope=")[0];

		System.out.println("Code is "+code);

	}


	public static void getAccessToken() {

		String getAccessTokenResponse=	given().urlEncodingEnabled(false).queryParams("code",code)
				.queryParams("client_id","692183103107-p0m7ent2hk7suguv4vq22hjcfhcr43pj.apps.googleusercontent.com")
				.queryParams("client_secret","erZOWM9g3UtwNRj340YYaK_W")
				.queryParams("redirect_uri","https://rahulshettyacademy.com/getCourse.php")
				.queryParams("grant_type","authorization_code")
				.when().post("https://www.googleapis.com/oauth2/v4/token")
				.then().log().all().assertThat().statusCode(200).extract().response().asString();

		JsonPath js = new JsonPath(getAccessTokenResponse);
		accessToken=js.get("access_token");

		System.out.println("Access Token is "+accessToken);

	}


	public static void getCourseDetails() {

		String responseofGetCourse=given().urlEncodingEnabled(false).queryParam("access_token", accessToken)
				.when()
				.get("https://rahulshettyacademy.com/getCourse.php")
				.then().assertThat().statusCode(200).extract().response().asString();

		System.out.println("The response of the course is "+responseofGetCourse);
	}


	public static void main(String args[]) {

		getAuthCode();
		getAccessToken();
		getCourseDetails();

	}

}
