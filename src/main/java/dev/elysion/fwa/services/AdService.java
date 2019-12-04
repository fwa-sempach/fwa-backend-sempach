package dev.elysion.fwa.services;

import dev.elysion.fwa.converter.Converter;
import dev.elysion.fwa.converter.RestConverter;
import dev.elysion.fwa.dao.AdDao;
import dev.elysion.fwa.dto.Ad;
import dev.elysion.fwa.dto.Image;
import dev.elysion.fwa.entity.AdEntity;
import dev.elysion.fwa.space.SpaceService;
import dev.elysion.fwa.util.RestUtil;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RequestScoped
public class AdService {
	private AdDao adDao;
	private SpaceService spaceService;

	protected AdService() {
		//Used for proxy
	}

	@Inject
	public AdService(AdDao adDao, SpaceService spaceService) {
		this.adDao = adDao;
		this.spaceService = spaceService;
	}

	public List<Ad> readAll() {
		return adDao.readAll()
					.stream()
					.map(RestConverter::convert)
					.collect(Collectors.toList());
	}

	public List<Ad> readPagedAndFiltered(int page, int pageSize, List<Integer> categoryIds,
										 List<Integer> organisationIds, String sortField, String sortOrder,
										 boolean onlyActive) {
		return adDao.readPagedAndFiltered(RestUtil.sanitizePageNumber(page), pageSize, categoryIds, organisationIds,
				sortField, RestUtil.determineSortOrder(sortOrder), onlyActive)
					.stream()
					.map(Converter::convert)
					.collect(Collectors.toList());
	}

	public Long readFilteredCount(List<Integer> categoryIds, List<Integer> organisationIds, boolean onlyActive) {
		return adDao.readFilteredCount(categoryIds, organisationIds, onlyActive);
	}

	public Ad getAdById(int id) {
		return Converter.convert(adDao.readById(id));
	}

	public Ad saveAd(Ad ad) throws IOException {
		Image image = ad.getImage();

		// presave to generate id, if it's a new organisation
		if (ad.getId() == 0) {
			int offerId = adDao.merge(Converter.convert(ad))
							   .getId();
			ad.setId(offerId);
		}

		if (image.getData() != null) {
			String imageUrl = spaceService.persistAdImage(ad.getId(), image.getFilename(), image.getData());

			ad.getImage()
			  .setImageUrl(imageUrl);
		}

		return Converter.convert(adDao.merge(Converter.convert(ad)));
	}

	public void deleteAd(int adId) {
		AdEntity ad = adDao.readById(adId);
		ad.setDeleted(true);
		adDao.merge(ad);
	}
}
