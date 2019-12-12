package de.unibayreuth.bayceer.oc.entity;

import java.util.List;
import java.util.Map;

public class Hit {
	private String key;
	private String path;
	private Map<String,List<String>> previews;
	private Float score;
	private byte[] thumb;

	public Hit() {

	}

	public Hit(String key, Float score, String path, Map<String,List<String>> previews, byte[] thumb) {
		super();
		this.key = key;
		this.score = score;
		this.path = path;
		this.previews = previews;
		this.thumb = thumb;
	}

	public String getKey() {
		return key;
	}

	public String getPath() {
		return path;
	}

	
	public Float getScore() {
		return score;
	}

	public byte[] getThumb() {
		return thumb;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public void setPath(String path) {
		this.path = path;
	}

	
	public void setScore(Float score) {
		this.score = score;
	}

	public void setThumb(byte[] thumb) {
		this.thumb = thumb;
	}

	public Map<String, List<String>> getPreviews() {
		return previews;
	}

	public void setPreviews(Map<String, List<String>> previews) {
		this.previews = previews;
	}
}