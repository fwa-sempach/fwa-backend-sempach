package dev.elysion.fwa.entity;


import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "organisation")
@NamedQueries({@NamedQuery(name = OrganisationEntity.QUERY_READ_ALL,
						   query = "select o from OrganisationEntity o"), @NamedQuery(
		name = OrganisationEntity.QUERY_READ_BY_USER_ID,
		query = "select o from OrganisationEntity o where o.userId = :userId")})

public class OrganisationEntity {
	public static final String QUERY_READ_ALL = "organisations.readAll";
	public static final String QUERY_READ_BY_USER_ID = "organisations.readByUserId";

	private int id;
	private int userId;
	private PersonEntity contactPerson;
	private String imageUrl;
	private String orgName;
	private String websiteUrl;
	private String description;
	private Boolean verified;
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

	@Basic
	@Column(name = "account_id")
	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	@OneToOne
	@JoinColumn(name = "contact_person_id", referencedColumnName = "id", nullable = false)
	@Cascade(org.hibernate.annotations.CascadeType.ALL)
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
	@Column(name = "org_name", nullable = false, length = 50)
	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	@Basic
	@Column(name = "website_url", nullable = true, length = 100)
	public String getWebsiteUrl() {
		return websiteUrl;
	}

	public void setWebsiteUrl(String websiteUrl) {
		this.websiteUrl = websiteUrl;
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
	@Column(name = "verified", nullable = true)
	public Boolean getVerified() {
		return verified;
	}

	public void setVerified(Boolean verified) {
		this.verified = verified;
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
		if (!(o instanceof OrganisationEntity)) {
			return false;
		}
		OrganisationEntity that = (OrganisationEntity) o;
		return id == that.id && userId == that.userId && Objects.equals(contactPerson, that.contactPerson) && Objects.equals(imageUrl, that.imageUrl) && Objects.equals(orgName, that.orgName) && Objects.equals(websiteUrl, that.websiteUrl) && Objects.equals(description, that.description) && Objects.equals(verified, that.verified) && Objects.equals(deleted, that.deleted);
	}

	@Override
	public int hashCode() {

		return Objects.hash(id, userId, contactPerson, imageUrl, orgName, websiteUrl, description, verified, deleted);
	}
}
