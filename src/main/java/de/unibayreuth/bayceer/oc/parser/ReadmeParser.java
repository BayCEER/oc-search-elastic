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

	// https://regex101.com/
	// Matches all key values
	
	
	private static final Pattern p = Pattern.compile("^(.+):(.*)");
	
	private static Logger log = LoggerFactory.getLogger(ReadmeParser.class);

	public static List<SimpleEntry<String, String>> parse(String content) throws ReadmeParserException {
		List<SimpleEntry<String, String>> ret = new ArrayList<SimpleEntry<String, String>>(10);
		try (BufferedReader br = new BufferedReader(new StringReader(content))) {
			String line;
			Boolean onKey = false;
			while ((line = br.readLine()) != null) {
				Matcher matcher = p.matcher(line);
				if (matcher.matches()) {
					String key = matcher.group(1);
					String value = matcher.group(2);
					if (value.isEmpty() || key.isEmpty()) {
						onKey = false;
					} else {
						ret.add(new SimpleEntry<String, String>(key.trim(), value));
						onKey = true;
					}
				} else {
					// Ignoring comments 
					if (!line.trim().startsWith("#")) {					
						// Handle values spanning many lines  
						if (ret.size() > 0 && onKey) {
							SimpleEntry<String, String> lastEntry = ret.get(ret.size() - 1);
							StringBuffer b = new StringBuffer(lastEntry.getValue());
							b.append("\n");
							b.append(line);
							lastEntry.setValue(b.toString());
						}
					}
				}

			}
		} catch (IOException e) {
			throw new ReadmeParserException(e.getMessage());
		}

		return ret;
	}
	
	public static Map<String,String> parseAsMap(String content) throws ReadmeParserException {				
		Map<String,String> ret = new HashMap<String, String>();
		for (SimpleEntry<String, String> a : parse(content)) {
			if (!a.getKey().startsWith(ReadmeDocument.SYSTEM_FIELD_PREFIX)) {
				ret.put(a.getKey(), a.getValue());
			} else {
				log.warn("Ignoring system field:{}",a.getKey());
			}
		}
		return ret;
		
	}

}
