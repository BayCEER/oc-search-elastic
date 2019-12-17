package de.unibayreuth.bayceer.oc.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.unibayreuth.bayceer.oc.entity.ReadmeDocument;

public class ReadmeParser {

				
	public static final String START_COMMENT_CHAR = "#";
	public static final String LIST_SEPERATOR = ";";

	
	// Verified on https://regex101.com/
	private static final Pattern p = Pattern.compile("([^:]*):(.*)");
	
	private static Logger log = LoggerFactory.getLogger(ReadmeParser.class);

	public static List<SimpleEntry<String, String>> parse(String content) throws ReadmeParserException {
		List<SimpleEntry<String, String>> ret = new ArrayList<SimpleEntry<String, String>>(10);
		
		try (BufferedReader br = new BufferedReader(new StringReader(content))) {
			String line;			
			while ((line = br.readLine()) != null) {
				// Skip Comments 
				if (!line.trim().startsWith(START_COMMENT_CHAR)) {					
					// Handle Block
					if (line.startsWith(" ") && ret.size()>0) {						
						SimpleEntry<String, String> lastEntry = ret.get(ret.size() - 1);
						StringBuffer b = new StringBuffer(lastEntry.getValue());
						b.append("\n");
						b.append(line.trim());
						lastEntry.setValue(b.toString());												
					} else {						
						Matcher matcher = p.matcher(line);
						if (matcher.matches()) {
							String key = matcher.group(1).trim();
							String value = matcher.group(2).trim();
							if (!(value.isEmpty() || key.isEmpty())) {								
								if (value.contains(LIST_SEPERATOR)) {
									for(String e :value.split(LIST_SEPERATOR)) {
										ret.add(new SimpleEntry<String, String>(key, e.trim()));
									}							
								} else {
									ret.add(new SimpleEntry<String, String>(key, value));	
								}
							}
						} 																		
					}
				}							
			}
		} catch (IOException e) {
			throw new ReadmeParserException(e.getMessage());
		}
		return ret;
	}
	
	public static Map<String,List<String>> parseAsMap(String content) throws ReadmeParserException {				
		Map<String,List<String>> ret = new HashMap<String, List<String>>();
		for (SimpleEntry<String, String> a : parse(content)) {
			if (!a.getKey().startsWith(ReadmeDocument.SYSTEM_FIELD_PREFIX)) {				
				if (ret.containsKey(a.getKey())) {
					ret.get(a.getKey()).add(a.getValue()); 
				} else {
					ArrayList<String> l = new ArrayList<String>();
					l.add(a.getValue());
					ret.put(a.getKey(), l);
				}								
			} else {
				log.warn("Ignoring system field:{}",a.getKey());
			}
		}
		return ret;
		
	}

}
