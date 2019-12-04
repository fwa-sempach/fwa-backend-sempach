package dev.elysion.fwa.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;

public class ClasspathUtil {


	private static final Logger LOGGER = LogManager.getLogger();

	private ClasspathUtil() {
		//Privater Konstruktor verhinder Instanzierung dieser Helper Klasse
	}

	/**
	 * Liest eine Ressource aus dem Klassenpfad und gibt einen InputStream zurÃ¼ck.
	 *
	 * @param classloader Der Classloader von welchem die Resource gelesen werden soll
	 * @param fileNames   Datei die gelesen werden soll. Es kÃ¶nnen mehrere Dateinamen angegeben werden.
	 *                    Es wird nur so lange gesucht bis die erste davon gefunden ist.
	 *                    <p>
	 *                    <strong>Achtung:</strong> der Client dieser Methode ist dafÃ¼r verantwortlich den
	 *                    {@link InputStream InputStream}
	 *                    wieder korrekt zu schliessen.
	 *                    </p>
	 * @return {@link InputStream InputStream} der ersten gefundenen Datei. Falls keine davon gefunden wird,
	 * wird <code>null</code> zurÃ¼ckgegeben
	 */
	public static InputStream readResource(ClassLoader classloader, String... fileNames) {
		if (classloader != null) {
			for (String fileName : fileNames) {
				InputStream is = classloader.getResourceAsStream(fileName);
				if (is != null) {
					LOGGER.info(String.format("Lese Konfiguration: %s", fileName));
					return is;
				}
			}
		}

		LOGGER.error("Konfigurationsdateien '{}' konnten nicht auf Klassenpfad gefunden werden",
				Arrays.toString(fileNames));

		return null;
	}

	/**
	 * Liest eine Ressource aus dem Konfigurationsverzeichnis, oder falls kein Konfigurationsverzeichnis ermittelt
	 * werden konnte, aus dem Klassenpfad und gibt einen InputStream zurÃ¼ck.
	 *
	 * @param classloader Der Classloader von welchem die Resource gelesen werden soll
	 * @param fileNames   Datei die gelesen werden soll. Es kÃ¶nnen mehrere Dateinamen angegeben werden.
	 *                    Es wird nur so lange gesucht bis die erste davon gefunden ist.
	 *                    <p>
	 *                    <strong>Achtung:</strong> der Client dieser Methode ist dafÃ¼r verantwortlich den
	 *                    {@link InputStream InputStream}
	 *                    wieder korrekt zu schliessen.
	 *                    </p>
	 * @return {@link InputStream InputStream} der ersten gefundenen Datei. Falls keine davon gefunden wird,
	 * wird <code>null</code> zurÃ¼ckgegeben
	 */
	public static InputStream readConfigFile(ClassLoader classloader, String... fileNames) {
		return readResource(classloader, fileNames);
	}

	private static InputStream readFileFromPath(Path appConfigPath, String[] fileNames) {

		for (String fileName : fileNames) {
			Path targetFile = appConfigPath.resolve(fileName);
			if (targetFile.toFile()
						  .exists()) {
				LOGGER.info(String.format("Lese Konfiguration: %s", targetFile));
				try {
					return Files.newInputStream(targetFile, StandardOpenOption.READ);
				}
				catch (IOException e) {
					LOGGER.error("Konfigurationsfile '{}' konnte nicht gelesen werden", targetFile, e);
				}
			}
		}

		LOGGER.error("Konfigurationsdateien '{}' konnten nicht im Verzeichnis '{}' gefunden werden",
				Arrays.toString(fileNames), appConfigPath.toAbsolutePath());
		return null;
	}

}

