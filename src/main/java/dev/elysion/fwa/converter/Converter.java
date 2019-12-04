package dev.elysion.fwa.converter;

import dev.elysion.fwa.dto.*;
import dev.elysion.fwa.entity.*;

import javax.enterprise.context.RequestScoped;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.stream.Collectors;

@RequestScoped
public class Converter {

	private Converter() {
		// util class
	}

	public static Config convert(ConfigEntity source) {
		Config target = new Config();
		target.setId(source.getId());
		target.setTitle(source.getTitle());
		target.setAboutPlatform(source.getAboutPlatform());
		target.setAboutPlatformTitle(source.getAboutPlatformTitle());
		target.setAboutVoluntaryWork(source.getAboutVoluntaryWork());
		target.setAboutVoluntaryWorkTitle(source.getAboutVoluntaryWorkTitle());

		Image banner = new Image();
		banner.setImageUrl(source.getBannerUrl());
		target.setBanner(banner);

		Image logo = new Image();
		logo.setImageUrl(source.getLogoLargeUrl());
		target.setLogo(logo);

		Image icon = new Image();
		icon.setImageUrl(source.getLogoSmallUrl());
		target.setIcon(icon);

		return target;
	}

	public static Organisation convert(OrganisationEntity source) {
		if (source == null) {
			return null;
		}

		Organisation target = new Organisation();

		target.setId(source.getId());
		target.setDeleted(source.getDeleted());
		target.setDescription(source.getDescription());
		target.setName(source.getOrgName());
		target.setVerified(source.getVerified());
		target.setWebsiteUrl(source.getWebsiteUrl());
		target.setUserId(source.getUserId());

		Image image = new Image();
		image.setImageUrl(source.getImageUrl());
		target.setImage(image);

		if (source.getContactPerson() != null) {
			target.setContactPerson(convert(source.getContactPerson()));
		}

		return target;
	}

	public static OrganisationEntity convert(Organisation source) {
		OrganisationEntity target = new OrganisationEntity();

		target.setId(source.getId());
		target.setDeleted(source.getDeleted());
		target.setDescription(source.getDescription());
		target.setOrgName(source.getName());
		target.setVerified(source.getVerified());
		target.setWebsiteUrl(source.getWebsiteUrl());
		target.setUserId(source.getUserId());

		if (source.getImage() != null) {
			target.setImageUrl(source.getImage()
									 .getImageUrl());
		}

		if (source.getContactPerson() != null) {
			target.setContactPerson(convert(source.getContactPerson()));
		}

		return target;
	}

	public static Offer convert(OfferEntity source) {
		if (source == null) {
			return null;
		}
		Offer target = new Offer();
		target.setActive(source.getActive());
		target.setDeleted(source.getDeleted());
		target.setDescription(source.getDescription());
		target.setId(source.getId());
		target.setTitle(source.getTitle());

		Image image = new Image();
		image.setImageUrl(source.getImageUrl());
		target.setImage(image);

		if (source.getCategory() != null) {
			target.setCategory(convert(source.getCategory()));
		}

		if (source.getContactPerson() != null) {
			target.setContactPerson(convert(source.getContactPerson()));
		}

		if (source.getOrganisation() != null) {
			target.setOrganisation(convert(source.getOrganisation()));
		}
		return target;
	}

	public static OfferEntity convert(Offer source) {
		OfferEntity target = new OfferEntity();
		target.setActive(source.getActive());
		target.setDeleted(source.getDeleted());
		target.setDescription(source.getDescription());
		target.setId(source.getId());
		target.setTitle(source.getTitle());

		target.setImageUrl(source.getImage()
								 .getImageUrl());

		if (source.getCategory() != null) {
			target.setCategory(convert(source.getCategory()));
		}

		if (source.getContactPerson() != null) {
			target.setContactPerson(convert(source.getContactPerson()));
		}

		if (source.getOrganisation() != null) {
			target.setOrganisation(convert(source.getOrganisation()));
		}

		return target;
	}

	public static Participant convert(ParticipantEntity source) {
		Participant target = new Participant();
		target.setId(source.getId());
		target.setAdId(source.getAdId());
		target.setAnnotation(source.getAnnotation());
		target.setDeleted(source.getDeleted());
		target.setOrganisationId(source.getOrganisationId());
		target.setStatus(source.getStatus());

		Image image = new Image();
		image.setImageUrl(source.getImageUrl());
		target.setImage(image);

		if (source.getPerson() != null) {
			target.setPerson(convert(source.getPerson()));
		}

		if (source.getSkills() != null) {
			target.setSkills(source.getSkills()
								   .stream()
								   .map(Converter::convert)
								   .collect(Collectors.toList()));
		}

		return target;
	}

