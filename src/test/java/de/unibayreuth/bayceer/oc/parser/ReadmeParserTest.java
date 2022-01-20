package de.unibayreuth.bayceer.oc.parser;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ReadmeParserTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void parseSimple() throws IOException, ReadmeParserException {
		String content = new String(Files.readAllBytes(Paths.get("src/test/resources/READMEdc_simple.txt")));		
		List<SimpleEntry<String, String>> dc = ReadmeParser.parse(content);
		assertEquals(12,dc.size());
	}
	
	@Test 
	public void parseInvalid() throws IOException, ReadmeParserException {
		String content = new String(Files.readAllBytes(Paths.get("src/test/resources/READMEdc_invalid.txt")));		
		List<SimpleEntry<String, String>> dc = ReadmeParser.parse(content);
		assertEquals(2, dc.size());
	}
	
	@Test
	public void parseMultilineKey() throws IOException, ReadmeParserException {
		String content = new String(Files.readAllBytes(Paths.get("src/test/resources/READMEdc_multilinekey.txt")));		
		List<SimpleEntry<String, String>> dc = ReadmeParser.parse(content);
		assertEquals(1,dc.size());
		assertEquals("title", dc.get(0).getKey());
		assertEquals("Line1\nLine2", dc.get(0).getValue());
	}
		
	@Test
	public void parseBOMContent() throws IOException, ReadmeParserException {
			String content = new String(Files.readAllBytes(Paths.get("src/test/resources/READMEdc_bom.txt")));		
			List<SimpleEntry<String, String>> dc = ReadmeParser.parse(content);
			assertEquals(1,dc.size());
			assertEquals("title", dc.get(0).getKey());
			assertEquals("BOM is here", dc.get(0).getValue());			
	}
	
	@Test
	public void parseKeySpace() throws IOException, ReadmeParserException {
		String content = new String(Files.readAllBytes(Paths.get("src/test/resources/READMEdc_keyspace.txt")));		
		List<SimpleEntry<String, String>> dc = ReadmeParser.parse(content);
		assertEquals(1,dc.size());
		assertEquals("key space", dc.get(0).getKey());
		assertEquals("value", dc.get(0).getValue());
	}
	
			
	@Test
	public void parseAsList() throws IOException, ReadmeParserException {	
		String content = new String(Files.readAllBytes(Paths.get("src/test/resources/READMEdc_list.txt")));
		List<SimpleEntry<String, String>> dc = ReadmeParser.parse(content);
		assertEquals(2,dc.size());
		assertEquals("creator", dc.get(0).getKey());
		assertEquals("Oliver Archner", dc.get(0).getValue());
		assertEquals("creator", dc.get(1).getKey());
		assertEquals("Stefan Holzheu", dc.get(1).getValue());
	}
	
	@Test
	public void parseAsMap() throws IOException, ReadmeParserException {	
		String content = new String(Files.readAllBytes(Paths.get("src/test/resources/READMEdc_list.txt")));
		Map<String,List<String>> dc = ReadmeParser.parseAsMap(content);		
		assertEquals(1,dc.size());		
		assertEquals(true, dc.get("creator").contains("Oliver Archner"));
		assertEquals(true, dc.get("creator").contains("Stefan Holzheu"));		
	}
	
	@Test
	public void parseColonValue() throws IOException, ReadmeParserException {
		String content = "creator: Oliver:Archner";
		List<SimpleEntry<String, String>> dc = ReadmeParser.parse(content);
		assertEquals(1,dc.size());		
		assertEquals("Oliver:Archner", dc.get(0).getValue());
		
	}
	
	
	
	
}
