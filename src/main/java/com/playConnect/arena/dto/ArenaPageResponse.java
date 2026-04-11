package com.playConnect.arena.dto;

import java.util.List;

public class ArenaPageResponse {

	private List<ArenaResponse> content;
	private int page;
	private int size;
	private long totalElements;
	private int totalPages;

	public ArenaPageResponse(List<ArenaResponse> content, int page, int size, long totalElements) {
		this.content = content;
		this.page = page;
		this.size = size;
		this.totalElements = totalElements;
		this.totalPages = size > 0 ? (int) Math.ceil((double) totalElements / size) : 0;
	}

	public List<ArenaResponse> getContent() {
		return content;
	}

	public int getPage() {
		return page;
	}

	public int getSize() {
		return size;
	}

	public long getTotalElements() {
		return totalElements;
	}

	public int getTotalPages() {
		return totalPages;
	}
}
