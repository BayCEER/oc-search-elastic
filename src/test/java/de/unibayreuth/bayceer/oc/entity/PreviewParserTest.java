package de.unibayreuth.bayceer.oc.entity;


import static org.junit.Assert.assertEquals;
import org.junit.Test;


public class PreviewParserTest {

	@Test
	public void test() {
		String t = "Prefix<em>Highlight</em>Postfix";		
		Preview p = PreviewAdapter.fromString(t);	
		assertEquals(p.preFix,"Prefix");
		assertEquals(p.getHighlight(),"Highlight");
		assertEquals(p.postFix,"Postfix");
	}

}
