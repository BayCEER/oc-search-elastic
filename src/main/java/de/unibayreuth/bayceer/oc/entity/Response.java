package de.unibayreuth.bayceer.oc.entity;

import java.util.List;



public class Response {
	private List<Hit> hits;
	private Long totalHits;
	private List<AggResponse> aggs;
	
	public Response() {
	
	}
		
	public Response(List<Hit> hits, Long totalHits, List<AggResponse> aggs) {
		super();
		this.hits = hits;
		this.totalHits = totalHits;
		this.aggs = aggs;
	}


	public List<Hit> getHits() {
		return hits;
	}
	public void setHits(List<Hit> hits) {
		this.hits = hits;
	}
	public Long getTotalHits() {
		return totalHits;
	}
	public void setTotalHits(Long totalHits) {
		this.totalHits = totalHits;
	}

	public List<AggResponse> getAggs() {
		return aggs;
	}

	public void setAggs(List<AggResponse> aggs) {
		this.aggs = aggs;
	}

		
	
	

}