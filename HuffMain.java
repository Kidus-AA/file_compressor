import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * Implements the Huff class to compress, uncompress, and compare files
 * 
 * @author Kidus Asmare Ayele
 */
public class HuffMain {
	public static void main ( String[] args ) throws IOException {

		boolean programRunner = true;
		while ( programRunner ) { // true until the user quits the program
			try {
				System.out.println();
				System.out.println("(1) Compress file");
				System.out.println("(2) Uncompress file");
				System.out.println("(3) Compare files");
				System.out.println("(4) Quit");

				Scanner userInput = new Scanner(System.in);
				System.out.print("> ");
				int command = userInput.nextInt();
				if ( command == 1 ) { // compresses the file
					Scanner file = new Scanner(System.in);
					System.out.print("File to compress:");
					String fileToCompress = file.nextLine(); // file to compress
					System.out.print("File to save in:");
					String fileToSaveIn = file.nextLine(); // file to save in
					Huff.compress(fileToCompress,fileToSaveIn);
				} else if ( command == 2 ) { // uncompresses the file
					Scanner file = new Scanner(System.in);
					System.out.print("File to uncompress: ");
					String fileToUncompress = file.nextLine(); // file to uncompress
					System.out.print("File to save in: ");
					String fileToSaveIn = file.nextLine(); // file to save in
					Huff.uncompress(fileToUncompress,fileToSaveIn);
				} else if ( command == 3 ) { // compares two files
					Scanner file = new Scanner(System.in);
					System.out.print("File #1: ");
					String file1 = file.nextLine(); // first file
					System.out.print("File #2: ");
					String file2 = file.nextLine(); // second file
					System.out.print(Huff.compare(file1,file2));
				} else if ( command == 4 ) { // exits the program
					programRunner = false;
				} else {
					System.out.println("Expected a number between 1 to 4");
				}
			} catch ( InputMismatchException e ) {
				System.out.println("Expected a number");
			} catch ( FileNotFoundException e ) {
				System.out.println("Expected valid files");
			} catch ( IllegalArgumentException e ) {
				System.out.println("Expected a file that has been compressed");
			}
		}
	}
}
