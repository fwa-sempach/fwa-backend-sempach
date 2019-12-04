package dev.elysion.fwa.entity;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "skill")
public class SkillEntity {
	private int id;
	private ParticipantEntity participant;
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
	@JoinColumn(name = "participant_id")
	public ParticipantEntity getParticipant() {
		return participant;
	}

	public void setParticipant(ParticipantEntity participant) {
		this.participant = participant;
	}

	@Basic
	@Column(name = "description", nullable = false, length = 150)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Basic
	@Column(name = "deleted")
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
		SkillEntity that = (SkillEntity) o;
		return id == that.id && Objects.equals(participant, that.participant) && Objects.equals(description,
				that.description) && Objects.equals(deleted, that.deleted);
	}

	@Override
	public int hashCode() {

		return Objects.hash(id, participant, description, deleted);
	}
}
