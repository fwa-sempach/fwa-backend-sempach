package dev.elysion.fwa.entity;

import dev.elysion.fwa.enumeration.Role;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "user_role")
@NamedQuery(name = UserRoleEntity.QUERY_READ_BY_USERID_ROLE,
			query = "SELECT u FROM UserRoleEntity u WHERE userId = :userId AND role = :role")
public class UserRoleEntity {

	public static final String QUERY_READ_BY_USERID_ROLE = "user.readByUserIdRole";

	private int id;
	private int userId;
	private Role role;

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
	@Column(name = "user_id", nullable = false)
	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	@Enumerated(EnumType.STRING)
	@Column(name = "role_code", nullable = false, length = 20)
	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		UserRoleEntity that = (UserRoleEntity) o;
		return id == that.id && userId == that.userId && Objects.equals(role, that.role);
	}

	@Override
	public int hashCode() {

		return Objects.hash(id, userId, role);
	}
}
