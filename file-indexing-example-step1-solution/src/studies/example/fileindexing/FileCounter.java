package studies.example.fileindexing;

import java.util.concurrent.atomic.AtomicInteger;

import studies.example.fileindexing.crawl.CrawlMonitoring;

/**
 * 
 * Klasse die das Z�hlen der gefundenen Dateien threadsicher erlaubt.
 *
 */
public class FileCounter implements CrawlMonitoring {
	// AtomicInteger zum z�hlen der gefundenen Dateien
	private final AtomicInteger found = new AtomicInteger(0);

	// Methode zum zur�ckliefern des Zahlenwertes.
	@Override
	public int getFoundCount() {
		// Threadsicherer Call zu AtomicInteger
		return found.get();
	}

	// Methode zum inkrementieren des Z�hlers
	@Override
	public void incrementFound() {
		// Atomares, threadsicheres inkrementieren
		found.incrementAndGet();
	}

}
