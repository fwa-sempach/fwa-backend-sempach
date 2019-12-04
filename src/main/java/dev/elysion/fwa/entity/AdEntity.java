package dev.elysion.fwa.entity;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "ad")
@NamedQueries({@NamedQuery(name = AdEntity.QUERY_READ_ALL, query = "select a from AdEntity a"), @NamedQuery(
		name = AdEntity.QUERY_READ_BY_ORGANISATION_ID,
		query = "select a from AdEntity a where a.offer.organisation.id = :organisationId"), @NamedQuery(
		name = AdEntity.QUERY_READ_BY_CATEGORY_ID,
		query = "select a from AdEntity a where a.offer.category.id = :categoryId"), @NamedQuery(
		name = AdEntity.QUERY_READ_BY_OFFER_ID, query = "select a from AdEntity a where a.offer.id = :offerId")})

public class AdEntity {
	public static final String QUERY_READ_ALL = "ads.readAll";
	public static final String QUERY_READ_BY_ORGANISATION_ID = "ads.readByOrganisationId";
	public static final String QUERY_READ_BY_CATEGORY_ID = "ads.readByCategoryId";
	public static final String QUERY_READ_BY_OFFER_ID = "ads.readByOfferId";

	private int id;
	private OfferEntity offer;
	private String imageUrl;
	private String title;
	private Integer numberOfVolunteersNeeded;
	private Timestamp releaseDate;
	private Timestamp validUntil;
	private Boolean active;
	private Boolean deleted;
	private List<BasicConditionEntity> basicConditions;
	private List<TaskEntity> tasks;

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
	@JoinColumn(name = "offer_id", referencedColumnName = "id", nullable = false)
	public OfferEntity getOffer() {
		return offer;
	}

	public void setOffer(OfferEntity offer) {
		this.offer = offer;
	}

	@Basic
	@Column(name = "image_url")
	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}


	@Basic
	@Column(name = "title", nullable = false, length = 50)
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Basic
	@Column(name = "number_of_volunteers_needed", nullable = true)
	public Integer getNumberOfVolunteersNeeded() {
		return numberOfVolunteersNeeded;
	}

	public void setNumberOfVolunteersNeeded(Integer numberOfVolunteersNeeded) {
		this.numberOfVolunteersNeeded = numberOfVolunteersNeeded;
	}

	@Basic
	@Column(name = "release_date", nullable = true)
	public Timestamp getReleaseDate() {
		return releaseDate;
	}

	public void setReleaseDate(Timestamp releaseDate) {
		this.releaseDate = releaseDate;
	}

	@Basic
	@Column(name = "valid_until", nullable = true)
	public Timestamp getValidUntil() {
		return validUntil;
	}

	public void setValidUntil(Timestamp validUntil) {
		this.validUntil = validUntil;
	}

	@Basic
	@Column(name = "active", nullable = true)
	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	@Basic
	@Column(name = "deleted", nullable = true)
	public Boolean getDeleted() {
		return deleted;
	}

	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}

	@OneToMany(mappedBy = "ad", cascade = CascadeType.ALL, orphanRemoval = true)
	public List<BasicConditionEntity> getBasicConditions() {
		return basicConditions;
	}

	public void setBasicConditions(List<BasicConditionEntity> basicConditions) {
		this.basicConditions = basicConditions;
	}

	@OneToMany(mappedBy = "ad", cascade = CascadeType.ALL, orphanRemoval = true)
	public List<TaskEntity> getTasks() {
		return tasks;
	}

	public void setTasks(List<TaskEntity> tasks) {
		this.tasks = tasks;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof AdEntity)) {
			return false;
		}
		AdEntity adEntity = (AdEntity) o;
		return id == adEntity.id && Objects.equals(offer, adEntity.offer) && Objects.equals(imageUrl,
				adEntity.imageUrl) && Objects.equals(title, adEntity.title) && Objects.equals(numberOfVolunteersNeeded
				, adEntity.numberOfVolunteersNeeded) && Objects.equals(releaseDate, adEntity.releaseDate) && Objects.equals(validUntil, adEntity.validUntil) && Objects.equals(active, adEntity.active) && Objects.equals(deleted, adEntity.deleted) && Objects.equals(basicConditions, adEntity.basicConditions) && Objects.equals(tasks, adEntity.tasks);
	}

	@Override
	public int hashCode() {

		return Objects.hash(id, offer, imageUrl, title, numberOfVolunteersNeeded, releaseDate, validUntil, active,
				deleted, basicConditions, tasks);
	}
}
