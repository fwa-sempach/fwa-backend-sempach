package dev.elysion.fwa.services;

import dev.elysion.fwa.converter.Converter;
import dev.elysion.fwa.converter.RestConverter;
import dev.elysion.fwa.dao.OfferDao;
import dev.elysion.fwa.dto.Image;
import dev.elysion.fwa.dto.Offer;
import dev.elysion.fwa.entity.OfferEntity;
import dev.elysion.fwa.space.SpaceService;
import dev.elysion.fwa.util.RestUtil;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RequestScoped
public class OfferService {
	private OfferDao offerDao;
	private SpaceService spaceService;

	protected OfferService() {
		//Used for proxy
	}

	@Inject
	public OfferService(OfferDao offerDao, SpaceService spaceService) {
		this.offerDao = offerDao;
		this.spaceService = spaceService;
	}


	public List<Offer> readAll() {
		return offerDao.readAll()
					   .stream()
					   .map(Converter::convert)
					   .collect(Collectors.toList());
	}

	public List<Offer> readPagedAndFiltered(int page, int pageSize, List<Integer> categoryIds,
											List<Integer> organisationIds, boolean onlyActive, String sortField,
											String sortOrder) {
		return offerDao.readPagedAndFiltered(RestUtil.sanitizePageNumber(page), pageSize, categoryIds, organisationIds
				, onlyActive, sortField, RestUtil.determineSortOrder(sortOrder))
					   .stream()
					   .map(RestConverter::convert)
					   .collect(Collectors.toList());
	}

	public Long readFilteredCount(List<Integer> categoryIds, List<Integer> organisationIds, boolean onlyActive) {
		return offerDao.readFilteredCount(categoryIds, organisationIds, onlyActive);
	}

	public Offer getOfferById(int id) {
		return Converter.convert(offerDao.readById(id));
	}

	public Offer saveOffer(Offer offer) throws IOException {
		Image image = offer.getImage();

		// presave to generate id, if it's a new organisation
		if (offer.getId() == 0) {
			int offerId = offerDao.merge(Converter.convert(offer))
								  .getId();
			offer.setId(offerId);
		}

		if (image.getData() != null) {
			String imageUrl = spaceService.persistOfferImage(offer.getId(), image.getFilename(), image.getData());

			offer.getImage()
				 .setImageUrl(imageUrl);
		}

		return Converter.convert(offerDao.merge(Converter.convert(offer)));
	}

	public void deleteOffer(int offerId) {
		OfferEntity offer = offerDao.readById(offerId);
		offer.setDeleted(true);
		offerDao.merge(offer);
	}
}
