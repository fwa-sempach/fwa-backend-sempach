package dev.elysion.fwa.entity;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "users")
@NamedQueries({@NamedQuery(name = UserEntity.QUERY_READ_BY_USERNAME,
						   query = "select u from UserEntity u where u.username = :username")})
public class UserEntity {

	public static final String QUERY_READ_BY_USERNAME = "user.readByUsername";

	private int id;
	private String username;
	private String email;
	private String password;
	private String salt;
	private Boolean deleted;
	private Boolean verified;

	private List<UserRoleEntity> roles;

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
	@Column(name = "username", nullable = false, length = 50)
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Basic
	@Column(name = "email", nullable = false, length = 250)
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Basic
	@Column(name = "pass", nullable = false, length = 1000)
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Basic
	@Column(name = "salt", nullable = false, length = 1000)
	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	@Basic
	@Column(name = "deleted", nullable = false)
	public Boolean getDeleted() {
		return deleted;
	}

	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}

	@Basic
	@Column(name = "verified", nullable = false)
	public Boolean getVerified() {
		return verified;
	}

	public void setVerified(Boolean verified) {
		this.verified = verified;
	}

	@OneToMany(fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id", referencedColumnName = "id")
	public List<UserRoleEntity> getRoles() {
		return roles;
	}

	public void setRoles(List<UserRoleEntity> roles) {
		this.roles = roles;
	}


	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof UserEntity)) {
			return false;
		}
		UserEntity that = (UserEntity) o;
		return id == that.id && Objects.equals(username, that.username) && Objects.equals(password, that.password) && Objects.equals(salt, that.salt) && Objects.equals(deleted, that.deleted) && Objects.equals(roles, that.roles);
	}

	@Override
	public int hashCode() {

		return Objects.hash(id, username, password, salt, deleted, roles);
	}
}
