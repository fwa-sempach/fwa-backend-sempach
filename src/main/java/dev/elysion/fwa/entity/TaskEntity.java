package dev.elysion.fwa.entity;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "task")
public class TaskEntity {
	private int id;
	private AdEntity ad;
	private String description;
	private Boolean deleted;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ad_id")
	public AdEntity getAd() {
		return ad;
	}

	public void setAd(AdEntity ad) {
		this.ad = ad;
	}

	@Basic
	@Column(name = "description", nullable = false, length = 100)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Basic
	@Column(name = "deleted", nullable = true)
	public Boolean getDeleted() {
		return deleted;
	}

	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		TaskEntity that = (TaskEntity) o;
		return id == that.id && Objects.equals(ad, that.ad) && Objects.equals(description, that.description) && Objects.equals(deleted, that.deleted);
	}

	@Override
	public int hashCode() {

		return Objects.hash(id, ad, description, deleted);
	}
}
