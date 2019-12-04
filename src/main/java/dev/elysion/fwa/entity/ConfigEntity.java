package dev.elysion.fwa.entity;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "config")
public class ConfigEntity {
	private int id;
	private String logoSmallUrl;
	private String bannerUrl;
	private String logoLargeUrl;
	private String title;
	private String aboutVoluntaryWork;
	private String aboutVoluntaryWorkTitle;
	private String aboutPlatform;
	private String aboutPlatformTitle;

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
	@Column(name = "logo_small_url")
	public String getLogoSmallUrl() {
		return logoSmallUrl;
	}

	public void setLogoSmallUrl(String logoSmallUrl) {
		this.logoSmallUrl = logoSmallUrl;
	}

	@Basic
	@Column(name = "banner_url")
	public String getBannerUrl() {
		return bannerUrl;
	}

	public void setBannerUrl(String bannerUrl) {
		this.bannerUrl = bannerUrl;
	}

	@Basic
	@Column(name = "logo_large_url")
	public String getLogoLargeUrl() {
		return logoLargeUrl;
	}

	public void setLogoLargeUrl(String logoLargeUrl) {
		this.logoLargeUrl = logoLargeUrl;
	}

	@Basic
	@Column(name = "platform_title", nullable = false, length = 100)
	public String getTitle() {
		return title;
	}

	public void setTitle(String platformTitle) {
		this.title = platformTitle;
	}

	@Basic
	@Column(name = "about_voluntary_work", nullable = true, length = 2000)
	public String getAboutVoluntaryWork() {
		return aboutVoluntaryWork;
	}

	public void setAboutVoluntaryWork(String aboutVoluntaryWork) {
		this.aboutVoluntaryWork = aboutVoluntaryWork;
	}


	@Basic
	@Column(name = "about_platform", nullable = true, length = 2000)
	public String getAboutPlatform() {
		return aboutPlatform;
	}

	public void setAboutPlatform(String aboutThePlattform) {
		this.aboutPlatform = aboutThePlattform;
	}

	@Basic
	@Column(name = "about_platform_title", nullable = true, length = 2000)
	public String getAboutVoluntaryWorkTitle() {
		return aboutVoluntaryWorkTitle;
	}

	public void setAboutVoluntaryWorkTitle(String aboutVoluntaryWorkTitle) {
		this.aboutVoluntaryWorkTitle = aboutVoluntaryWorkTitle;
	}

	@Basic
	@Column(name = "about_voluntary_work_title", nullable = true, length = 2000)
	public String getAboutPlatformTitle() {
		return aboutPlatformTitle;
	}

	public void setAboutPlatformTitle(String aboutThePlattformTitle) {
		this.aboutPlatformTitle = aboutThePlattformTitle;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof ConfigEntity)) {
			return false;
		}
		ConfigEntity that = (ConfigEntity) o;
		return id == that.id && Objects.equals(logoSmallUrl, that.logoSmallUrl) && Objects.equals(bannerUrl,
				that.bannerUrl) && Objects.equals(logoLargeUrl, that.logoLargeUrl) && Objects.equals(title,
				that.title) && Objects.equals(aboutVoluntaryWork, that.aboutVoluntaryWork) && Objects.equals(aboutVoluntaryWorkTitle, that.aboutVoluntaryWorkTitle) && Objects.equals(aboutPlatform, that.aboutPlatform) && Objects.equals(aboutPlatformTitle, that.aboutPlatformTitle);
	}

	@Override
	public int hashCode() {

		return Objects.hash(id, logoSmallUrl, bannerUrl, logoLargeUrl, title, aboutVoluntaryWork,
				aboutVoluntaryWorkTitle, aboutPlatform, aboutPlatformTitle);
	}
}
