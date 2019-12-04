package dev.elysion.fwa.services;

import dev.elysion.fwa.converter.Converter;
import dev.elysion.fwa.dao.AdDao;
import dev.elysion.fwa.dao.ParticipantDao;
import dev.elysion.fwa.dto.Ad;
import dev.elysion.fwa.dto.Image;
import dev.elysion.fwa.dto.Participant;
import dev.elysion.fwa.entity.ParticipantEntity;
import dev.elysion.fwa.enumeration.ParticipantStatus;
import dev.elysion.fwa.space.SpaceService;
import dev.elysion.fwa.util.MailUtil;
import dev.elysion.fwa.util.RestUtil;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RequestScoped
public class ParticipantService {
	private ParticipantDao participantDao;
	private AdDao adDao;
	private SpaceService spaceService;
	private MailService mailService;

	protected ParticipantService() {
		//Used for proxy
	}

	@Inject
	public ParticipantService(ParticipantDao participantDao, AdDao adDao, SpaceService spaceService,
							  MailService mailService) {
		this.participantDao = participantDao;
		this.adDao = adDao;
		this.spaceService = spaceService;
		this.mailService = mailService;
	}


	public List<Participant> readAll() {
		return participantDao.readAll()
							 .stream()
							 .map(Converter::convert)
							 .collect(Collectors.toList());
	}

	public List<Participant> readPagedAndFiltered(int page, int pageSize, Integer organisationId, List<Integer> adIds,
												  List<ParticipantStatus> status, String sortField, String sortOrder) {
		return participantDao.readPagedAndFiltered(RestUtil.sanitizePageNumber(page), pageSize, organisationId, adIds,
				status, sortField, RestUtil.determineSortOrder(sortOrder))
							 .stream()
							 .map(Converter::convert)
							 .collect(Collectors.toList());
	}

	public Long readFilteredCount(Integer organisationId, List<Integer> adIds, List<ParticipantStatus> status) {
		return participantDao.readFilteredCount(organisationId, adIds, status);
	}

	public Participant readParticipantById(int id) {
		return Converter.convert(participantDao.readById(id));
	}

	public Participant saveParticipant(Participant participant, boolean notifyOrg) throws IOException {
		Image image = participant.getImage();

		// presave to generate id, if it's a new organisation
		if (participant.getId() == 0) {
			// Set status if participant is new
			participant.setStatus(ParticipantStatus.NEW);
			int offerId = participantDao.merge(Converter.convert(participant))
										.getId();
			participant.setId(offerId);
		}

		if (image != null && image.getData() != null) {
			String imageUrl = spaceService.persistParticipantImage(participant.getId(), image.getFilename(),
					image.getData());

			participant.getImage()
					   .setImageUrl(imageUrl);
		}

		//save participant
		Participant persistedParticipant = Converter.convert(participantDao.merge(Converter.convert(participant)));

		if (notifyOrg) {
			//send emails to participant & organisation
			Ad relatedAd = Converter.convert(adDao.readById(persistedParticipant.getAdId()));
			String[] organisationEmail = {relatedAd.getOffer()
												   .getContactPerson().getEmail()};
			String[] participantEmail = {persistedParticipant.getPerson().getEmail()};


			String subject = MailUtil.fillTemplate("mail-templates/organisation-new-candidate-subject.txt", relatedAd,
					persistedParticipant);

			String content = MailUtil.fillTemplate("mail-templates/organisation-new-candidate.txt", relatedAd,
					persistedParticipant);
			mailService.sendMail(organisationEmail, subject, content);


			subject = MailUtil.fillTemplate("mail-templates/participant-thanks-for-applying-subject.txt", relatedAd,
					persistedParticipant);
			content = MailUtil.fillTemplate("mail-templates/participant-thanks-for-applying.txt", relatedAd,
					persistedParticipant);
			mailService.sendMail(participantEmail, subject, content);
		}

		return persistedParticipant;
	}

	public void deleteParticipant(int participantId) {
		ParticipantEntity participant = participantDao.readById(participantId);
		participant.setDeleted(true);
		participantDao.merge(participant);
	}
}