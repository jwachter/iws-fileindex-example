package studies.example.fileindexing.crawl;

import java.io.File;
import java.io.FileFilter;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;

/**
 *
 * Task zum ausführen des crawlers für ein root Verzeichnis innerhalb eines ExecutorService
 *
 */
public class CrawlJob implements Runnable{

	// Verzeichnisbaum welcher durchlaufen werden soll.
	private final File path;
	
	// Filter für die gültigen Dateien
	private final FileFilter filter;
	
	// Objekt das die Anzahl der gefundenen Dateien loggen soll.
	private CrawlMonitoring monitoring;

	// Queue zur Übergabe der Ergebnisse.
	private final BlockingQueue<File> queue;

	// Erstellt einen neuen CrawlJob mit dem Pfad, Filter und Logging.
	public CrawlJob(File path, FileFilter filter, CrawlMonitoring monitoring, BlockingQueue<File> queue) {
		this.path = path;
		this.filter = filter;
		this.monitoring = monitoring;
		this.queue = queue;
	}

	// Private Methode die einen Verzeichnisbaum traversiert.
	private void crawl(File file) throws InterruptedException {
		File[] entries = file.listFiles(filter);
		if (entries != null) {
			for (File entry : entries) {
				if (entry.isDirectory()) {
					crawl(entry);
				} else {
					// Eine Datei wurde gefunden, hochzählen des Counters.
					queue.put(entry);
					monitoring.incrementFound();
				}
			}
		}
	}

	// Dateien durchlaufen.
	@Override
	public void run(){
		try {
			crawl(path);
		} catch (InterruptedException e) {
			/* just finish running */
		}
	}

}
