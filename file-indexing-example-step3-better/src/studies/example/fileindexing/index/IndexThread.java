package studies.example.fileindexing.index;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import studies.example.fileindexing.FileCounter;

/**
 *
 * Dieser Thread dient zum indizieren der einzelnen Dateien die durch den Crawler gefunden werden.
 *
 */
public class IndexThread extends Thread {
	// Wenn dieses Objekt in der Queue ankommt muss der Thread terminieren
	private static final File POISON = new File("");

	// Zähler der Dateien
	private final FileCounter fileCounter;
	
	// Queue zur Übergabe der Ergebnisse.
	private final BlockingQueue<File> queue;

	// Executor zum Ausführen  
	private final ExecutorService indexExecutor;

	// Neuen Thread mit der gegebenen Queue und dem FileCounter erzeugen.
	// Zusätzlich wird ein Executor zum durchführen 
	public IndexThread(final BlockingQueue<File> queue, final FileCounter fileCounter, final ExecutorService executor) {
		this.queue = queue;
		this.fileCounter = fileCounter;
		this.indexExecutor = executor;
	}

	// Optimierung: Es werden weitere Tasks mit Listen einer bestimmten Bucketsize zum indizieren gestartet.
	@Override
	public void run() {
		List<Future<?>> tasks = new LinkedList<Future<?>>();
		int bucket = 20;
		List<File> list = new LinkedList<File>();
		try {
			while (true) {
				// Element aus der Queue nehmen
				final File f = queue.take();
				// Wenn das Element gleich dem POISON Paket ist, den Thread terminieren lassen.
				// Hier wird ein letzter Task mit den übrigen Files erzeugt
				if (f.equals(POISON)) {
					tasks.add(indexExecutor.submit(new IndexJob(new LinkedList<File>(list), fileCounter)));
					list.clear();
					break;
				} else {
					// Solange bucket size nicht erreicht wurde, die Dateien nur in die Liste einfügen.
					// Wenn danach die bucket size erreicht wurde, neuen Task erzeugen und lokale liste
					// leeren.
					list.add(f);
					if(list.size() >= bucket){
						tasks.add(indexExecutor.submit(new IndexJob(new LinkedList<File>(list), fileCounter)));
						list.clear();
					}
				}
			}
		} catch (final InterruptedException ex) {
			// Consumed!
		}
		// Auf jeden Fall erst terminieren wenn alle Tasks fertig gelaufen sind und damit das indizieren abgeschlossen ist.
		for(Future<?> fut : tasks){
			try {
				fut.get();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
	}
}
