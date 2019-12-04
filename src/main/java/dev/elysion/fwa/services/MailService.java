package dev.elysion.fwa.services;

import dev.elysion.fwa.dto.Organisation;
import dev.elysion.fwa.dto.User;
import dev.elysion.fwa.entity.UserEntity;
import dev.elysion.fwa.util.MailUtil;
import io.helidon.config.Config;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Properties;

@ApplicationScoped
public class MailService {

	private final String MAIL_CONFIG_KEY = "mail.smtp";
	private final String FRONTEND_URL_KEY = "environment.frontend-url";
	private static final Logger LOGGER = LogManager.getLogger();
	private Config appConfig;
	private String frontendUrl;

	@Inject
	public MailService(Config appConfig) {
		this.appConfig = appConfig;

		this.frontendUrl = appConfig.get(FRONTEND_URL_KEY)
									.asString()
									.get();
	}

	public void sendPasswordReset(UserEntity user, String token) throws IOException {
		String template = MailUtil.loadTemplate("mail-templates/password-reset.txt");
		String url = frontendUrl + "/#/reset-password?t=" + token;
		template = template.replace("{url}", url);
		template = template.replace("{user}", user.getUsername());

		sendMail(user.getEmail(), "Passwort zurücksetzen", template);
	}

	public void sendEmailVerification(String to, String verificationToken) throws IOException {
		String template = MailUtil.loadTemplate("mail-templates/verify-emailadress.txt");
		String url = frontendUrl + "/#/login?t=" + verificationToken;
		template = template.replace("{url}", url);

		sendMail(to, "Emailadresse bestätigen", template);
	}

	public void sendMail(String to, String subject, String body) {
		String[] tos = {to};
		sendMail(tos, subject, body);
	}

	public void sendMail(String[] to, String subject, String body) {
		Properties props = getProperties();
		Session session = Session.getDefaultInstance(props);
		session.setDebug(true);

		MimeMessage message = new MimeMessage(session);

		try {
			message.setFrom(new InternetAddress(props.getProperty("mail.smtp.user")));
			InternetAddress[] toAddress = new InternetAddress[to.length];

			// To get the array of addresses
			for (int i = 0; i < to.length; i++) {
				toAddress[i] = new InternetAddress(to[i]);
			}

			for (int i = 0; i < toAddress.length; i++) {
				message.addRecipient(Message.RecipientType.TO, toAddress[i]);
			}

			message.setSubject(subject);
			message.setText(body);

			Transport transport = session.getTransport("smtp");
			transport.connect(session.getProperty("host"), props.getProperty("mail.smtp.user"), props.getProperty(
					"mail.smtp.password"));
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();
		}
		catch (AddressException ae) {
			LOGGER.warn("AdressException when trying to send email: " + ae.getMessage());
		}
		catch (MessagingException me) {
			LOGGER.warn("MessagingException when trying to send email: " + me.getMessage());
		}
	}

	private Properties getProperties() {
		Properties prop = System.getProperties();

		appConfig.get(MAIL_CONFIG_KEY)
				 .asMap()
				 .get()
				 .entrySet()
				 .stream()
				 .forEach(entry -> prop.setProperty(entry.getKey(), entry.getValue()));

		return prop;
	}

	public void sendOrganisationUpdateNotification(Organisation organisation) throws IOException {
		String template = MailUtil.loadTemplate("mail-templates/organisation-verified.txt");
		template = template.replace("{organisation.contactPerson.firstname}", organisation.getContactPerson()
																						  .getFirstname());
		template = template.replace("{organisation.name}", organisation.getName());

		sendMail(organisation.getContactPerson()
							 .getEmail(), "Organisation verifiziert", template);
	}

	public void sendPasswordChanged(User user) throws IOException {
		String template = MailUtil.loadTemplate("mail-templates/password-changed.txt");
		template = template.replace("{user}", user.getUsername());

		sendMail(user.getEmail(), "Passwort geändert", template);
	}

	public void sendEmailChanged(User user) throws IOException {
		String template = MailUtil.loadTemplate("mail-templates/email-changed.txt");
		template = template.replace("{user}", user.getUsername());

		sendMail(user.getEmail(), "Emailadresse geändert", template);
	}


}
