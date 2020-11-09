
/**
 * Builds up the full coding tree using character frequency
 * 
 * @author Kidus Asmare Ayele
 */
public class WeightedCodingTree {
	private int weight_; // the tree's weight
	private Trie<BitString,Character> weightedCharacterTree_; // a trie that
	                                                          // contains
	                                                          // characters and
	                                                          // their weight

	/**
	 * @param weight
	 *          weight of a character
	 * @param Character
	 *          an uncompressed character
	 */
	public WeightedCodingTree ( int weight, char Character ) {
		weight_ = weight;
		weightedCharacterTree_ = new Trie<BitString,Character>(Character);
	}

	/**
	 * @param tree1
	 *          a weighted coding tree
	 * @param tree2
	 *          a weighted coding tree
	 */
	public WeightedCodingTree ( WeightedCodingTree tree1,
	                            WeightedCodingTree tree2 ) {
		weight_ = tree1.getWeight() + tree2.getWeight(); // adds the weights of the
		                                                 // two trees
		weightedCharacterTree_ = new Trie<BitString,Character>(); // creates a new
		                                                          // trie to attach
		                                                          // to
		weightedCharacterTree_.attach(weightedCharacterTree_.getRoot(),
		                              tree1.weightedCharacterTree_,BitString.ZERO); // attaches
		                                                                            // the
		                                                                            // first
		                                                                            // tree
		                                                                            // to
		                                                                            // the
		                                                                            // left
		weightedCharacterTree_.attach(weightedCharacterTree_.getRoot(),
		                              tree2.weightedCharacterTree_,BitString.ONE); // attaches
		                                                                           // the
		                                                                           // second
		                                                                           // tree
		                                                                           // to
		                                                                           // the
		                                                                           // right
	}

	/**
	 * Gets the coding tree for a weighted coding tree
	 * 
	 * @return new coding tree with the stored trie
	 */
	public CodingTree getCodingTree () {
		CodingTree tree = new CodingTree(weightedCharacterTree_);
		return tree;
	}

	/**
	 * Gets the weight of a weighted coding tree
	 * 
	 * @return the weight
	 */
	public int getWeight () {
		return weight_;
	}
}