	public static ParticipantEntity convert(Participant source) {
		ParticipantEntity target = new ParticipantEntity();
		target.setId(source.getId());
		target.setAdId(source.getAdId());
		target.setAnnotation(source.getAnnotation());
		target.setDeleted(source.getDeleted());
		target.setOrganisationId(source.getOrganisationId());
		target.setStatus(source.getStatus());

		if (source.getImage() != null) {
			target.setImageUrl(source.getImage()
									 .getImageUrl());
		}

		if (source.getPerson() != null) {
			target.setPerson(convert(source.getPerson()));
		}

		if (source.getSkills() != null) {
			target.setSkills(source.getSkills()
								   .stream()
								   .map(s -> {
									   SkillEntity t = Converter.convert(s);
									   t.setParticipant(target);
									   return t;
								   })
								   .collect(Collectors.toList()));
		}

		return target;
	}

	public static Skill convert(SkillEntity source) {
		Skill target = new Skill();
		target.setId(source.getId());
		target.setDescription(source.getDescription());
		target.setDeleted(source.getDeleted());
		return target;
	}

	public static SkillEntity convert(Skill source) {
		SkillEntity target = new SkillEntity();
		target.setId(source.getId());
		target.setDescription(source.getDescription());
		target.setDeleted(source.getDeleted());
		return target;
	}

	public static Category convert(CategoryEntity source) {
		Category target = new Category();
		target.setId(source.getId());
		target.setName(source.getName());
		target.setDeleted(source.getDeleted());

		return target;
	}

	public static CategoryEntity convert(Category source) {
		CategoryEntity target = new CategoryEntity();
		target.setId(source.getId());
		target.setName(source.getName());
		target.setDeleted(source.getDeleted());

		return target;
	}

	public static Person convert(PersonEntity source) {
		Person target = new Person();
		target.setId(source.getId());
		target.setCity(source.getCity());
		target.setDeleted(source.getDeleted());
		target.setEmail(source.getEmail());
		target.setFirstname(source.getFirstname());
		target.setLastname(source.getLastname());
		target.setHouseNr(source.getHouseNr());
		target.setStreet(source.getStreet());
		target.setZip(source.getZip());
		target.setPhone(source.getPhone());

		return target;
	}

	public static PersonEntity convert(Person source) {
		PersonEntity target = new PersonEntity();
		target.setId(source.getId());
		target.setCity(source.getCity());
		target.setDeleted(source.getDeleted());
		target.setEmail(source.getEmail());
		target.setFirstname(source.getFirstname());
		target.setLastname(source.getLastname());
		target.setHouseNr(source.getHouseNr());
		target.setStreet(source.getStreet());
		target.setZip(source.getZip());
		target.setPhone(source.getPhone());

		return target;
	}

	public static User convert(UserEntity source) {
		if (source == null) {
			return null;
		}

		User target = new User();
		target.setId(source.getId());
		target.setUsername(source.getUsername());
		target.setEmail(source.getEmail());
		target.setPassword(source.getPassword());
		target.setSalt(source.getSalt());
		target.setDeleted(source.getDeleted());
		target.setVerified(source.getVerified());

		if (source.getRoles() != null) {
			target.setRoles(source.getRoles()
								  .stream()
								  .map(UserRoleEntity::getRole)
								  .collect(Collectors.toList()));
		}

		return target;
	}

	public static UserEntity convert(User source) {
		if (source == null) {
			return null;
		}

		UserEntity target = new UserEntity();
		target.setId(source.getId());
		target.setUsername(source.getUsername());
		target.setEmail(source.getEmail());
		target.setPassword(source.getPassword());
		target.setSalt(source.getSalt());
		target.setDeleted(source.isDeleted());
		target.setVerified(source.isVerified());

		// FIXME: get rid of unmanaged manager
		/*
		if (source.getRoles() != null) {
			EntityManager em = EntityManagerProducer.createUnmanagedEntityManager();

			target.setRoles(source.getRoles()
								  .stream()
								  .map(r -> {
									  int userId = source.getId();
									  UserRoleDao userRoleDao = new UserRoleDao(em);
									  UserRoleEntity userRole = userRoleDao.readByUserIdRole(userId, r);
									  if (userRole == null) {
										  userRole = new UserRoleEntity();
										  userRole.setUserId(userId);
										  userRole.setRole(r);
									  }

									  return userRole;
								  })
								  .collect(Collectors.toList()));
		}
		*/

		return target;
	}

	public static TaskEntity convert(Task source) {
		TaskEntity target = new TaskEntity();
		target.setId(source.getId());
		target.setDeleted(source.getDeleted());
		target.setDescription(source.getDescription());
		return target;
	}

