package util;

import java.util.Collection;

public class CollectionPrinter
{
	/**
	 * Prints every right element (V) in results.
	 * @param results An optionally empty collection of non null pairs.
	 * @throws NullPointerException Iff any item in Collection is null.
	 */
	public static <T, V> void printPairRight(Collection<Pair<T, V>> results)
	{
		results.stream()
		.map(pair -> pair.getRight())
		.forEach(System.out::println);
	}

	/**
	 * Print the percentage of pairs that are equal in results.
	 * @param results A collection of non null pairs. May not be empty.
	 * @throws ArithmeticException Iff results is empty.
	 * @throws NullPointerException Iff results or any item within results is null.
	 */
	public static <T, V> void printPercentageCorrect(Collection<Pair<T, V>> results)
	{
		int totalCorrect = Pair.countEqual(results);
		int totalClassifications = results.size();

		System.out.println((double) totalCorrect / (double) totalClassifications * 100 + "% correct");
	}
}
