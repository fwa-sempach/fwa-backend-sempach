package dev.elysion.fwa.dto;

import java.time.LocalDateTime;
import java.util.List;

public class Ad {
	private int id;
	private Offer offer;
	private Image image;
	private String title;
	private int numberOfVolunteersNeeded;
	private LocalDateTime releaseDate;
	private LocalDateTime validUntil;
	private boolean active;
	private boolean deleted;
	private List<BasicCondition> basicConditions;
	private List<Task> tasks;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Offer getOffer() {
		return offer;
	}

	public void setOffer(Offer offer) {
		this.offer = offer;
	}

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getNumberOfVolunteersNeeded() {
		return numberOfVolunteersNeeded;
	}

	public void setNumberOfVolunteersNeeded(int numberOfVolunteersNeeded) {
		this.numberOfVolunteersNeeded = numberOfVolunteersNeeded;
	}

	public LocalDateTime getReleaseDate() {
		return releaseDate;
	}

	public void setReleaseDate(LocalDateTime releaseDate) {
		this.releaseDate = releaseDate;
	}

	public LocalDateTime getValidUntil() {
		return validUntil;
	}

	public void setValidUntil(LocalDateTime validUntil) {
		this.validUntil = validUntil;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public List<BasicCondition> getBasicConditions() {
		return basicConditions;
	}

	public void setBasicConditions(List<BasicCondition> basicConditions) {
		this.basicConditions = basicConditions;
	}

	public List<Task> getTasks() {
		return tasks;
	}

	public void setTasks(List<Task> tasks) {
		this.tasks = tasks;
	}
}
