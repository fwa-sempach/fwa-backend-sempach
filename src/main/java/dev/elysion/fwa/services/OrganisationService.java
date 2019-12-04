package dev.elysion.fwa.services;

import dev.elysion.fwa.converter.Converter;
import dev.elysion.fwa.dao.OrganisationDao;
import dev.elysion.fwa.dto.Image;
import dev.elysion.fwa.dto.Organisation;
import dev.elysion.fwa.entity.OrganisationEntity;
import dev.elysion.fwa.space.SpaceService;
import dev.elysion.fwa.util.RestUtil;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class OrganisationService {

	private OrganisationDao organisationDao;
	private MailService mailService;
	private SpaceService spaceService;

	@Inject
	public OrganisationService(OrganisationDao organisationDao, MailService mailService, SpaceService spaceService) {
		this.organisationDao = organisationDao;
		this.mailService = mailService;
		this.spaceService = spaceService;
	}

	public Organisation readOrganisationById(int organisationId) {
		return Converter.convert(organisationDao.readById(organisationId));
	}

	public Organisation saveOrganisation(Organisation organisation, boolean notifyOrg) throws IOException {
		Image image = organisation.getImage();

		// presave to generate id, if it's a new organisation
		if (organisation.getId() == 0) {
			int organisationId = organisationDao.merge(Converter.convert(organisation))
												.getId();
			organisation.setId(organisationId);
		}

		// save image to cdn
		if (image.getData() != null) {
			String imageUrl = spaceService.persistOrganisationImage(organisation.getId(), image.getFilename(),
					image.getData());

			organisation.getImage()
						.setImageUrl(imageUrl);
		}

		// save org to db
		Organisation savedOrganisation = Converter.convert(organisationDao.merge(Converter.convert(organisation)));

		// send notification
		if (notifyOrg) {
			mailService.sendOrganisationUpdateNotification(savedOrganisation);
		}

		return savedOrganisation;
	}

	public Organisation readOrganisationByUserId(int userId) {
		return Converter.convert(organisationDao.readByUserId(userId));
	}

	public Organisation deleteOrganisation(int organisationId){
		OrganisationEntity organisation = organisationDao.readById(organisationId);
		// TODO: delete File for Offers, Ads and Org from Space
		organisationDao.delete(organisation);
		return Converter.convert(organisation);
	}

	public List<Organisation> readPagedAndFiltered(int page, int pageSize, String sortField, String sortOrder,
												   boolean onlyVerified) {
		return organisationDao.readPagedAndFiltered(RestUtil.sanitizePageNumber(page), pageSize, sortField,
				RestUtil.determineSortOrder(sortOrder), onlyVerified)
							  .stream()
							  .map(Converter::convert)
							  .collect(Collectors.toList());
	}

	public Long readFilteredCount(boolean onlyVerified) {
		return organisationDao.readFilteredCount(onlyVerified);
	}
}
