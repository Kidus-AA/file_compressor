import java.util.Comparator;

/**
 * Prioritizes trees from the least weight to the most weight
 * 
 * @author Kidus Asmare Ayele
 */
public class WeightedCodingTreeComparator
    implements Comparator<WeightedCodingTree> {

	@Override
	public int compare ( WeightedCodingTree tree1, WeightedCodingTree tree2 ) {
		if ( tree1.getWeight() < tree2.getWeight() ) {
			return -1;
		} else if ( tree1.getWeight() > tree2.getWeight() ) {
			return 1;
		} else {
			return 0;
		}
	}
}
