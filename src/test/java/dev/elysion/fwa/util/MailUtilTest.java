package dev.elysion.fwa.util;

import dev.elysion.fwa.dto.Ad;
import dev.elysion.fwa.dto.Participant;
import dev.elysion.fwa.dto.Person;
import dev.elysion.fwa.dto.Skill;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;

public class MailUtilTest {

	@Test
	public void loadTemplate_success() {
		try {
			MailUtil testee = new MailUtil();
			String template = testee.loadTemplate("mail-templates/organisation-new-candidate.txt");

			System.out.println(template);
		}
		catch (Exception e) {
			fail();
		}
	}

	@Test
	public void fillTemplate_success() {
		String template = "";
		Ad ad = new Ad();
		ad.setId(1);
		ad.setTitle("Helfer gesucht!");

		Person person = new Person();
		person.setFirstname("Hans");
		person.setLastname("Muster");
		person.setEmail("hans.muster@gmail.com");
		person.setPhone("+41 79 155 23 54");
		person.setZip("6340");
		person.setCity("Baar");
		person.setStreet("Dorfstrasse");
		person.setHouseNr("13");

		List<Skill> skills = new ArrayList<>();
		Skill skill = new Skill();
		skill.setDescription("Router installieren");
		skills.add(skill);

		Participant participant = new Participant();
		participant.setAnnotation("Würde mich freuen wenn ich mitmachen darf :)");
		participant.setPerson(person);
		participant.setSkills(skills);

		try {
			MailUtil testee = new MailUtil();
			template = testee.fillTemplate("mail-templates/organisation-new-candidate.txt", ad, participant);

			System.out.println(template);
		}
		catch (Exception e) {
			fail();
		}

		assertFalse(template.contains("{ad.title}"));
		assertFalse(template.contains("{participant.firstname}"));
		assertFalse(template.contains("{participant.lastname}"));
		assertFalse(template.contains("{participant.street}"));
		assertFalse(template.contains("{participant.house_nr}"));
		assertFalse(template.contains("{participant.zip}"));
		assertFalse(template.contains("{participant.city}"));
		assertFalse(template.contains("{participant.email}"));
		assertFalse(template.contains("{participant.phone}"));
		assertFalse(template.contains("{participant.skills}"));
		assertFalse(template.contains("{participant.annotation}"));
	}
}
