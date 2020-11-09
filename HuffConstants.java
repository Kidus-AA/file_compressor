/**
 * Constants used by the Huffman coding compression and decompression.
 * 
 * @author Stina Bridgeman
 */
public class HuffConstants {

	/**
	 * 'Chunk' size for reading and writing bits in uncompressed files.
	 */
	public static final int BITS_PER_CHARACTER = 8;

	/**
	 * Magic number to identify huff-compressed files.
	 */
	public static final BitString MAGIC_NUMBER =
	    new BitString("10101010101010101010101010101010");

	/**
	 * Pseudo-eof character.
	 */
	public static final Character PSEUDO_EOF = Character.MIN_VALUE;
}
