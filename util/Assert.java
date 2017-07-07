package util;
import java.util.Collection;

public class Assert
{
	/**
	 * Exits with an error code of 1 if test is false.
	 * Prints message before exiting.
	 * @param test The assertion to make.
	 * @param message Message to be printed before exiting.
	 */
	public static void isTrue(boolean test, String message)
	{
		if (!test)
		{
			System.out.println(message);
			System.exit(1);
		}
	}

	/**
	 * Asserts isTrue for every item in items, and items itself
	 * is non null.
	 * @param items The items to assert are non null.
	 */
	public static <T> void NonNull(T ... items)
	{
		isTrue(items != null, "items is null");
		for (T item : items)
		{
			isTrue(item != null, "item is null");
		}
	}

	/**
	 * Asserts isTrue that trainingCollection is non empty.
	 * @param trainingCollection The collection of items to test.
	 * @throws NullPointerException Iff trainingCollection is null.
	 */
	public static <T> void NonEmpty(Collection<T> trainingCollection)
	{
		isTrue(!trainingCollection.isEmpty(), "Collection is empty");
	}

	/**
	 * Asserts isTrue that number is non negative.
	 * @param number The number to test. May be equal to zero.
	 */
	public static void NonNegative(int number)
	{
		isTrue(number >= 0, String.format("Number is negative (%d)", number));
	}

	/**
	 * Asserts isTrue that number is non zero.
	 * @param number The number to test. May be negative.
	 */
	public static void NonZero(int number)
	{
		isTrue(number != 0, "Number is equal to 0");
	}

}
