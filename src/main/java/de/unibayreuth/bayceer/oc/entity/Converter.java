package de.unibayreuth.bayceer.oc.entity;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;


public class Converter {
	
	public static final List<String> stringToArray(String value) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper om = new ObjectMapper();
		JavaType type = om.getTypeFactory().constructCollectionType(List.class, String.class);
		return om.readValue(value, type);		
	}
	
	public static final Map<String,List<String>> stringToMap(String value) throws JsonParseException, JsonMappingException, IOException{
		ObjectMapper om = new ObjectMapper();
		return om.readValue(value, new TypeReference<Map<String, List<String>>>() {
		});
		
	}
	
	
	

}
