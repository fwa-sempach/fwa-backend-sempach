package dev.elysion.fwa.entity;

import dev.elysion.fwa.enumeration.ParticipantStatus;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "participant")
@NamedQueries(
		{@NamedQuery(name = ParticipantEntity.QUERY_READ_ALL, query = "select p from ParticipantEntity p"),
				@NamedQuery(
				name = ParticipantEntity.QUERY_READ_BY_AD_ID,
				query = "select p from ParticipantEntity p where p.adId = :adId"), @NamedQuery(
				name = ParticipantEntity.QUERY_READ_BY_ORGANISATION_ID,
				query = "select p from ParticipantEntity p where p.organisationId = :organisationId")})
public class ParticipantEntity {
	public static final String QUERY_READ_ALL = "participants.readAll";
	public static final String QUERY_READ_BY_AD_ID = "participants.readByAdId";
	public static final String QUERY_READ_BY_ORGANISATION_ID = "participants.readByOrganisationId";

	private int id;
	private PersonEntity person;
	private String imageUrl;
	private int organisationId;
	private Integer adId;
	private ParticipantStatus status;
	private String annotation;
	private Boolean deleted;
	private List<SkillEntity> skills;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@OneToOne
	@JoinColumn(name = "person_id", referencedColumnName = "id", nullable = false)
	@Cascade(org.hibernate.annotations.CascadeType.MERGE)
	public PersonEntity getPerson() {
		return person;
	}

	public void setPerson(PersonEntity person) {
		this.person = person;
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
	@Column(name = "organisation_id", nullable = false)
	public int getOrganisationId() {
		return organisationId;
	}

	public void setOrganisationId(int organisationId) {
		this.organisationId = organisationId;
	}

	@Basic
	@Column(name = "ad_id", nullable = true)
	public Integer getAdId() {
		return adId;
	}

	public void setAdId(Integer adId) {
		this.adId = adId;
	}

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	public ParticipantStatus getStatus() { return status; }

	public void setStatus(ParticipantStatus status) { this.status = status; }

	@Basic
	@Column(name = "annotation", nullable = true, length = 500)
	public String getAnnotation() {
		return annotation;
	}

	public void setAnnotation(String annotation) {
		this.annotation = annotation;
	}

	@Basic
	@Column(name = "deleted", nullable = true)
	public Boolean getDeleted() {
		return deleted;
	}

	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}

	@OneToMany(mappedBy = "participant", cascade = CascadeType.ALL, orphanRemoval = true)
	public List<SkillEntity> getSkills() {
		return skills;
	}

	public void setSkills(List<SkillEntity> skills) {
		this.skills = skills;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof ParticipantEntity)) {
			return false;
		}
		ParticipantEntity that = (ParticipantEntity) o;
		return id == that.id && organisationId == that.organisationId && Objects.equals(person, that.person) && Objects.equals(imageUrl, that.imageUrl) && Objects.equals(adId, that.adId) && status == that.status && Objects.equals(annotation, that.annotation) && Objects.equals(deleted, that.deleted) && Objects.equals(skills, that.skills);
	}

	@Override
	public int hashCode() {

		return Objects.hash(id, person, imageUrl, organisationId, adId, status, annotation, deleted, skills);
	}
}
