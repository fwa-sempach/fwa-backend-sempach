package dev.elysion.fwa.entity;

import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "offer")
@NamedQueries({@NamedQuery(name = OfferEntity.QUERY_READ_ALL, query = "select o from OfferEntity o"), @NamedQuery(
		name = OfferEntity.QUERY_READ_BY_ORGANISATION_ID,
		query = "select o from OfferEntity o where o.organisation.id = :organisationId"), @NamedQuery(
		name = OfferEntity.QUERY_READ_BY_CATEGORY_ID,
		query = "select o from OfferEntity o where o.category.id = :categoryId"),})

public class OfferEntity {

	public static final String QUERY_READ_ALL = "offers.readAll";
	public static final String QUERY_READ_BY_ORGANISATION_ID = "offers.readByOrganisationId";
	public static final String QUERY_READ_BY_CATEGORY_ID = "offers.readByCategoryId";

	private int id;
	private String title;
	private String description;
	private Boolean active;
	private Boolean deleted;
	private String imageUrl;
	private OrganisationEntity organisation;
	private CategoryEntity category;
	private PersonEntity contactPerson;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@ManyToOne
	@JoinColumn(name = "organisation_id", referencedColumnName = "id")
	public OrganisationEntity getOrganisation() {
		return organisation;
	}

	public void setOrganisation(OrganisationEntity organisation) {
		this.organisation = organisation;
	}

	@ManyToOne
	@JoinColumn(name = "category_id", referencedColumnName = "id")
	public CategoryEntity getCategory() {
		return category;
	}

	public void setCategory(CategoryEntity category) {
		this.category = category;
	}

	@OneToOne
	@JoinColumn(name = "contact_person_id", referencedColumnName = "id", nullable = false)
	@Cascade(org.hibernate.annotations.CascadeType.MERGE)
	public PersonEntity getContactPerson() {
		return contactPerson;
	}

	public void setContactPerson(PersonEntity contactPerson) {
		this.contactPerson = contactPerson;
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
	@Column(name = "description", nullable = false, length = 2000)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Basic
	@Column(name = "active", nullable = false)
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

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof OfferEntity)) {
			return false;
		}
		OfferEntity that = (OfferEntity) o;
		return id == that.id && Objects.equals(title, that.title) && Objects.equals(description, that.description) && Objects.equals(active, that.active) && Objects.equals(deleted, that.deleted) && Objects.equals(imageUrl, that.imageUrl) && Objects.equals(organisation, that.organisation) && Objects.equals(category, that.category) && Objects.equals(contactPerson, that.contactPerson);
	}

	@Override
	public int hashCode() {

		return Objects.hash(id, title, description, active, deleted, imageUrl, organisation, category, contactPerson);
	}
}
