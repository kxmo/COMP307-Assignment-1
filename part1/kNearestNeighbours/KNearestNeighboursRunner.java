package part1.kNearestNeighbours;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import util.Assert;
import util.CollectionPrinter;
import util.FileLoader;
import util.Pair;
import util.CollectionUtil;

public class KNearestNeighboursRunner
{
	public static void parse(String[] args)
	{
		Assert.isTrue(args != null && args.length == 3, usageInformation());

		String trainingFilename = args[0];
		String testFilename = args[1];
		final int kNearest = Integer.parseInt(args[2]);
		
		Optional<Collection<Iris>> trainingSet = createIrisSet(FileLoader.getContents(FileLoader.getFileReader(trainingFilename)));
		Assert.isTrue(trainingSet.isPresent(), String.format("Could not read training set data from '%s'", trainingFilename));

		Optional<Collection<Iris>> testSet = createIrisSet(FileLoader.getContents(FileLoader.getFileReader(testFilename)));
		Assert.isTrue(testSet.isPresent(), String.format("Could not read test set data from '%s'", testFilename));

		Collection<Pair<Iris, Iris>> results = KNearestNeighbours.applyTestSet(trainingSet.get(), testSet.get(), kNearest);

		CollectionPrinter.printPairRight(results);
		CollectionPrinter.printPercentageCorrect(results);
	}
	
	/**
	 * Usage information for calling this program from the command line.
	 */
	private static String usageInformation()
	{
		String speciesAvailable = CollectionUtil.arrayConcat(Species.values(), "\n");
		
		return "USAGE:\n"
				+ "Arguments:\n"
				+ "trainingDataFilename testDataFilename k\n"
				+ "Both the training data and the test data must be formatted as follows:\n"
				+ "sepalLength sepalWidth petalLength petalWidth classification\n"
				+ "Where every length or width is of type double, and the classification is one of:\n"
				+ speciesAvailable
				+ "prepended by Iris-\n"
				+ "k must be an non negative, non zero integer denoting the number of training neighbours to consider for a given test instance.";
	}

	/**
	 * Parse a series of lines representing flowers in the iris dataset.
	 * @param contents A series of lines, each representing exactly one Iris.
	 * @return Optional.of(Collection containing every item in contents as an Iris) or
	 * Optional.empty() if contents is null, not present, or malformed.
	 */
	private static Optional<Collection<Iris>> createIrisSet(Optional<Stream<String>> contents)
	{
		if (contents != null && contents.isPresent())
		{
			List<String> lines = contents.get().collect(Collectors.toList());
			Collection<Iris> flowers = new ArrayList<>();

			for (String line : lines)
			{
				String[] items = line.split("  ");
				Iris flower;
				
				if (items.length == 1) // Blank lines
				{
					continue;
				}

				try
				{
					if (items.length != 5)
					{
						throw new IndexOutOfBoundsException();
					}
					
					flower = new Iris(
							Double.parseDouble(items[0]),
							Double.parseDouble(items[1]),
							Double.parseDouble(items[2]),
							Double.parseDouble(items[3]),
							stringToSpecies(items[4]).get());
				}
				catch (NumberFormatException // Double is not as expected
						| NoSuchElementException // Species string is not valid
						| IndexOutOfBoundsException e) // Incorrect number of items on this line
				{
					return Optional.empty();
				}

				flowers.add(flower);
			}

			return Optional.of(flowers);
		}
		else
		{
			return Optional.empty();
		}
	}

	/**
	 * Convert val into the string representation expected in the Iris dataset.
	 * @param val The string to compare to the expected representation.
	 * @return Optional.of(the matching Species) if one exists, Optional.empty() otherwise.
	 */
	private static Optional<Species> stringToSpecies(String val)
	{
		return Stream.of(Species.values())
				.filter(species -> { return ("Iris-" + species.toString()).equals(val); })
				.findAny(); // Enums are unique, so any will do.
	}
}