	public static Task convert(TaskEntity source) {
		Task target = new Task();
		target.setId(source.getId());
		target.setAdId(source.getAd()
							 .getId());
		target.setDeleted(source.getDeleted());
		target.setDescription(source.getDescription());
		return target;
	}

	public static BasicConditionEntity convert(BasicCondition source) {
		BasicConditionEntity target = new BasicConditionEntity();
		target.setId(source.getId());
		target.setDeleted(source.getDeleted());
		target.setDescription(source.getDescription());
		return target;
	}

	public static BasicCondition convert(BasicConditionEntity source) {
		BasicCondition target = new BasicCondition();
		target.setId(source.getId());
		target.setAdId(source.getAd()
							 .getId());
		target.setDeleted(source.getDeleted());
		target.setDescription(source.getDescription());
		return target;
	}

	public static AdEntity convert(Ad source) {
		AdEntity target = new AdEntity();
		target.setId(source.getId());
		target.setTitle(source.getTitle());
		target.setActive(source.isActive());
		target.setDeleted(source.isDeleted());
		target.setNumberOfVolunteersNeeded(source.getNumberOfVolunteersNeeded());
		target.setReleaseDate(convertToTimestamp(source.getReleaseDate()));
		target.setValidUntil(convertToTimestamp(source.getValidUntil()));

		target.setImageUrl(source.getImage()
								 .getImageUrl());

		if (source.getOffer() != null) {
			target.setOffer(convert(source.getOffer()));
		}

		if (source.getTasks() != null) {
			target.setTasks(source.getTasks()
								  .stream()
								  .map(Converter::convert)
								  .collect(Collectors.toList()));

			for (TaskEntity taskEntity : target.getTasks()) {
				taskEntity.setAd(target);
			}
		}

		if (source.getBasicConditions() != null) {
			target.setBasicConditions(source.getBasicConditions()
											.stream()
											.map(Converter::convert)
											.collect(Collectors.toList()));
			for (BasicConditionEntity basicConditionEntity : target.getBasicConditions()) {
				basicConditionEntity.setAd(target);
			}
		}

		return target;
	}

	public static Ad convert(AdEntity source) {
		if (source == null) {
			return null;
		}

		Ad target = new Ad();
		target.setId(source.getId());
		target.setTitle(source.getTitle());
		target.setActive(source.getActive());
		target.setDeleted(source.getDeleted());
		target.setNumberOfVolunteersNeeded(source.getNumberOfVolunteersNeeded());
		target.setReleaseDate(convertToLocalDateTime(source.getReleaseDate()));
		target.setValidUntil(convertToLocalDateTime(source.getValidUntil()));

		Image image = new Image();
		image.setImageUrl(source.getImageUrl());
		target.setImage(image);

		if (source.getOffer() != null) {
			target.setOffer(convert(source.getOffer()));
		}

		if (source.getTasks() != null) {
			target.setTasks(source.getTasks()
								  .stream()
								  .map(Converter::convert)
								  .collect(Collectors.toList()));
		}

		if (source.getBasicConditions() != null) {
			target.setBasicConditions(source.getBasicConditions()
											.stream()
											.map(Converter::convert)
											.collect(Collectors.toList()));
		}

		return target;
	}

	public static Document convert(DocumentEntity source) {
		Document target = new Document();

		target.setId(source.getId());
		target.setData(ImageDataConverter.convert(source.getData()));
		target.setDocumentType(source.getDocumentType());
		target.setFilename(source.getFilename());
		target.setDataType(source.getDataType());

		return target;
	}

	public static DocumentEntity convert(Document source) {
		DocumentEntity target = new DocumentEntity();

		target.setId(source.getId());
		target.setData(ImageDataConverter.convert(source.getData()));
		target.setDocumentType(source.getDocumentType());
		target.setFilename(source.getFilename());

		return target;
	}

	protected static LocalDate convert(Date source) {
		return source.toLocalDate();
	}

	protected static Date convert(LocalDate source) {
		return Date.valueOf(source);
	}

	public static java.util.Date convertToDate(LocalDateTime source) {
		return java.util.Date.from(source.atZone(ZoneId.systemDefault())
										 .toInstant());
	}

	public static LocalDateTime convertToLocalDateTime(java.util.Date source) {
		return source.toInstant()
					 .atZone(ZoneId.systemDefault())
					 .toLocalDateTime();
	}

	public static LocalDateTime convertToLocalDateTime(java.sql.Timestamp source) {
		return source.toLocalDateTime();
	}

	private static java.sql.Timestamp convertToTimestamp(LocalDateTime source) {
		return Timestamp.valueOf(source);
	}
}
