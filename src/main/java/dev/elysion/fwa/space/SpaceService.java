package dev.elysion.fwa.space;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import dev.elysion.fwa.converter.ImageDataConverter;
import io.helidon.config.Config;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;


@ApplicationScoped
public class SpaceService {

	//TODO: examples see here: https://www.baeldung.com/aws-s3-java


	private static final String BUCKET_KEY = "space.bucket";
	private static final String CDN_URL_KEY = "space.cdnUrl";

	private static final String ORG_IMG_PATH = "images/organisations/";
	private static final String AD_IMG_PATH = "images/jobads/";
	private static final String OFFER_IMG_PATH = "images/offers/";
	private static final String PARTICIPANT_IMG_PATH = "images/participants/";

	private static final Logger LOGGER = LogManager.getLogger();

	private AmazonS3 s3client;

	private String cdnUrl;
	private String bucketName;

	protected SpaceService() {
		// CDI Proxy
	}

	@Inject
	public SpaceService(Config appConfig, AmazonS3 s3client) {
		this.s3client = s3client;

		this.cdnUrl = appConfig.get(CDN_URL_KEY)
							   .asString()
							   .get();
		this.bucketName = appConfig.get(BUCKET_KEY)
								   .asString()
								   .get();
	}

	public String readOrganisationImageUrl(int organisationId) {
		String keyStart = ORG_IMG_PATH + String.valueOf(organisationId);

		ListObjectsRequest listObjectsRequest = new ListObjectsRequest().withBucketName(bucketName)
																		.withPrefix(ORG_IMG_PATH);

		Optional<String> keyOptional = s3client.listObjects(listObjectsRequest)
											   .getObjectSummaries()
											   .stream()
											   .map(S3ObjectSummary::getKey)
											   .filter(k -> k.startsWith(keyStart))
											   .findFirst();

		return keyOptional.isPresent() ? keyOptional.get() : "";
	}

	public String persistOrganisationImage(int organisationId, String fileName, String dataUrl) throws IOException {
		String key = generateFileName(ORG_IMG_PATH, String.valueOf(organisationId),
				ImageDataConverter.getExtenstion(fileName));
		return persistObject(key, ImageDataConverter.getContentType(dataUrl), ImageDataConverter.convert(dataUrl));
	}

	public String persistAdImage(int adId, String fileName, String dataUrl) throws IOException {
		String key = generateFileName(AD_IMG_PATH, String.valueOf(adId), ImageDataConverter.getExtenstion(fileName));
		return persistObject(key, ImageDataConverter.getContentType(dataUrl), ImageDataConverter.convert(dataUrl));
	}

	public String persistOfferImage(int offerId, String fileName, String dataUrl) throws IOException {
		String key = generateFileName(OFFER_IMG_PATH, String.valueOf(offerId),
				ImageDataConverter.getExtenstion(fileName));
		return persistObject(key, ImageDataConverter.getContentType(dataUrl), ImageDataConverter.convert(dataUrl));
	}

	public String persistParticipantImage(int participantId, String fileName, String dataUrl) throws IOException {
		String key = generateFileName(PARTICIPANT_IMG_PATH, String.valueOf(participantId),
				ImageDataConverter.getExtenstion(fileName));
		return persistObject(key, ImageDataConverter.getContentType(dataUrl), ImageDataConverter.convert(dataUrl));
	}


	private String generateFileName(String location, String prefix, String extension) {
		StringBuilder sb = new StringBuilder();

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss_SSS");

		return sb.append(location)
				 .append(prefix)
				 .append("_")
				 .append(LocalDateTime.now()
									  .format(formatter))
				 .append(extension)
				 .toString();
	}

	private String persistObject(String key, String contentType, byte[] data) throws IOException {
		try (InputStream fis = new ByteArrayInputStream(data)) {
			ObjectMetadata metadata = new ObjectMetadata();
			metadata.setContentLength(data.length);
			metadata.setContentType(contentType);
			metadata.setCacheControl("public, max-age=31536000");
			s3client.putObject(bucketName, key, fis, metadata);
			s3client.setObjectAcl(bucketName, key, CannedAccessControlList.PublicRead);
			LOGGER.info("saved to space service: " + key);
			return cdnUrl + key;
		}
	}
}
