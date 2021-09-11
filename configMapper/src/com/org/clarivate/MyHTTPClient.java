package com.org.clarivate;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;


public class MyHTTPClient {

	public static JsonObject call_me(String url) {
		JsonReader jsonReader = null;
	    JsonObject object = null;
		try {
			URL obj = new URL(url);
			System.out.println("object1");
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			System.out.println("object2");
			// optional default is GET
			con.setRequestMethod("GET");
			// add request header
			con.setRequestProperty("User-Agent", "Mozilla/5.0");
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			System.out.println("object3");
			String inputLine;
			StringBuffer response = new StringBuffer();
			System.out.println("object4");
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			System.out.println("object5");
			in.close();
			jsonReader = Json.createReader(new FileReader("/Users/rakeshkingsley/Desktop/myProjectCode/zuulprodconfig.json"));
			System.out.println("object6");
			object = jsonReader.readObject();
			System.out.println("object7");
		} catch (Exception ex) {
			System.out.println("Exeption in call_me : " + ex.getMessage());
		} finally {
			jsonReader.close();
			return object;
		}

	}

}
