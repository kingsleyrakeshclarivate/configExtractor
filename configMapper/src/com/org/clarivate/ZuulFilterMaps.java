package com.org.clarivate;

import java.util.ArrayList;
import java.util.List;

import javax.json.JsonArray;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonValue;

public class ZuulFilterMaps {

	String url;

	public ZuulFilterMaps(String env) {
		if (env.equals("prod")) {
			url = "http://access.clarivate.com/api/zuulStats/";
		} else if (env.equals("stable")) {
			url = "http://access.dev-stable.clarivate.com/api/zuulStats/";
		} else if (env.equals("shared")) {
			url = "http://1p-zuul.us-west-2.dev.shared.oneplatform.build/api/zuulStats/";
		} else {
			url = "http://access.dev-snapshot.clarivate.com/api/zuulStats/";
		}
	}

	List<String> getZuulConfigFromZuulStats() {
		List<String> filterList = new ArrayList<String>();
		try {
			JsonObject citeMaps = (JsonObject) FileHandler.getJsonData();
			System.out.println("Test1");
			JsonObject filters = (JsonObject) FileHandler.getJsonData().get("filters");
			System.out.println("Test2");
			JsonObject clients = (JsonObject) FileHandler.getJsonData().get("clients");
			System.out.println("Test3");
			for (JsonValue jv : filters.getJsonArray("pre")) {
				JsonObject jo = jv.asJsonObject();
				JsonObject accessInfo = jo.getJsonObject("accessInfo");
				String finalString = "";
				String name = "";
				String path = "";
				String targetPath = "";
				String subFilters = "";
				String authType = "";
				String client = "";
				String vipAddress = "";
				if (accessInfo != null && accessInfo.getInt("countExecution") > 0) {
					String routes = "";
					String hosts = "";
					boolean ipRange = false;
					if (jo.containsKey("name")) {
						name = jo.getString("name");
						for (JsonValue citeValue : citeMaps.getJsonArray("citeMaps")) {
							JsonObject citeObject = citeValue.asJsonObject();
							if (citeObject.containsKey("target")) {
								if (citeObject.getString("target").equals(name)) {
									if (citeObject.containsKey("path")) {
										routes += citeObject.getString("path") + "||";
									}
									if (citeObject.containsKey("host")) {
										hosts += citeObject.getString("host") + "||";
									}
									if (citeObject.containsKey("ip_range")) {
										ipRange = true;
									}
								}
								if (citeObject.containsKey("children")) {
									for (JsonValue childCiteMap : citeObject.getJsonArray("children")) {
										JsonObject childCiteMapObj = childCiteMap.asJsonObject();
										if (childCiteMapObj.containsKey("target")
												&& childCiteMapObj.getString("target").equals(name)) {
											if (childCiteMapObj.containsKey("path")) {
												routes += childCiteMapObj.getString("path") + "||";
											} else {
												if (citeObject.containsKey("path")) {
													path = citeObject.getString("path");
													routes = path + "||";
												}
											}
											if (childCiteMapObj.containsKey("host")) {
												hosts += childCiteMapObj.getString("host") + "||";
											}
										}
									}
								}
							}
						}
					}
					if (jo.containsKey("path")) {
						path = jo.getString("path");
					}
					if (jo.containsKey("targetPath")) {
						targetPath = jo.getString("targetPath");
					}
					if (jo.containsKey("subFilters")) {
						subFilters = jo.getJsonArray("subFilters").get(0).toString();
					}
					if (jo.containsKey("authType")) {
						authType = jo.getString("authType");
					}
					if (jo.containsKey("client")) {
						client = jo.getString("client");
						JsonObject clientObject = clients.getJsonArray("clients").stream().map(JsonValue::asJsonObject)
								.filter(cliObj -> cliObj.containsKey("name"))
								.filter(cliObj -> cliObj.getString("name").equals(jo.getString("client"))).findFirst()
								.orElse(null);
						if (clientObject.containsKey("urls") && clientObject.getJsonArray("urls").size() > 0) {
							vipAddress = clientObject.get("properties").asJsonObject()
									.getString("DeploymentContextBasedVipAddresses");
						} else {
							continue;
						}
					}
					finalString =  (vipAddress.equals("")?"null":vipAddress) + "," + (routes.equals("")?"null":routes) + "," + (path.equals("")?"null":path) + "," + (targetPath.equals("")?"null":targetPath) + "," + (hosts.equals("")?"null":hosts) + "," + ipRange
							+ "," + (subFilters.equals("")?"null":subFilters) + "," + (authType.equals("")?"null":authType) + "," + (client.equals("")?"null":client) + "," + (name.equals("")?"null":name);
					if (finalString != "")
						filterList.add(finalString);
				}
			}
		} catch (JsonException je) {
			System.out.println("JSONException : " + je.getMessage());
		}
		return filterList;
	}

