package de.unibayreuth.bayceer.oc.entity;

import java.util.HashMap;
import java.util.Map;

public class ReadmeMapAdapter {

	public static String key = ReadmeDocument.SYSTEM_FIELD_PREFIX + "key";
	public static String path = ReadmeDocument.SYSTEM_FIELD_PREFIX + "path";
	public static String content = ReadmeDocument.SYSTEM_FIELD_PREFIX + "content";
	public static String lastModified = ReadmeDocument.SYSTEM_FIELD_PREFIX + "lastModified";

	public static Map<String, Object> asMap(ReadmeDocument d) {
		Map<String, Object> s = new HashMap<>();
		s.put(key, d.getKey());
		s.put(path, d.getPath());
		s.put(content, d.getContent());
		s.put(lastModified, d.getLastModified());
		return s;
	}

	public static ReadmeDocument fromMap(Map<String, Object> map) {
		ReadmeDocument ret = new ReadmeDocument();

		ret.setKey((String) map.get(key));
		ret.setPath((String) map.get(path));
		ret.setContent((String) map.get(content));
		Object ls = map.get(lastModified);
		ret.setLastModified((ls==null)?null:Long.valueOf(ls.toString()));			
		
		return ret;
	}
	
	

}
