package dev.elysion.fwa.dto;

public class Config {
	private int id;
	private Image logo;
	private Image banner;
	private Image icon;
	private String title;
	private String aboutVoluntaryWork;
	private String aboutPlatform;
	private String aboutVoluntaryWorkTitle;
	private String aboutPlatformTitle;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Image getLogo() {
		return logo;
	}

	public void setLogo(Image logo) {
		this.logo = logo;
	}

	public Image getBanner() {
		return banner;
	}

	public void setBanner(Image banner) {
		this.banner = banner;
	}

	public Image getIcon() {
		return icon;
	}

	public void setIcon(Image icon) {
		this.icon = icon;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAboutVoluntaryWork() {
		return aboutVoluntaryWork;
	}

	public void setAboutVoluntaryWork(String aboutVoluntaryWork) {
		this.aboutVoluntaryWork = aboutVoluntaryWork;
	}

	public String getAboutPlatform() {
		return aboutPlatform;
	}

	public void setAboutPlatform(String aboutPlatform) {
		this.aboutPlatform = aboutPlatform;
	}

	public String getAboutVoluntaryWorkTitle() {
		return aboutVoluntaryWorkTitle;
	}

	public void setAboutVoluntaryWorkTitle(String aboutVoluntaryWorkTitle) {
		this.aboutVoluntaryWorkTitle = aboutVoluntaryWorkTitle;
	}

	public String getAboutPlatformTitle() {
		return aboutPlatformTitle;
	}

	public void setAboutPlatformTitle(String aboutPlatformTitle) {
		this.aboutPlatformTitle = aboutPlatformTitle;
	}
}
