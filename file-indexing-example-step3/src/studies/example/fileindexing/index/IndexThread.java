package studies.example.fileindexing.index;

import java.io.File;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import studies.example.fileindexing.FileCounter;

/**
 *
 * Dieser Thread dient zum indizieren der einzelnen Dateien die durch den Crawler gefunden werden.
 *
 */
public class IndexThread extends Thread {
	// Wenn dieses Objekt in der Queue ankommt muss der Thread terminieren
	private static final File POISON = new File("");
	
	// Maximale Dauer des indizierens.
	private static final int MAX_TIMEOUT = 500;
	
	// Zufallszahlengenerator
	private static final Random rnd = new Random();

	// Zähler der Dateien
	private final FileCounter fileCounter;
	
	// Queue zur Übergabe der Ergebnisse.
	private final BlockingQueue<File> queue;

	// Neuen Thread mit der gegebenen Queue und dem FileCounter erzeugen.
	public IndexThread(final BlockingQueue<File> queue, final FileCounter fileCounter) {
		this.queue = queue;
		this.fileCounter = fileCounter;
	}

	@Override
	public void run() {
		try {
			while (true) {
				// Element aus der Queue nehmen
				final File f = queue.take();
				// Wenn das Element gleich dem POISON Paket ist, den Thread terminieren lassen.
				if (f.equals(POISON)) {
					break;
				} else {
					index(f);
				}
			}
		} catch (final InterruptedException ex) {
			// We're done.
		}
	}

	private String index(final File file) {
		try {
			TimeUnit.MILLISECONDS.sleep(rnd.nextInt(MAX_TIMEOUT));
		} catch (final InterruptedException e) {
			/* just return the value */
		}
		fileCounter.incrementIndexed();
		return file.getName();
	}
}
