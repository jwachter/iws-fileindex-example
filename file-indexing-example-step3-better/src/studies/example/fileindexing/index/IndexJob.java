package studies.example.fileindexing.index;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import studies.example.fileindexing.FileCounter;

public class IndexJob implements Runnable {

	private final List<File> toIndex;

	private final FileCounter counter;

	// Maximale Dauer des indizierens.
	private static final int MAX_TIMEOUT = 500;

	// Zufallszahlengenerator
	private static final Random rnd = new Random();

	public IndexJob(List<File> toIndex, FileCounter counter) {
		super();
		this.toIndex = toIndex;
		this.counter = counter;
	}

	@Override
	public void run() {
		index(toIndex);
	}

	private List<String> index(final List<File> toIndex2) {
		List<String> list = new LinkedList<String>();
		try {
			TimeUnit.MILLISECONDS.sleep(rnd.nextInt(MAX_TIMEOUT));
		} catch (final InterruptedException e) {
			/* just return the value */
		}
		for(File f : toIndex2){
			list.add(f.getName());
			counter.incrementIndexed();
		}
		return list;
	}
}
