package de.unibayreuth.bayceer.oc.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.unibayreuth.bayceer.oc.parser.ReadmeParser;
import de.unibayreuth.bayceer.oc.parser.ReadmeParserException;

public class ReadmeMapAdapter {

	public static String key = ReadmeDocument.SYSTEM_FIELD_PREFIX + "key";
	public static String path = ReadmeDocument.SYSTEM_FIELD_PREFIX + "path";	
	public static String content = ReadmeDocument.SYSTEM_FIELD_PREFIX + "content";
	public static String lastModified = ReadmeDocument.SYSTEM_FIELD_PREFIX + "lastModified";
	public static String user = ReadmeDocument.SYSTEM_FIELD_PREFIX + "user";
	
	
	private static final Logger log = LoggerFactory.getLogger(ReadmeParser.class);	


	public static Map<String, Object> toMap(ReadmeDocument d, Map<String, String> mapping) throws ReadmeParserException {
		Map<String, Object> s = new HashMap<>();		
		s.put(key, d.getKey());
		s.put(path, d.getPath());		
		s.put(lastModified, d.getLastModified());
		s.put(user, d.getUser());
		s.put(content, d.getContent());		
						
		// Filter out invalid entries
		Map<String,List<String>> co = new HashMap<String, List<String>>();
		ReadmeParser.parseAsMap(d.getContent()).forEach((_key,_list) -> {
			List<String> tl = new ArrayList<String>();
			for(String value:_list) {			
				if ( mapping.containsKey(_key) && (!TypeValidator.isValid(value, mapping.get(_key))) ){
					log.warn("Key:{} type:{} value:{} is invalid.",_key,mapping.get(_key),value);
				} else {
					tl.add(value);
				}
			}
			co.put(_key,tl);						
		});
		s.putAll(co);
		
		return s;
	}

	
	public static ReadmeDocument fromMap(Map<String, Object> map) {
		ReadmeDocument ret = new ReadmeDocument();				
		ret.setKey((String) map.get(key));
		ret.setPath((String) map.get(path));		
		Object ls = map.get(lastModified);
		ret.setLastModified((ls==null)?null:Long.valueOf(ls.toString()));
		ret.setUser((String)map.get(user));		
		ret.setContent((String)map.get(content));		
		return ret;
	}
	
	

	
	

}
