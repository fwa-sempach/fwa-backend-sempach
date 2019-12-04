package dev.elysion.fwa.space;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import io.helidon.config.Config;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

@ApplicationScoped
public class S3ClientProducer {

	private static final String ACCESS_KEY_KEY = "space.accessKey";
	private static final String SECRET_KEY_KEY = "space.secretKey";
	private static final String END_POINT_KEY = "space.endPoint";
	private static final String REGION_KEY = "space.region";

	private Config appConfig;

	@Inject
	public S3ClientProducer(Config appConfig) {
		this.appConfig = appConfig;
	}

	@Produces
	@RequestScoped
	public AmazonS3 produceS3Client() {
		AWSCredentials credentials = new BasicAWSCredentials(getConfigValue(ACCESS_KEY_KEY),
				getConfigValue(SECRET_KEY_KEY));

		return AmazonS3ClientBuilder.standard()
									.withCredentials(new AWSStaticCredentialsProvider(credentials))
									.withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(getConfigValue(END_POINT_KEY), getConfigValue(REGION_KEY)))
									.build();
	}

	private String getConfigValue(String key) {
		return this.appConfig.get(key)
							 .asString()
							 .get();
	}


}
