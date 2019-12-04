package dev.elysion.fwa;

import io.helidon.config.Config;
import io.helidon.config.PollingStrategies;
import io.helidon.microprofile.server.Server;

import java.io.IOException;

import static io.helidon.config.ConfigSources.classpath;
import static io.helidon.config.ConfigSources.file;

public class Main {


	public static void main(String[] args) throws IOException {
		// redirect JUL logs to Log4j. This statement needs to be before any logging calls
		System.setProperty("java.util.logging.manager", "org.apache.logging.log4j.jul.LogManager");
		System.setProperty("log4j.configurationFile", "config/logging.yaml");

		Server.builder()
			  .config(buildConfig())
			  .build()
			  .start();
	}

	private static Config buildConfig() {
		return Config.builder()
					 .sources(file("config/application.yaml").pollingStrategy(PollingStrategies::watch)
															 .optional(), classpath("META-INF/microprofile-config" +
							 ".properties").optional(), classpath("application.yaml"))
					 .build();
	}

}
