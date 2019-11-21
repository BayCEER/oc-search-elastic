package de.unibayreuth.bayceer.oc.entity;

import java.util.List;

public class Hit {
	private String key;
	private String path;
	private List<String> previews;
	private Float score;
	private byte[] thumb;

	public Hit() {

	}

	public Hit(String key, Float score, String path, List<String> previews, byte[] thumb) {
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

	public List<String> getPreviews() {
		return previews;
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

	public void setPreviews(List<String> previews) {
		this.previews = previews;
	}

	public void setScore(Float score) {
		this.score = score;
	}

	public void setThumb(byte[] thumb) {
		this.thumb = thumb;
	}
}