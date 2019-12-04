package dev.elysion.fwa.persistence;

import io.helidon.config.Config;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.Map;
import java.util.stream.Collectors;

@ApplicationScoped
public class EntityManagerProducer {

	private final String CONFIG_KEY = "datasource";
	private final Logger LOGGER = LogManager.getLogger();
	private EntityManagerFactory factory;

	@Inject
	private EntityManagerProducer(Config appConfig) {
		Map<String, String> config = extractConfig(appConfig);
		this.factory = Persistence.createEntityManagerFactory("dev.elysion.fwa", config);
		LOGGER.debug("EntityManagerFactory with persistenceUnit 'dev.elysion.fwa' has been created");
	}

	private Map<String, String> extractConfig(Config appConfig) {
		return appConfig.get(CONFIG_KEY)
						.asMap()
						.get()
						.entrySet()
						.stream()
						.collect(Collectors.toMap(e -> e.getKey()
														.replace(CONFIG_KEY + ".", ""), e -> e.getValue()));
	}

	@Produces
	@RequestScoped
	public EntityManager createEntityManager() {
		return factory.createEntityManager();
	}

	public void close(@Disposes EntityManager entityManager) {
		entityManager.close();
	}
}