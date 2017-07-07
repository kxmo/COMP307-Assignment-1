package part1.kNearestNeighbours;
import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

import util.Assert;
import util.Pair;
import util.CollectionUtil;

import static util.MathUtil.square;

public class KNearestNeighbours
{
	/**
	 * Applies classify to every value in the testSet against every value in the training set
	 * and collates the results.
	 * @param trainingSet The training set as defined in classify.
	 * @param testSet The test set as defined in classify.
	 * @param kNearest kNearest as defined in classify.
	 * @return A map from every test set instance to the instance predicted by classify.
	 * Note that a collection of Pairs must be used, rather than a map, because multiple instances
	 * of a single flower may appear in a single training or test set.
	 */
	public static Collection<Pair<Iris, Iris>> applyTestSet(Collection<Iris> trainingSet, Collection<Iris> testSet, int kNearest)
	{
		return testSet.stream()
				.map(instance -> { return new Pair<Iris, Iris>(instance, KNearestNeighbours.classify(trainingSet, instance, kNearest)); })
				.collect(Collectors.toList());
	}

	/**
	 * Classify a previously unknown species of Iris given a training set, the instance to
	 * classify, and the specification of the k nearest neighbours to consider.
	 * @param trainingCollection A group of known Iris examples. May not be null or empty.
	 * @param instance An unknown instance of an Iris. Iris.species is ignored.
	 * @param kNearest A non zero, non negative count of the nearest neighbours to consider.
	 * @return A new Iris with identical characteristics to instance, with the Iris.species
	 * modified to fit the prediction made by the KNearestNeighbours algorithm.
	 */
	public static Iris classify(Collection<Iris> trainingCollection, Iris instance, int kNearest)
	{
		Assert.NonNull(trainingCollection, instance);
		Assert.NonEmpty(trainingCollection);
		Assert.NonZero(kNearest);
		Assert.NonNegative(kNearest);

		Comparator<Iris> nearestFirst = createDistanceMeasure(
				CollectionUtil.range(trainingCollection, Iris::getSepalLength),
				CollectionUtil.range(trainingCollection, Iris::getSepalWidth),
				CollectionUtil.range(trainingCollection, Iris::getPetalLength),
				CollectionUtil.range(trainingCollection, Iris::getPetalWidth));

		// http://stackoverflow.com/questions/25878948/how-to-sort-a-stream-by-parameter-using-a-comparator-in-java-8
		Comparator<Iris> minimumEuclideanDistance = Comparator.comparingDouble(p -> nearestFirst.compare(p, instance));

		Species species = CollectionUtil.mostCommon(trainingCollection.stream()
				.sorted(minimumEuclideanDistance)
				.limit(kNearest)
				.map(Iris::getSpecies)
				.collect(Collectors.toList()));

		return new Iris(
				instance.getSepalLength(),
				instance.getSepalWidth(),
				instance.getPetalLength(),
				instance.getPetalWidth(),
				species);
	}

	/**
	 * Create a comparator that implements the distance measure
	 * for two numeric values as discussed in lectures.
	 * rX refers to the range of values in the training set for
	 * a particular attribute of an Iris. These attributes are
	 * listed below.
	 * All ranges are in cm.
	 * @param r1 sepalLength 
	 * @param r2 sepalWidth
	 * @param r3 petalLength
	 * @param r4 petalWidth
	 * @return
	 */
	private static Comparator<Iris> createDistanceMeasure(double r1, double r2, double r3, double r4)
	{
		return (a, b) ->
		{
			// We need to increase the range of all of these values, because comparators return
			// an integer, and the values here are low so are rounded to zero.
			// It doesn't matter what value is used, provided the values are distinguishable
			// at an integer level of precision and applied consistently.
			final int modifier = 100;
			return (int) Math.sqrt(
					(square(a.getSepalLength() - b.getSepalLength()) / square(r1 / modifier))
					+ (square(a.getSepalWidth() - b.getSepalWidth()) / square(r2 / modifier))
					+ (square(a.getPetalLength() - b.getPetalLength()) / square(r3 / modifier))
					+ (square(a.getPetalWidth() - b.getPetalWidth()) / square(r4 / modifier))
					);
		};
	}
}
