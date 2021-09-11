package com.org.clarivate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonValue;

public class FileHandler {

	private static List<String> getAllZuulJsonFiles(String dirPath) {
		List<String> filePaths = new ArrayList<String>();
		// Creating a File object for directory
		File directoryPath = new File(dirPath);
		FilenameFilter jsonFilesList = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				String lowercaseName = name.toLowerCase();
				if (lowercaseName.endsWith(".json")) {
					return true;
				} else {
					return false;
				}
			}
		};
		for (File file : directoryPath.listFiles(jsonFilesList)) {
			filePaths.add(file.getAbsolutePath());
		}
		return filePaths;
	}

	private static JsonObject getConsolidatedZuulJson(List<String> filePaths) {

		JsonObject consolidatedJson = null;
		InputStream fis;
		JsonReader reader = null;
		JsonObject currentJson;
		try {
			for (String filePath : filePaths) {
				fis = new FileInputStream(filePath);
				reader = Json.createReader(fis);
				currentJson = reader.readObject();
				if (consolidatedJson != null) {
					JsonArray filters = consolidatedJson.getJsonArray("filters");
					JsonArray citeMaps = consolidatedJson.getJsonArray("citeMap");
					JsonArray clients = consolidatedJson.getJsonArray("clients");
					JsonArrayBuilder filterBuilder = Json.createArrayBuilder();
					for (JsonValue jsVal : filters) {
						filterBuilder.add(jsVal);
					}
					if (currentJson.containsKey("filters")) {
						for (JsonValue jsVal : currentJson.getJsonArray("filters")) {
							filterBuilder.add(jsVal);
						}
					}
					JsonArray newFilterArray = filterBuilder.build();
					JsonArrayBuilder citeMapsBuilder = Json.createArrayBuilder();
					for (JsonValue jsVal : citeMaps) {
						citeMapsBuilder.add(jsVal);
					}
					if (currentJson.containsKey("citeMap")) {
						for (JsonValue jsVal : currentJson.getJsonArray("citeMap")) {
							citeMapsBuilder.add(jsVal);
						}
					}
					JsonArray newCiteMapsArray = citeMapsBuilder.build();
					JsonArrayBuilder clientsBuilder = Json.createArrayBuilder();
					for (JsonValue jsVal : clients) {
						clientsBuilder.add(jsVal);
					}
					if (currentJson.containsKey("clients")) {
						for (JsonValue jsVal : currentJson.getJsonArray("clients")) {
							clientsBuilder.add(jsVal);
						}
					}
					JsonArray newClientsArray = clientsBuilder.build();
					JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
					jsonObjectBuilder.add("filters", newFilterArray);
					jsonObjectBuilder.add("citeMap", newCiteMapsArray);
					jsonObjectBuilder.add("clients", newClientsArray);
					consolidatedJson = jsonObjectBuilder.build();
				} else {
					consolidatedJson = currentJson;
				}
			}
			if (reader != null)
				reader.close();
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		}
		return consolidatedJson;
	}

	public static JsonObject getZuulConfigJson(String dirPath) {
		return getConsolidatedZuulJson(getAllZuulJsonFiles(dirPath));
	}

	private static Map<String, JsonObject> getAllKongJsonFiles(String dirPath) {

		Map<String, JsonObject> kongConfigMap = new HashMap<String, JsonObject>();
		InputStream fis;
		JsonReader reader = null;
		try {
			fis = new FileInputStream(dirPath + "/services.json");
			reader = Json.createReader(fis);
			kongConfigMap.put("services", reader.readObject());
			reader.close();
			fis = new FileInputStream(dirPath + "/plugins.json");
			reader = Json.createReader(fis);
			kongConfigMap.put("plugins", reader.readObject());
			reader.close();
			fis = new FileInputStream(dirPath + "/routes.json");
			reader = Json.createReader(fis);
			kongConfigMap.put("routes", reader.readObject());
			reader.close();
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		}
		return kongConfigMap;
	}

	public static Map<String, JsonObject> getKongConfigJson(String dirPath) {
		return getAllKongJsonFiles(dirPath);
	}

	public static void insertIntoExcel(List<String> excelInput, String fileName) {
		try {
			FileWriter writer = new FileWriter(fileName + ".csv");
			for (String currentLine : excelInput) {
				writer.write(currentLine);
				writer.write("\n");
			}
			writer.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	public static JsonObject getJsonData() {
		JsonReader jsonReader = null;
		JsonObject object = null;
		try {
			jsonReader = Json
					.createReader(new FileReader("/Users/rakeshkingsley/Desktop/myProjectCode/zuulprodconfig.json"));
			object = jsonReader.readObject();

		} catch (Exception ex) {
			System.out.println("Exeption in getJsonData : " + ex.getMessage());
		} finally {
			jsonReader.close();
			return object;
		}
	}

}
