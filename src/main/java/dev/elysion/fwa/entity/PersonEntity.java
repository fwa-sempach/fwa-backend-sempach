package dev.elysion.fwa.entity;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "person")
public class PersonEntity {
	private int id;
	private String firstname;
	private String lastname;
	private String street;
	private String houseNr;
	private String zip;
	private String city;
	private String email;
	private String phone;
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
	@Column(name = "firstname", nullable = false, length = 50)
	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	@Basic
	@Column(name = "lastname", nullable = false, length = 50)
	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	@Basic
	@Column(name = "street", nullable = true, length = 50)
	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	@Basic
	@Column(name = "house_nr", nullable = true)
	public String getHouseNr() {
		return houseNr;
	}

	public void setHouseNr(String houseNr) {
		this.houseNr = houseNr;
	}

	@Basic
	@Column(name = "zip", nullable = true, length = 10)
	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	@Basic
	@Column(name = "city", nullable = true, length = 50)
	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	@Basic
	@Column(name = "email", nullable = false, length = 100)
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Basic
	@Column(name = "phone", nullable = true, length = 20)
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
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
		PersonEntity that = (PersonEntity) o;
		return id == that.id && Objects.equals(firstname, that.firstname) && Objects.equals(lastname, that.lastname) && Objects.equals(street, that.street) && Objects.equals(houseNr, that.houseNr) && Objects.equals(zip, that.zip) && Objects.equals(city, that.city) && Objects.equals(email, that.email) && Objects.equals(phone, that.phone) && Objects.equals(deleted, that.deleted);
	}

	@Override
	public int hashCode() {

		return Objects.hash(id, firstname, lastname, street, houseNr, zip, city, email, phone, deleted);
	}
}
