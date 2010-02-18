package studies.example.fileindexing;

import java.util.concurrent.atomic.AtomicInteger;

import studies.example.fileindexing.crawl.CrawlMonitoring;
import studies.example.fileindexing.index.IndexMonitoring;

/**
 * 
 * Klasse die das Z�hlen der gefundener und indizierter Dateien threadsicher erlaubt.
 *
 */
public class FileCounter implements CrawlMonitoring , IndexMonitoring{
	// AtomicInteger zum z�hlen der gefundenen Dateien
	private final AtomicInteger found = new AtomicInteger(0);
	
	// AtomicInteger zum z�hlen der indizierten Dateien
	private final AtomicInteger indexed = new AtomicInteger(0);

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

	// Zahl der Indizierten Dateien abholen
	@Override
	public int getIndexedCount() {
		return indexed.get();
	}

	// Inkrementieren der Anzahl an indizierten Dateien
	@Override
	public void incrementIndexed() {
		indexed.incrementAndGet();
	}

}
