package com;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

public class processYAML{
	@SuppressWarnings("unchecked")
	public static Service toService(String id) {
		Yaml yaml = new Yaml();
		InputStream in = new processYAML().getClass().getClassLoader().getResourceAsStream("services/"+id+".yml");
		Map<String , Object> yamlMaps = (Map<String , Object>)yaml.load(in);
		List<Map<String , Object>> microservices = (List<Map<String , Object>>)yamlMaps.get("microservices");
		Microservice[] ms = new Microservice[microservices.size()];
		int i = 0;
		for(Map<String , Object> msObj:microservices) {
			ms[i] = new Microservice((String)msObj.get("id"), (String)msObj.get("input"), (String)msObj.get("output"));
			i++;
		}
		//String id, double cost, double latency, double reliability, String input, String output, Microservice[] ms
		Service newService = new Service(
				(String)yamlMaps.get("id"),
				Double.valueOf((Integer)yamlMaps.get("cost")),
				Double.valueOf((Integer)yamlMaps.get("latency")),
				Double.valueOf((Integer)yamlMaps.get("reliability")),
				(String)yamlMaps.get("input"),
				(String)yamlMaps.get("output"),
				ms
				);
		return newService;
	}
}

