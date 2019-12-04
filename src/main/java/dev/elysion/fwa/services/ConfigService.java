package dev.elysion.fwa.services;

import dev.elysion.fwa.converter.Converter;
import dev.elysion.fwa.dao.ConfigDao;
import dev.elysion.fwa.dto.Config;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

@RequestScoped
public class ConfigService {

	private static final Logger LOGGER = LogManager.getLogger();

	private ConfigDao configDao;

	protected ConfigService() {
		// CDI proxy
	}

	@Inject
	public ConfigService(ConfigDao configDao) {
		this.configDao = configDao;
	}

	public Config readConfig() {
		return Converter.convert(configDao.readById(1));
	}
}
