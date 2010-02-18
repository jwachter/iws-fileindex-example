package studies.example.fileindexing;

import java.io.File;
import java.io.FileFilter;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import studies.example.fileindexing.crawl.CrawlJob;

/**
 * Aufgabe 2
 * 
 * Implementierung des Crawling Teils einer Desktop Suche mit dem Executor
 * Framework aus Java.
 * 
 * Schritte:
 * 
 * 1. Ändern der Klasse CrawlJob so das diese als Task ausgeführt werden kann
 * und eine Liste der gefundenen Dateien zurückliefert.
 * 
 * 2. Erstellen der neuen Tasks mit dem Executor und Verarbeitung der
 * Rückgabewerte.
 * 
 * 3. Ausgeben der Rückgabewerte pro Task (Dateinamen mittels File.getName()
 * 
 */
public class DesktopSearch {
	// Anzahl der Hardware Threads die auf unserem PC verfügbar sind
	public static final int THREADS = Runtime.getRuntime()
			.availableProcessors() + 1;

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
				.newFixedThreadPool(THREADS);

		// Alle Suchpfade durchlaufen

		// Liste der Rücklgabewerte der Tasks
		List<Future<List<File>>> found = new LinkedList<Future<List<File>>>();

		for (File path : paths) {
			FileFilter filter = getFilter();
			// Future des Tasks zur Liste hinzufügen.
			found.add(crawling.submit(new CrawlJob(path, filter, fileCounter)));
		}

		// Ausgabe der Ergebnisse sobald vorhanden.
		for (Future<List<File>> future : found) {
			List<File> list;
			try {
				// Warten bis ein Ergebnis vorliegt.
				list = future.get();
				
				// Ergebnisse durchlaufen.
				for (File f : list) {
					System.out.println(f.getName());
				}
			} catch (InterruptedException e) {
				// Ignore for now
			} catch (ExecutionException e) {
				// Ignore for now
			}
		}

		// ExecutorService beenden
		crawling.shutdown();

		// Ein bestimmte Zeit einräumen die der Executor auf das fertiglaufen
		// von Tasks wartet.
		try {
			crawling.awaitTermination(TIMEOUT, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			System.err
					.println("Crawler has been interrupted while waiting for tasks to finish.");
		} finally {
			System.out.println("Number of found files: "
					+ fileCounter.getFoundCount());
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
