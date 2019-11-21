package de.unibayreuth.bayceer.oc.entity;

public class TermCount {
	
	private String key;
	private Long count;
	
	public TermCount() {
	}
	
	public TermCount(String key, Long count) {
		super();
		this.key = key;
		this.count = count;
	}
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public Long getCount() {
		return count;
	}
	public void setCount(Long count) {
		this.count = count;
	}

}
