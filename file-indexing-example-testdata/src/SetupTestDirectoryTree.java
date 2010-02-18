import java.io.File;
import java.io.IOException;
import java.util.Random;

/**
 * This program creates the test folders and files used by the file indexing
 * example application.  
 *
 */
public class SetupTestDirectoryTree {
	// How many folder to create
	private static final int folder_count = 4;
	
	// How many layers of folders
	private static final int layers = 3;
	
	// Count for each dummy file type.
	private static final int txt_max_count = 4;
	private static final int jpg_max_count = 4;
	private static final int pdf_max_count = 4;

	// Counters to create a statistic
	private static int COUNT_FOLDER = 0;
	private static int COUNT_JPG = 0;
	private static int COUNT_PDF = 0;
	private static int COUNT_TXT = 0;

	// Main program
	public static void main(String[] args) throws IOException {
		// Get root create unique named files and folders.
		File root = new File("C:/Temp/eclipse-iws-multithreading/testfolder");
		for(int i = 0; i < 3; ++i){
			File dir = new File(root,"root_"+i);
			dir.mkdir();
			create(dir, layers, "_"+i);
		}
		
		// Show statistics
		System.out.println("Initialized Test Diretories below: "
				+ root.getName());
		System.out.println("======================");
		System.out.println("Folders: " + COUNT_FOLDER);
		System.out.println("======================");
		System.out.println("Text Files: " + COUNT_TXT);
		System.out.println("JPG Files: " + COUNT_JPG);
		System.out.println("PDF Files: " + COUNT_PDF);
		System.out.println("======================");
	}

	// Creates recursivly all layers and files for the test data.
	private static void create(File root, int layer, String basename)
			throws IOException {
		if (layer > 0) {
			for (int i = 0; i < folder_count; ++i) {
				File newRoot = new File(root, "folder" + basename + "_" + layer
						+ "_" + i);
				newRoot.mkdir();
				COUNT_FOLDER++;
				create(newRoot, layer - 1, basename + "_" + layer + "_" + i);
			}
		}
		Random rnd = new Random();
		for (int t = 0; t < rnd.nextInt(txt_max_count)+1; ++t) {
			new File(root, "file" + basename + "_" + layer + "_" + t + ".txt")
					.createNewFile();
			COUNT_TXT++;
		}
		for (int t = 0; t < rnd.nextInt(jpg_max_count)+1; ++t) {
			new File(root, "file" + basename + "_" + layer + "_" + t + ".jpg")
					.createNewFile();
			COUNT_JPG++;
		}
		for (int t = 0; t < rnd.nextInt(pdf_max_count)+1; ++t) {
			new File(root, "file" + basename + "_" + layer + "_" + t + ".pdf")
					.createNewFile();
			COUNT_PDF++;
		}
	}

}
