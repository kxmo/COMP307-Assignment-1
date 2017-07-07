package util;

import java.util.Collection;

/**
 * A pair of items.
 * The references of left and right are final.
 * T is the type of the left item.
 * V is the type of the right item.
 * T and V may be of the same type.
 */
public class Pair<T, V>
{
	private final T left;
	private final V right;

	public Pair(T left, V right)
	{
		this.left = left;
		this.right = right;
	}

	public T getLeft()
	{
		return left;
	}

	public V getRight()
	{
		return right;
	}

	public static <T, V> int countEqual(Collection<Pair<T, V>> results)
	{
		return (int) results.stream()
		.filter(pair -> pair.getLeft().equals(pair.getRight()))
		.count();
	}
}
