package dev.elysion.fwa.entity;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "category")
@NamedQueries({@NamedQuery(name = CategoryEntity.QUERY_READ_ALL, query = "select c from CategoryEntity c"),})

public class CategoryEntity {
	public static final String QUERY_READ_ALL = "categories.readAll";

	private int id;
	private String name;
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
	@Column(name = "category_name", nullable = false, length = 50)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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
		CategoryEntity that = (CategoryEntity) o;
		return id == that.id && Objects.equals(name, that.name) && Objects.equals(deleted, that.deleted);
	}

	@Override
	public int hashCode() {

		return Objects.hash(id, name, deleted);
	}
}
