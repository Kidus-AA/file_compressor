
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * Compresses, uncompresses, and compares files
 * 
 * @author Kidus Asmare Ayele
 */
public class Huff {
	public static void compress ( String file1, String file2 )
	    throws IOException {
		File inputFile = new File(file1);
		FileReader fileInputStream = new FileReader(file1);
		BufferedReader charInputStream = new BufferedReader(fileInputStream); 
		// Creates a buffered reader to read characters

		char[] storeChar = new char[(int) inputFile.length()];
		int readChar = charInputStream.read();
		for ( int i = 0 ; readChar != -1 ; i++ ) {
			char intToChar = (char) readChar;
			storeChar[i] = intToChar; // stores the characters read into an array
			readChar = charInputStream.read();
		}

		Map<Character,Integer> characterMap = new HashMap<Character,Integer>();

		for ( int i = 0 ; i < storeChar.length ; i++ ) {
			if ( characterMap.containsKey(storeChar[i]) ) { // checks if the character
			                                                // already exists
				characterMap.put(storeChar[i],characterMap.get(storeChar[i]) + 1); 
				// adds one to the number of times the character occurs
			} else {
				characterMap.put(storeChar[i],1); // adds a new character and a
				                                  // frequency of one
			}
		}

		PriorityQueue<WeightedCodingTree> charInOrder =
		    new PriorityQueue<WeightedCodingTree>(new WeightedCodingTreeComparator());
		for ( char character : characterMap.keySet() ) {
			charInOrder
			    .add(new WeightedCodingTree(characterMap.get(character),character)); 
			// arranges the characters by the smallest frequency to the biggest frequency
		}

		charInOrder.add(new WeightedCodingTree(1,HuffConstants.PSEUDO_EOF)); // adds
		                                                                     // the
		                                                                     // EOF
		                                                                     // to
		                                                                     // the
		                                                                     // map

		for ( ; charInOrder.size() > 1 ; ) { // loops until there is only one tree
		                                     // left in the priority queue
			WeightedCodingTree tree1 = charInOrder.poll(); // takes out the weighted
			                                               // coding tree that has the
			                                               // smallest frequency (the
			                                               // first one)
			WeightedCodingTree tree2 = charInOrder.poll(); // takes out the weighted
			                                               // coding tree that has the
			                                               // second smallest
			                                               // frequency (the second
			                                               // one)
			WeightedCodingTree combine = new WeightedCodingTree(tree1,tree2); // combines
			                                                                  // the
			                                                                  // two
			                                                                  // trees
			charInOrder.add(combine); // adds the combined tree back into the
			                          // priority queue
		}

		CodingTree optimalEncodingTree = charInOrder.poll().getCodingTree(); 
		// gets the coding tree for the combined priority queue

		File out = new File(file2);
		BitOutputStream outputStream =
		    new BitOutputStream(new FileOutputStream(out));

		outputStream.writeBits(HuffConstants.MAGIC_NUMBER); // writes the magic
		                                                    // number at the
		                                                    // beginning of the file

		optimalEncodingTree.write(outputStream); // writes the coding tree to a file

		Map<Character,BitString> charEncoding = optimalEncodingTree.getEncodings(); 
		// gets the encodings of the coding tree

		FileReader readFile = new FileReader(new File(file1)); 
		// restarts the reading to the beginning of the file

		int readCharacters = readFile.read(); // gets the first character
		for ( ; readCharacters != -1 ; ) { // reads the file until it reaches EOF
			outputStream.writeBits(charEncoding.get((char) readCharacters));
			// writes the compressed characters
			readCharacters = readFile.read(); // reads the next character
		}
		outputStream.write(HuffConstants.PSEUDO_EOF); // writes the EOF at the end
		                                              // of the file

		outputStream.flush();
		readFile.close();
		charInputStream.close();
	}

	public static void uncompress ( String file1, String file2 )
	    throws IOException {
		InputStream inputFile = new FileInputStream(new File(file1));
		BitInputStream inputStream = new BitInputStream(inputFile);
		BitString readMagicNumber =
		    inputStream.readBits(HuffConstants.MAGIC_NUMBER.length()); 
		// reads the first 'magic number length' number of bits

		if ( !readMagicNumber.equals(HuffConstants.MAGIC_NUMBER) ) { // throws an
		                                                             // exception if
		                                                             // the bits
		                                                             // read do not
		                                                             // match the
		                                                             // magic number
			throw new IllegalArgumentException("Please enter a file that has already been compressed");
		}

		File out = new File(file2);
		BitOutputStream outputStream =
		    new BitOutputStream(new FileOutputStream(out));

		Character nextCharacter;
		CodingTree reconstructedCodingTree = new CodingTree(inputStream); 
		// creates a coding tree from the input stream

		for ( ; !inputStream.eof() ; ) {
			nextCharacter = reconstructedCodingTree.nextChar(inputStream); // reads
			                                                               // the next
			                                                               // character
			if ( nextCharacter.equals(HuffConstants.PSEUDO_EOF) ) {
				break; // loop ends if EOF is reached
			}
			outputStream.writeBits(new BitString(nextCharacter)); // writes a
			                                                      // character to an
			                                                      // output file
		}

		outputStream.flush();
		outputStream.close();
		inputStream.close();
	}

	public static String compare ( String file1, String file2 )
	    throws IOException {

		File filer1 = new File(file1);
		File filer2 = new File(file2);

		FileInputStream file1Stream = new FileInputStream(filer1);
		FileInputStream file2Stream = new FileInputStream(filer2);

		byte[] file1ByteStream = new byte[(int) filer1.length()];
		byte[] file2ByteStream = new byte[(int) filer2.length()];
		// stores the bytes in an array

		file1Stream.read(file1ByteStream);
		file2Stream.read(file2ByteStream);
		// adds all of the characters in the file to the byte array

		int byteDifference = 0;
		int file1Byte = 0;
		int file2Byte = 0;
		boolean sameFiles = true;

		for ( int i = 0 ; i < file1ByteStream.length
		    || i < file2ByteStream.length ; i++ ) { // loop continues as long as i <
		                                            // the length of both arrays
			byteDifference = byteDifference + 1;
			if ( i + 1 > file1ByteStream.length || i + 1 > file2ByteStream.length ) { // checks
			                                                                          // if
			                                                                          // the
			                                                                          // two
			                                                                          // files
			                                                                          // have
			                                                                          // different
			                                                                          // lengths
				sameFiles = false;
				return "File length mismatch" + "\n" + "Files are different" + "\n";
			}
			if ( file1ByteStream[i] != file2ByteStream[i] ) { // if the files have the
			                                                  // same length but have
			                                                  // different values
				file1Byte = file1ByteStream[i]; // stores the value where file1 is
				                                // different
				file2Byte = file2ByteStream[i]; // stores the value where file2 is
				                                // different
				sameFiles = false;
				break;
			}
		}

		file1Stream.close();
		file2Stream.close();

		if ( sameFiles == true ) {
			return "The files are the same" + "\n";
		} else {
			return "File mismatch, byte " + byteDifference + ": " + file1Byte + " "
			    + file2Byte + "\n" + "Files are different" + "\n";
		}

	}
}