	List<String> getZuulConfigFromJsonConfig(String dirPath) {
		JsonObject masterJsonConfig = FileHandler.getZuulConfigJson(dirPath);
		List<String> filterList = new ArrayList<String>();
		try {
			JsonArray citeMaps = masterJsonConfig.getJsonArray("citeMap");
			JsonArray filters = masterJsonConfig.getJsonArray("filters");
			JsonArray clients = masterJsonConfig.getJsonArray("clients");
			for (JsonValue jv : filters) {
				JsonObject jo = jv.asJsonObject();
				String finalString = "";
				String name = "";
				String path = "";
				String targetPath = "";
				String subFilters = "";
				String entitlements = "";
				String authType = "";
				String client = "";
				String vipAddress = "";
				String routes = "";
				String hosts = "";
				boolean ipRange = false;
				for(String jsonKeyName : jo.keySet()) {
					name = jsonKeyName;
				}
				if(name != "") {
					JsonObject filterObject = jo.getJsonObject(name);
					if (filterObject.containsKey("path")) {
						path = filterObject.getString("path");
					}
					if (filterObject.containsKey("targetPath")) {
						targetPath = filterObject.getString("targetPath");
					}
					if (filterObject.containsKey("subFilters")) {
						subFilters = filterObject.getJsonArray("subFilters").get(0).toString();
					}
					if (filterObject.containsKey("authType")) {
						authType = filterObject.getString("authType");
					}
					if (filterObject.containsKey("entitlements")) {
						for(JsonValue entitlement : filterObject.getJsonArray("entitlements")) {
							entitlements += entitlement.toString() + "||";
						}
					}
					if (filterObject.containsKey("client")) {
						client = filterObject.getString("client");
						JsonObject clientObject = null;
						for(JsonValue clientJsonObj : clients) {
							if(clientJsonObj.asJsonObject().containsKey(client)) {
								clientObject = clientJsonObj.asJsonObject().getJsonObject(client);
							}
						}
						if(clientObject.containsKey("DeploymentContextBasedVipAddresses")) {
							vipAddress = clientObject.getString("DeploymentContextBasedVipAddresses");
						} else if(clientObject.containsKey("name")) {
							vipAddress = clientObject.getString("name");
						}
					}
					for (JsonValue citeValue : citeMaps) {
						JsonObject citeObject = citeValue.asJsonObject();
						if (citeObject.containsKey("target")) {
							if (citeObject.getString("target").equals(name)) {
								if (citeObject.containsKey("path")) {
									routes += citeObject.getString("path") + "||";
								}
								if (citeObject.containsKey("host")) {
									hosts += citeObject.getString("host") + "||";
								}
								if (citeObject.containsKey("ip_range")) {
									ipRange = true;
								}
							}
							if (citeObject.containsKey("children")) {
								for (JsonValue childCiteMap : citeObject.getJsonArray("children")) {
									JsonObject childCiteMapObj = childCiteMap.asJsonObject();
									if (childCiteMapObj.containsKey("target")
											&& childCiteMapObj.getString("target").equals(name)) {
										if (childCiteMapObj.containsKey("path")) {
											routes += childCiteMapObj.getString("path") + "||";
										} else {
											if (citeObject.containsKey("path")) {
												path = citeObject.getString("path");
												routes = path + "||";
											}
										}
										if (childCiteMapObj.containsKey("host")) {
											hosts += childCiteMapObj.getString("host") + "||";
										}
									}
								}
							}
						}
					}
					finalString = (vipAddress.equals("")?"null":vipAddress) + "," + (routes.equals("")?"null":routes) + "," + (path.equals("")?"null":path) + "," + (targetPath.equals("")?"null":targetPath) + "," + (hosts.equals("")?"null":hosts) + "," + ipRange + ","
							+ (subFilters.equals("")?"null":subFilters) + "," + (authType.equals("")?"null":authType) + "," + (entitlements.equals("")?"null":entitlements) + "," + (client.equals("")?"null":client) + "," + (name.equals("")?"null":name);
					if (finalString != "")
						filterList.add(finalString);
				}
			}
		} catch (JsonException je) {
			System.out.println("JSONException : " + je.getMessage());
		}
		return filterList;
	}

}
