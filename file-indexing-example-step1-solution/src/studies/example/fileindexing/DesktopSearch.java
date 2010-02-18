package studies.example.fileindexing;

import java.io.File;
import java.io.FileFilter;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import studies.example.fileindexing.crawl.*;
/**
 * Aufgabe 1
 * 
 * Implementierung des Crawling Teils einer Desktop Suche mit 
 * dem Executor Framework aus Java.
 * 
 * Schritte:
 * 
 * 1. Implementieren der Methoden der Counter Klasse, so dass diese threadsicher sind, zum z�hlen der gefundenen Dateien.
 * 
 * 2. Erzeugen einer sinnvollen ExecutorService Instanz.
 * 
 * 3. Die Klasse CrawlJob so anpassen, dass diese als Task ausgef�hrt werden kann.
 * 
 * 4. Den Z�hler f�r die Dateien in CrawlJob erh�hen. 
 * 
 * 5. CrawlJobs f�r die Verzeichnisse erstellen und an den ExecutorService �bergeben.
 *
 */
public class DesktopSearch {
	// Anzahl der Hardware Threads die auf unserem PC verf�gbar sind
	public static final int THREADS = Runtime.getRuntime()
			.availableProcessors() + 1;
	
	// TIMEOUT welche der ExecutorService hat um ausstehende Tasks zu beenden.
	private static final long TIMEOUT = 10000;
	
	public static void main(String[] args) {
		// Pfade aus den Kommandozeilenargumenten werden als Startverzeichnisse verwendet.
		List<File> paths = new LinkedList<File>();
		for (String path : args) {
			paths.add(new File(path));
		}

		// Z�hler f�r gefundene Dateien
		FileCounter fileCounter = new FileCounter();

		// ExecutorService
		ExecutorService crawling = Executors.newFixedThreadPool(THREADS);

		// Alle Suchpfade durchlaufen
		for (File path : paths) {
			FileFilter filter = getFilter();
			crawling.submit(new CrawlJob(path,filter,fileCounter));
		}
		
		// ExecutorService beenden
		crawling.shutdown();
		
		// Ein bestimmte Zeit einr�umen die der Executor auf das fertiglaufen von Tasks wartet.
		try {
			crawling.awaitTermination(TIMEOUT, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			System.err.println("Crawler has been interrupted while waiting for tasks to finish.");
		} finally {
			System.out.println("Number of found files: "+fileCounter.getFoundCount());
		}
	}

	// Erzeugt einen Dateifilter f�r Textdateien
	private static FileFilter getFilter(){
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
