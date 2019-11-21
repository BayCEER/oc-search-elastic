package de.unibayreuth.bayceer.oc.entity;

import java.util.ArrayList;
import java.util.List;

public class AggResponse {	
	
	String name;
	long sumOtherDocCount;
	List<TermCount> results;
	
	public AggResponse() {
		this.results = new ArrayList<TermCount>(10);
	}
	
		
	public AggResponse(String name, long sumOtherDocCounts) {
		this();
		this.name = name;
		this.sumOtherDocCount = sumOtherDocCounts;		
	}

	public Long getSumOtherDocCount() {
		return sumOtherDocCount;
	}
	public void setSumOtherDocCount(Long sumOtherDocCount) {
		this.sumOtherDocCount = sumOtherDocCount;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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
}
