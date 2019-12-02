package de.unibayreuth.bayceer.oc.entity;

public class Preview {
	
	String preFix;
	String highlight;
	String postFix;
	
	public Preview() {
	
	}

	
	public Preview(String preFix, String highlight, String postFix) {
		super();
		this.preFix = preFix;
		this.highlight = highlight;
		this.postFix = postFix;
	}


	public String getPreFix() {
		return preFix;
	}

	public void setPreFix(String preFix) {
		this.preFix = preFix;
	}

	public String getHighlight() {
		return highlight;
	}

	public void setHighlight(String highlight) {
		this.highlight = highlight;
	}

	public String getPostFix() {
		return postFix;
	}

	public void setPostFix(String postFix) {
		this.postFix = postFix;
	}
	
	
			

}
