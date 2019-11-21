package de.unibayreuth.bayceer.oc;

@SuppressWarnings("serial")
public class NoSuchDocException extends Exception {
	
	String key;
	
	public NoSuchDocException() {
		
	}

	public NoSuchDocException(String key) {
		this.key = key;				
	}

	public String getKey() {
		return key;
	}
	
	
	
	

}
