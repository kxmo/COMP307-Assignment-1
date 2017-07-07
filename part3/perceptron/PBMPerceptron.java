package part3.perceptron;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.IntUnaryOperator;
import java.util.stream.Collectors;

/**
 * A perceptron for PBM images.
 */
public class PBMPerceptron
{
	private final Map<PBMFeature, Integer> weights;
	private final int threshold;

	/**
	 * Create a new perceptron.
	 * @param threshold The number of active features required for classify to return true.
	 * @param weights The weights given to each feature that this perceptron considers.
	 */
	public PBMPerceptron(int threshold, Map<PBMFeature, Integer> weights)
	{
		this.weights = weights; //TODO map copy
		this.threshold = threshold;
	}

	/**
	 * Determine whether a given instance has a sufficient
	 * number of active features to be classified as true.
	 * @param instance The image to consider.
	 * @return True iff the instance is a member of the class.
	 * False otherwise.
	 */
	public boolean classify(PBMImage instance)
	{
		int sumOfActiveFeatures = weights.entrySet().stream()
				.filter(entry -> getFeature(entry).activeOnInstance(instance))
				.mapToInt(PBMPerceptron::getWeight)
				.sum();

		return sumOfActiveFeatures > threshold;
	}

	/**
	 * Generate a perceptron with modified weights
	 * @param instance The image to modify the weights on.
	 * @param modifyWeight The degree to which each active weight is modified.
	 * @return A new perceptron with the threshold, and any active feature's weights
	 * modified according to modifyWeight.
	 */
	public PBMPerceptron learn(PBMImage instance, IntUnaryOperator modifyWeight)
	{
		Map<PBMFeature, Integer> newWeights = new HashMap<>();
		int newThreshold = modifyWeight.applyAsInt(threshold);

		for (Entry<PBMFeature, Integer> entry : weights.entrySet())
		{
			if (getFeature(entry).activeOnInstance(instance))
			{
				newWeights.put(getFeature(entry), modifyWeight.applyAsInt(getWeight(entry)));
			}
			else
			{
				newWeights.put(getFeature(entry), getWeight(entry));
			}
		}

		return new PBMPerceptron(newThreshold, newWeights);
	}

	/**
	 * A human readable method for getting the weight from an entry.
	 * @param entry The entry in the map weights.
	 * @return The value side of the entry.
	 */
	private static int getWeight(Entry<PBMFeature, Integer> entry)
	{
		return entry.getValue();
	}

	/**
	 * A human readable method for getting the feature from an entry.
	 * @param entry The entry in the map weights.
	 * @return The key side of the entry.
	 */
	private static PBMFeature getFeature(Entry<PBMFeature, Integer> entry)
	{
		return entry.getKey();
	}

	public String reportFeaturesAndWeights()
	{
		return weights.entrySet().stream()
				.map(e -> "Weight: " + e.getValue().toString() + "\n"
						+ e.getKey().toString() + "\n")
				.reduce(String::concat).get();
	}

	public String reportWeightValues()
	{
		return weights.values().stream()
				.collect(Collectors.toList())
				.toString();
	}

	/**
	 * A list of all features, where a 1 is used if the
	 * feature is activated for this instance, a 0 otherwise.
	 * @param instance The instance to test all features against.
	 * @return A string representation of the feature values for
	 * this instance.
	 */
	public String reportFeatureValues(PBMImage instance)
	{
		return weights.keySet().stream()
				.mapToInt(f -> { return f.activeOnInstance(instance) ? 1 : 0; })
				.boxed() // Collection is easier using a Stream<Integer> over an IntStream.
				.collect(Collectors.toList())
				.toString();
	}

	@Override
	public String toString()
	{
		return threshold + " " + reportWeightValues();
	}
}
