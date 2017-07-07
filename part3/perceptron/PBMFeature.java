package part3.perceptron;

import java.awt.Dimension;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * A collection of pixels, and their associated value.
 * A feature can be composed of multiple collections of pixels and their values.
 * All are considered for all methods.
 */
public class PBMFeature
{
	private final Map<Dimension, Boolean> pixelPredicion;
	private final int threshold;

	/**
	 * Create a new feature.
	 * @param pixelPredicion Dimension is valid position on the associated image. Boolean is the predicted value for that position.
	 * @param threshold The minimum required matching dimensions for the feature to be considered present on a given image.
	 */
	public PBMFeature(Map<Dimension, Boolean> pixelPredicion, int threshold)
	{
		// TODO Copy map (add method to collectionUtil)
		this.pixelPredicion = new HashMap<>();
		this.pixelPredicion.putAll(pixelPredicion);

		this.threshold = threshold;
	}
	
	public int size()
	{
		return pixelPredicion.size();
	}

	/**
	 * Determine whether this feature is present for a given image.
	 * @param image The image to test this feature against.
	 * @return True if threshold or greater of the features hold true. False otherwise.
	 */
	public boolean activeOnInstance(PBMImage image)
	{
		return pixelPredicion.entrySet().stream()
				.filter(feature -> featureOnImage(feature, image))
				.count() >= threshold;
	}

	/**
	 * Determine whether this feature is on the image or not.
	 * @param feature The feature to consider.
	 * @param instance The image to test against.
	 * @return True if the feature has the same value present at that position of the image.
	 */
	private static boolean featureOnImage(Entry<Dimension, Boolean> feature, PBMImage instance)
	{
		return instance.getValue(feature.getKey()) == feature.getValue();
	}

	@Override
	public String toString()
	{
		return pixelPredicion.entrySet().stream()
				.map(PBMFeature::featureToString)
				.reduce(String::concat)
				.get();
	}

	/**
	 * Get feature in a human readable format.
	 * @param feature The non null feature to consider.
	 * @return
	 * @throws NullPointerException Iff feature is null.
	 */
	private static String featureToString(Entry<Dimension, Boolean> feature)
	{
		return "(" + dimensionToString(feature.getKey()) + " " + feature.getValue() + ")\n";
	}

	/**
	 * Get the dimension in a human readable format.
	 * @param d A non null dimension.
	 * @return A non null string containing d.height and d.width.
	 * @throws NullPointerException Iff d is null.
	 */
	private static String dimensionToString(Dimension d)
	{
		return d.height + ", " + d.width;
	}
}
