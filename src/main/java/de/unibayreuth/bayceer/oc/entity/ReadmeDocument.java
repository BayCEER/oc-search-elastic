package de.unibayreuth.bayceer.oc.entity;

public class ReadmeDocument {
	
	private String key;
	private String path;	
	private String content;	
	private Long lastModified;
	private String user;
			
	public static final String SYSTEM_FIELD_PREFIX = "_";
	
	public ReadmeDocument() {
	
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	
	public Long getLastModified() {
		return lastModified;
	}

	public void setLastModified(Long lastModified) {
		this.lastModified = lastModified;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String userName) {
		this.user = userName;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	
}
