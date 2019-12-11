package de.unibayreuth.bayceer.oc.entity;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.StringUtils;

public class AggResponse {	
	
	String key;	
	long sumOtherDocCount;
	List<TermCount> results;
	String title;
	
	
	public AggResponse() {
		this.results = new ArrayList<TermCount>(10);
	}
	
		
	public AggResponse(String key, long sumOtherDocCounts) {
		this();
		this.key = key;
		this.sumOtherDocCount = sumOtherDocCounts;		
	}

	public Long getSumOtherDocCount() {
		return sumOtherDocCount;
	}
	public void setSumOtherDocCount(Long sumOtherDocCount) {
		this.sumOtherDocCount = sumOtherDocCount;
	}
	
	public void buildTitle() {
		String k = (key.startsWith(ReadmeDocument.SYSTEM_FIELD_PREFIX)?key.substring(1):key);
		this.title = String.format("%s (%s)",StringUtils.capitalize(k), (sumOtherDocCount>0)?">" + results.size():results.size());		
	}

	
	
	public void setSumOtherDocCount(long sumOtherDocCount) {
		this.sumOtherDocCount = sumOtherDocCount;
	}


	public List<TermCount> getResults() {
		return results;
	}


	public void setResults(List<TermCount> results) {
		this.results = results;
	}


	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}


	public String getKey() {
		return key;
	}


	public void setKey(String key) {
		this.key = key;
	}

}
