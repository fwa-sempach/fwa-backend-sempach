package dev.elysion.fwa.dto;

public class InfoWrapper<T> {

	private Long totalCount;
	private Integer count;
	private T elements;

	public Long getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(Long totalCount) {
		this.totalCount = totalCount;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public T getElements() {
		return elements;
	}

	public void setElements(T elements) {
		this.elements = elements;
	}
}
