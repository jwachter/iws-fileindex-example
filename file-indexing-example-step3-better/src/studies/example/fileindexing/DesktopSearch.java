package studies.example.fileindexing;

import java.io.File;
import java.io.FileFilter;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import studies.example.fileindexing.crawl.CrawlJob;
import studies.example.fileindexing.index.IndexThread;

/**
 * Aufgabe 3
 * 
 * Implementierung des Crawling Teils einer Desktop Suche mit dem Executor
 * Framework aus Java.
 * 
 * Optimierung der Version aus Ausgabe 3:
 * 
 * - Verwenden eines zweiten Executors zum indexieren innerhalb des IndexThreads
 * 
 */
public class DesktopSearch {
	// Anzahl der Hardware Threads die auf unserem PC verfügbar sind
	public static final int HARDWARE_THREADS = Runtime.getRuntime()
			.availableProcessors();

	// TIMEOUT welche der ExecutorService hat um ausstehende Tasks zu beenden.
	private static final long TIMEOUT = 10000;

	public static void main(String[] args) {
		// Pfade aus den Kommandozeilenargumenten werden als Startverzeichnisse
		// verwendet.
		List<File> paths = new LinkedList<File>();
		for (String path : args) {
			paths.add(new File(path));
		}

		// Zähler für gefundene Dateien
		FileCounter fileCounter = new FileCounter();

		// ExecutorService
		ExecutorService crawling = Executors
				.newFixedThreadPool((HARDWARE_THREADS/2)+1);

		// Alle Suchpfade durchlaufen

		// Liste der Rücklgabewerte der Tasks
		List<Future<?>> found = new LinkedList<Future<?>>();

		// Queue für gefundene Dateien erstellen
		BlockingQueue<File> queue = new LinkedBlockingQueue<File>(200);

		for (File path : paths) {
			FileFilter filter = getFilter();
			// Future des Tasks zur Liste hinzufügen.
			found.add(crawling.submit(new CrawlJob(path, filter, fileCounter,
					queue)));
		}

		// Optimierung: Executor mit der Hälfte der Hardware Threads + 1 zum indizieren
		final ExecutorService indexExecutor = Executors.newFixedThreadPool((HARDWARE_THREADS/2)+1);
		
		// Indizierungsthread erzeugen
		IndexThread index = new IndexThread(queue, fileCounter, indexExecutor);

		// Index Thread starten
		index.start();

		// Ausgabe der gefundenen Dateien sobald vorhanden.
		for (Future<?> future : found) {
			try {
				// Warten bis ein Ergebnis vorliegt.
				future.get();

			} catch (InterruptedException e) {
				// Ignore for now
			} catch (ExecutionException e) {
				// Ignore for now
			}
		}

		// Dafür sorgen dass der IndexThread zuende läuft
		final File POISON = new File("");
		try {
			queue.put(POISON);
		} catch (InterruptedException e2) {
			while (true) {
				try {
					queue.put(POISON);
				} catch (InterruptedException e) {
				}
			}
		}

		// Warten bis der Index Thread beendet ist.
		try {
			index.join();
		} catch (InterruptedException e1) {
			/* Just continue shutdown */
		}

		// ExecutorService beenden
		crawling.shutdown();
		indexExecutor.shutdown();

		// Ein bestimmte Zeit einräumen die der Executor auf das fertiglaufen
		// von Tasks wartet.
		try {
			crawling.awaitTermination(TIMEOUT, TimeUnit.SECONDS);
			indexExecutor.awaitTermination(TIMEOUT, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			System.err
					.println("Crawler has been interrupted while waiting for tasks to finish.");
		} finally {
			System.out.println("Number of found files: "
					+ fileCounter.getFoundCount());
			System.out.println("Number of indexed files: "
					+ fileCounter.getIndexedCount());
		}
	}

	// Erzeugt einen Dateifilter für Textdateien
	private static FileFilter getFilter() {
		return new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				final int length = pathname.getName().length();
				return pathname.isDirectory()
						|| (length > 4 && pathname.getName().substring(
								length - 4, length).equals(".txt"));
			}
		};
	}
}
