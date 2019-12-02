package de.unibayreuth.bayceer.oc.entity;

public class PreviewAdapter {
	
			 
	public static Preview fromString(String text) {		
		int startPrefix = text.indexOf("<em>");
		int stopPrefix = text.indexOf("</em>");		
		String prefix = text.substring(0,startPrefix);
		String match = text.substring(startPrefix + 4,stopPrefix);
		String postfix = text.substring(stopPrefix + 5);								
		return new Preview(prefix,match,postfix);
	}
	
		

}
