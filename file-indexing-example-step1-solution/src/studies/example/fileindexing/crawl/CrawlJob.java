package studies.example.fileindexing.crawl;

import java.io.File;
import java.io.FileFilter;

/**
 * 
 * Task zum ausf�hren des crawlers f�r ein root Verzeichnis innerhalb eines
 * ExecutorService
 * 
 */
public class CrawlJob implements Runnable {

	// Verzeichnisbaum welcher durchlaufen werden soll.
	private final File path;

	// Filter f�r die g�ltigen Dateien
	private final FileFilter filter;

	// Objekt das die Anzahl der gefundenen Dateien loggen soll.
	private CrawlMonitoring monitoring;

	// Erstellt einen neuen CrawlJob mit dem Pfad, Filter und Logging.
	public CrawlJob(File path, FileFilter filter, CrawlMonitoring monitoring) {
		this.path = path;
		this.filter = filter;
		this.monitoring = monitoring;
	}

	// Private Methode die einen Verzeichnisbaum traversiert.
	private void crawl(File file) throws InterruptedException {
		File[] entries = file.listFiles(filter);
		if (entries != null) {
			for (File entry : entries) {
				if (entry.isDirectory()) {
					crawl(entry);
				} else {
					// Eine Datei wurde gefunden, hochz�hlen des Counters.
					monitoring.incrementFound();
				}
			}
		}
	}

	// Task ausf�hren und Verzeichnis durchlaufen
	@Override
	public void run() {
		try {
			crawl(this.path);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
