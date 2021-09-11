package com.org.clarivate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonValue;

public class KongConfigMaps {

	String url;

	public KongConfigMaps(String env) {
		if (env.equals("prod")) {
			url = "http://access.clarivate.com/api/zuulStats/";
		} else if (env.equals("stable")) {
			url = "http://access.dev-stable.clarivate.com/api/zuulStats/";
		} else {
			url = "http://access.dev-snapshot.clarivate.com/api/zuulStats/";
		}
	}

	List<String> getKongConfig(String dirPath) {
		Map<String, JsonObject> kongConfigMap = FileHandler.getKongConfigJson(dirPath);
		List<String> kongConfigList = new ArrayList<String>();
		try {
			JsonArray services = kongConfigMap.get("services").getJsonArray("data");
			JsonArray plugins = kongConfigMap.get("plugins").getJsonArray("data");
			JsonArray routesPaths = kongConfigMap.get("routes").getJsonArray("data");
			JsonArrayBuilder filterBuilder = Json.createArrayBuilder();
			for (JsonValue plugin : plugins) {
				JsonObject pluginObj = plugin.asJsonObject();
				if(pluginObj.containsKey("enabled") && pluginObj.getBoolean("enabled") ) {
					filterBuilder.add(plugin);
				}
			}
			plugins = filterBuilder.build();
			for (JsonValue serviceValue : services) {
				JsonObject serviceObject = serviceValue.asJsonObject();
				String finalString = "";
				String name = "";
				String path = "";
				String vipAddress = "";
				String hosts = "";
				String upstreamHost = "";
				String upstreamPath = "";
				String servicePlugins = "";
				boolean stripPath = false;
				if (serviceObject.containsKey("name")) {
					name = serviceObject.getString("name");
				}
				if (serviceObject.containsKey("host")) {
					upstreamHost = serviceObject.getString("host");
				}
				if (serviceObject.containsKey("path")) {
					upstreamPath = serviceObject.getString("path");
				}
				for (JsonValue pluginValue : plugins) {
					JsonObject servicePlugin = pluginValue.asJsonObject();
					if (servicePlugin.containsKey("service") && !servicePlugin.isNull("service")) {
						JsonObject pluginObj = servicePlugin.getJsonObject("service");
						if (pluginObj != null && pluginObj.containsKey("id") && !pluginObj.isNull("id")) {
							if (serviceObject.getString("id").equals(pluginObj.getString("id"))) {
								servicePlugins += servicePlugin.getString("name") + "||";
								if (servicePlugin.getString("name").equals("eureka-router")) {
									if (!servicePlugin.isNull("config")) {
										if (!servicePlugin.getJsonObject("config").isNull("vipAddress"))
											vipAddress = servicePlugin.getJsonObject("config").getString("vipAddress");
										else if (!servicePlugin.getJsonObject("config").isNull("staticAddress"))
											vipAddress = servicePlugin.getJsonObject("config")
													.getString("staticAddress");
									}

								}
							}
						}
					}
				}
				for (JsonValue routeValue : routesPaths) {
					JsonObject routeObject = routeValue.asJsonObject();
					if (routeObject.containsKey("service")) {
						JsonObject route = routeObject.getJsonObject("service");
						if (route != null && route.containsKey("id") && !route.isNull("id")) {
							if (serviceObject.getString("id").equals(route.getString("id"))) {
								if (routeObject.containsKey("paths") && !routeObject.isNull("paths")
										&& routeObject.getJsonArray("paths").size() > 0) {
									for (JsonValue routePathValues : routeObject.getJsonArray("paths")) {
										path += routePathValues.toString() + "||";
									}
								}
								if (routeObject.containsKey("hosts") && !routeObject.isNull("hosts")
										&& routeObject.getJsonArray("hosts").size() > 0) {
									for (JsonValue hostValues : routeObject.getJsonArray("hosts")) {
										hosts += hostValues.toString() + "||";
									}
								}
								if (routeObject.containsKey("strip_path") && routeObject.getBoolean("strip_path")) {
									stripPath = true;
								}
							}
						}
					}
				}
				finalString = (vipAddress.equals("")?"null":vipAddress) + "," + (upstreamHost.equals("")?"null":upstreamHost) + "," + (upstreamPath.equals("")?"null":upstreamPath) + "," + (path.equals("")?"null":path) + "," + (hosts.equals("")?"null":hosts) + ","
						+ stripPath + "," + (servicePlugins.equals("")?"null":servicePlugins) + "," + (name.equals("")?"null":name);
				if (finalString != "")
					kongConfigList.add(finalString);
			}
		} catch (JsonException je) {
			System.out.println("JSONException : " + je.getMessage());
		}
		return kongConfigList;
	}

}
