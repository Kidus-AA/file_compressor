import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * A trie that has a 0 child and 1 child
 * 
 * @author Kidus Asmare Ayele
 */
public class CodingTree {
	Trie<BitString,Character> characterTree_; // a trie that contains characters

	/**
	 * @param characterTree
	 *          a trie that contains characters
	 */
	public CodingTree ( Trie<BitString,Character> characterTree ) {
		characterTree_ = characterTree;
	}

	/**
	 * @param inputStream
	 *          input stream that the coding tree is read from
	 * @throws IOException
	 */
	public CodingTree ( BitInputStream inputStream ) throws IOException {
		characterTree_ = treeReader(inputStream);
	}

	/**
	 * Reads bits from an input stream and returns a subtree
	 * 
	 * @param inputStream
	 *          stream that contains characters
	 * @return a trie containing characters and their paths
	 * @throws IOException
	 */
	private Trie<BitString,Character> treeReader ( BitInputStream inputStream )
	    throws IOException {
		if ( inputStream.eof() ) { // checks the EOF has been reached
			return null;
		}
		BitString bit = inputStream.readBits(1); // reads one bit
		if ( bit != null ) {
			if ( bit.equals(BitString.ONE) ) { // if the bit is a leaf
				BitString bitToChar =
				    inputStream.readBits(HuffConstants.BITS_PER_CHARACTER); // reads 8
				                                                            // bits

				Character character = bitToChar.toCharacter(); // converts the 8 bits to
				                                               // a
				                                               // character
				return new Trie<BitString,Character>(character);
			} else if ( bit.equals(BitString.ZERO) ) { // if the bit is an internal
			                                           // node
				Trie<BitString,Character> leftChild = treeReader(inputStream); // moves
				                                                               // to the
				                                                               // left
				                                                               // child
				Trie<BitString,Character> rightChild = treeReader(inputStream); // moves
				                                                                // to
				                                                                // the
				                                                                // right
				                                                                // child

				Trie<BitString,Character> stiched = new Trie<BitString,Character>(); // a
				                                                                     // new
				                                                                     // trie

				if ( leftChild != null ) {
					stiched.attach(stiched.getRoot(),leftChild,BitString.ZERO); // attaches
					                                                            // the
					                                                            // left
					                                                            // child
					                                                            // to the
					                                                            // trie
				}
				if ( rightChild != null ) {
					stiched.attach(stiched.getRoot(),rightChild,BitString.ONE); // attaches
					                                                            // the
					                                                            // right
					                                                            // child
					                                                            // to the
					                                                            // trie
				}
				return stiched;
			}
		}
		return null;
	}

	/**
	 * Writes the coding tree to an output stream
	 * 
	 * @param outputStream
	 *          stream that the compressed characters are sent to
	 * @throws IOException
	 */
	public void write ( BitOutputStream outputStream ) throws IOException {
		writer(characterTree_,characterTree_.getRoot(),outputStream);
	}

	/**
	 * Helper method to write the coding tree to an output stream
	 * 
	 * @param characterTree
	 *          a trie containing characters and their bit string
	 * @param node
	 *          the root of the subtree
	 * @param outputStream
	 *          the output stream
	 * @throws IOException
	 */
	private void writer ( Trie<BitString,Character> characterTree,
	                      Node<Character> node, BitOutputStream outputStream )
	    throws IOException {
		if ( characterTree.isLeaf(node) ) { // if the node is a leaf
			outputStream.writeBits(BitString.ONE);
			BitString characterBits = new BitString(node.getElement());
			outputStream.writeBits(characterBits); // writes the character

		} else if ( characterTree.isInternal(node) ) { // if the node is internal
			outputStream.writeBits(BitString.ZERO); // writes
			Node<Character> left = characterTree.getChild(node,BitString.ZERO); // moves
			                                                                    // to
			                                                                    // the
			                                                                    // left
			                                                                    // child
			Node<Character> right = characterTree.getChild(node,BitString.ONE); // moves
			                                                                    // to
			                                                                    // the
			                                                                    // right
			                                                                    // child

			writer(characterTree,left,outputStream); // calls the method on the left
			                                         // child
			writer(characterTree,right,outputStream); // calls the method on the right
			                                          // child
		}
	}

	/**
	 * Reads bits from an input stream and returns characters
	 * 
	 * @param inputStream
	 *          stream with bits
	 * @return a character
	 * @throws IOException
	 */
	public Character nextChar ( BitInputStream inputStream ) throws IOException {
		Node<Character> current = characterTree_.getRoot();
		BitString bitFromInputStream = new BitString();

		while ( !characterTree_.isLeaf(current) && !inputStream.eof() ) {
			if ( inputStream.eof() ) { // checks if the EOF has been reached
				return null;
			}
			bitFromInputStream = inputStream.readBits(1); // reads the first bit
			if ( bitFromInputStream.equals(BitString.ONE) ) {
				current = characterTree_.getChild(current,BitString.ONE); // moves the
				                                                          // node to the
				                                                          // right

			} else {
				current = characterTree_.getChild(current,BitString.ZERO); // moves the
				                                                           // node to
				                                                           // the left
			}
		}
		return current.getElement().charValue(); // returns the character
	}

	/**
	 * Adds the path to each character to a map
	 * 
	 * @return a map with characters as the key and their path as the value
	 */
	public Map<Character,BitString> getEncodings () {
		Map<Character,BitString> rootToLeaf = new HashMap<Character,BitString>();
		BitString encoding = new BitString();
		rootToLeaf =
		    encoder(characterTree_,characterTree_.getRoot(),rootToLeaf,encoding);
		return rootToLeaf;
	}

	/**
	 * Reads through a trie and adds the path to each character in a map
	 * 
	 * @param characterTree
	 *          a trie containing characters and their bit string
	 * @param node
	 *          the root of the subtree
	 * @param charMap
	 *          an empty map
	 * @param a
	 *          an empty bit string
	 * @return a map with characters and their paths
	 */
	private static Map<Character,BitString> encoder ( Trie<BitString,Character> characterTree,
	                                                  Node<Character> node,
	                                                  Map<Character,BitString> charMap,
	                                                  BitString a ) {
		if ( characterTree.isLeaf(node) ) { // if the node is a leaf
			charMap.put(node.getElement(),a); // adds the character in the node with
			                                  // the bit string to the map
		} else {
			encoder(characterTree,characterTree.getChild(node,BitString.ZERO),charMap,
			        a.concat(0)); // moves to the left child
			encoder(characterTree,characterTree.getChild(node,BitString.ONE),charMap,
			        a.concat(1)); // moves to the right child

			return charMap;
		}
		return charMap; // returns a character map
	}
}