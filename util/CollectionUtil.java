package util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class CollectionUtil
{
	/**
	 * Zip two collections.
	 * If one collection is smaller than the other then additional items are ignored.
	 * @param left The collection of items to be placed in Pair.getLeft().
	 * @param right The collection of items to be placed in Pair.getRight().
	 * @return A non null, potentially empty collection, containing an item from left and right.
	 */
	public static <T, V> Collection<Pair<T, V>> createPair(Collection<T> left, Collection<V> right)
	{
		// Inspired by http://stackoverflow.com/questions/31963297/how-to-zip-two-arraylists
		return IntStream.range(0, Math.min(left.size(), right.size()))
		.mapToObj(i -> new Pair<>(getNth(left.iterator(), i), getNth(right.iterator(), i)))
		.collect(Collectors.toCollection(ArrayList::new));
	}

	/**
	 * A convenience method for iterators, getting the nth item.
	 * @param iterator The iterator to increment. It is modified by this method.
	 * @param n The number of times to call iterator.next().
	 * @return The Nth item.
	 * @throws NoSuchElementException Iff iterator does not have n elements remaining.
	 */
	public static <T> T getNth(Iterator<T> iterator, int n)
	{
		for (int i = 0; i < n - 1; i++)
		{
			iterator.next();
		}
		return iterator.next();
	}

	public static <T> Map<Boolean, List<T>> partitionBy(Collection<T> items, Predicate<T> given)
	{
		return items.stream().collect(Collectors.partitioningBy(given::test));
	}

	/**
	 * Get a list from a stream.
	 * This allows a stream to be accessed multiple times by calling list.stream().
	 */
	public static <T> Optional<List<T>> streamToList(Optional<Stream<T>> stream)
	{
		if (!stream.isPresent())
		{
			return Optional.empty();
		}

		return Optional.of(stream.get().collect(Collectors.toList()));
	}

	/**
	 * Concatenate every non null item with it's string
	 * representation, and a provided delimiter.
	 * @param array The array to operate on.
	 * @param delimiter The string to separate every item with.
	 * @return A string combining every non null item in array with delimiter.
	 * An empty string is returned if array is empty or consists only of null items.
	 */
	public static <T> String arrayConcat(T[] array, String delimiter)
	{
		return Arrays.stream(array)
				.filter(i -> i != null)
				.map(i -> i.toString() + delimiter)
				.reduce("", String::concat);
	}

	/**
	 * Find the most common item in some collection of items.
	 * If there is more than one most common, the first is chosen.
	 * @param items The non null, non empty collection of items to find in.
	 * @return The most common item in items.
	 * @throws NoSuchElementException Iff the collection is empty.
	 * @throws NullPointerException Iff items is null.
	 */
	public static <T> T mostCommon(Collection<T> items)
	{
		return occurrenceCount(items).entrySet().stream()
				.sorted((a, b) -> {return b.getValue() - a.getValue();})
				.findFirst()
				.get()
				.getKey();
	}

	/**
	 * A mapping from every item in items to the number of times they
	 * occur in items.
	 * @param items The collection to count items in.
	 * @return An empty map if collection is empty.
	 * @throws NullPointerException Iff items is empty.
	 */
	public static <T> Map<T, Integer> occurrenceCount(Collection<T> items)
	{
		Map<T, Integer> itemToCount = new HashMap<>();

		for (T item : items)
		{
			itemToCount.putIfAbsent(item, 0);
			itemToCount.computeIfPresent(item, (i, count) -> { return count + 1; });
		}

		return itemToCount;
	}

	/**
	 * Find the range of values that some collection encompasses.
	 * @param items The items to find the range of.
	 * @param property The function to call on each item to find the range of.
	 * @return The maximum value in items minus the minimum value.
	 * @throws NoSuchElementException Iff items is empty.
	 */
	public static <T> double range(Collection<T> items, Function<T, Double> property)
	{
		double minimum = items.stream().map(property).mapToDouble(d -> d).min().getAsDouble();
		double maximum = items.stream().map(property).mapToDouble(d -> d).max().getAsDouble();

		return maximum - minimum;
	}
}
