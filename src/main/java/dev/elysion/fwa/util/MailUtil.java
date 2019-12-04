package dev.elysion.fwa.util;

import dev.elysion.fwa.dto.Ad;
import dev.elysion.fwa.dto.Participant;
import dev.elysion.fwa.dto.Skill;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.stream.Collectors;

public class MailUtil {
	private static final Logger LOGGER = LogManager.getLogger();

	public static String fillTemplate(String templateName, Ad ad, Participant participant) {
		String template = "";
		try {
			template = MailUtil.loadTemplate(templateName);
		}
		catch (IOException e) {
			LOGGER.warn("IOException occured while trying to load email template: " + templateName + ". Message: " + e.getMessage());
			return "";
		}

		if (ad != null && ad.getOffer() != null && ad.getOffer()
													 .getOrganisation() != null) {
			template = template.replace("{organisation.name}", ad.getOffer()
																 .getOrganisation()
																 .getName());
		}
		if (ad != null) {
			template = template.replace("{ad.title}", ad.getTitle());
		}
		if (participant != null) {
			template = template.replace("{participant.annotation}", participant.getAnnotation());
			if (participant.getPerson() != null) {
				template = template.replace("{participant.firstname}", participant.getPerson()
																				  .getFirstname());
				template = template.replace("{participant.lastname}", participant.getPerson()
																				 .getLastname());
				template = template.replace("{participant.street}", participant.getPerson()
																			   .getStreet());
				template = template.replace("{participant.house_nr}", participant.getPerson()
																				 .getHouseNr()
																				 .toString());
				template = template.replace("{participant.zip}", participant.getPerson()
																			.getZip());
				template = template.replace("{participant.city}", participant.getPerson()
																			 .getCity());
				template = template.replace("{participant.email}", participant.getPerson()
																			  .getEmail());
				template = template.replace("{participant.phone}", participant.getPerson()
																			  .getPhone());
			}
			if (participant.getSkills() != null) {
				template = template.replace("{participant.skills}", participant.getSkills()
																			   .stream()
																			   .map(Skill::getDescription)
																			   .map(d -> "- " + d)
																			   .collect(Collectors.joining("\n")));
			}
		}

		return template;
	}

	public static String loadTemplate(String templateName) throws IOException {
		String template = "";


		try (InputStream input = MailUtil.class.getClassLoader()
											   .getResourceAsStream(templateName)) {

			if (input == null) {
				return template;
			}

			java.util.Scanner s = new java.util.Scanner(input, "UTF-8").useDelimiter("\\A");
			template = s.hasNext() ? s.next() : "";

		}
		return template;

	}

}

