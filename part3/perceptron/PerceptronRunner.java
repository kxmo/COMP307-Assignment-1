package part3.perceptron;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.function.IntUnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import util.Assert;
import util.CollectionUtil;
import util.FileLoader;
import util.Pair;

public class PerceptronRunner
{
	public static void parse(String[] args)
	{
		Assert.isTrue(args != null && args.length == 1, usageInformation());

		String imagesFilename = args[0];

		Optional<Stream<String>> imagesContents = FileLoader.getContents(FileLoader.getFileReader(imagesFilename));
		Assert.isTrue(imagesContents.isPresent(), String.format("Could not read images data from '%s'", imagesFilename));

		Stream<String> imagesRaw = splitIntoImages(CollectionUtil.streamToList(imagesContents).get(), "P1");

		Optional<Collection<PBMImage>> images = FileLoader.parseFile(imagesRaw, PerceptronRunner::parsePBM);
		Assert.isTrue(images.isPresent(), String.format("Could not parse image data from '%s'", imagesFilename));

		Pair<PBMPerceptron, Integer> trainedPerceptron = trainPerceptron(images.get(), 10, 10, 4);

		printPerceptron(trainedPerceptron);
	}

	private static void printPerceptron(Pair<PBMPerceptron, Integer> trainedPerceptron)
	{
		System.out.println("Images incorrectly classified: " + trainedPerceptron.getRight());
		System.out.println("Format: [x, y pixel value]\n");
		System.out.println(trainedPerceptron.getLeft().reportFeaturesAndWeights());
	}

	private static Pair<PBMPerceptron, Integer> trainPerceptron(Collection<PBMImage> collection, int featureWidth, int featureHeight, int featureAspects)
	{
		List<PBMFeature> features = randomFeatures(50, featureWidth, featureHeight, featureAspects);
		int threshold = 1;
		PBMPerceptron perceptron = new PBMPerceptron(threshold, createWeightMapping(features));

		int correct = 0;
		for (int epoch = 0; epoch < 100; epoch++)
		{
			correct = 0;
			
			for (PBMImage instance : collection)
			{
				boolean instanceClass = instance.getType().equals("Yes"); // FIXME get classes from data

				if (perceptron.classify(instance) != instanceClass)
				{
					IntUnaryOperator weightChange = instanceClass ? PerceptronRunner::increaseWeight : PerceptronRunner::decreaseWeight;
					perceptron = perceptron.learn(instance, weightChange);
				}
				else
				{
					correct++;
				}
			}

			if (correct >= collection.size())
			{
				System.out.println("Cycles to convergence: " + epoch);
				break;
			}
		}

		return new Pair<PBMPerceptron, Integer>(perceptron, collection.size() - correct);
	}

	private static int increaseWeight(int i)
	{
		return i + 1;
	}

	private static int decreaseWeight(int i)
	{
		return i - 1;
	}

	/**
	 * @return a mapping from every item in features to the value(s)
	 * provided by initialWeightValue().
	 */
	private static <T> Map<T, Integer> createWeightMapping(List<T> features)
	{
		// FIXME If two identical features are present then an illegal argument exception is thrown.
		return features.stream().collect(Collectors.toMap(feature -> feature, feature -> initialWeightValue()));
	}

	/**
	 * @return The initial value for each weight.
	 * May be positive or negative although no guarantee
	 * is given to the distribution or variance of the value returned.
	 */
	private static int initialWeightValue()
	{
		Random r = new Random();
		return r.nextInt(2); // The value is exclusive, so valid numbers are 0 and 1.
	}

	/**
	 * Generate featureCount number of features, each with aspectsPerFeature
	 * features, where the pixel position is limited to widthBound, heightBound
	 * exclusive. Must be positive. 
	 * @param featureCount The number of features to generate.
	 * @param widthBound The maximum value of the image width, exclusive.
	 * @param heightBound The maximum value of the image height, exclusive.
	 * @param aspectsPerFeature The number of pixels to consider per feature.
	 * @return A new list of random features.
	 */
	private static List<PBMFeature> randomFeatures(int featureCount, int widthBound, int heightBound, int aspectsPerFeature)
	{
		Random r = new Random();
		List<PBMFeature> features = new ArrayList<>();

		for (int i = 0; i < featureCount; i++)
		{
			Map<Dimension, Boolean> pixelPrediction = new HashMap<>();

			// Due to randomness, items in the map can be overridden, so iterating aspectsPerFeature times is not sufficient.
			while (pixelPrediction.size() < aspectsPerFeature)
			{
				Dimension pixelPosition = new Dimension(r.nextInt(widthBound), r.nextInt(heightBound));
				boolean pixelValue = r.nextBoolean();
				pixelPrediction.put(pixelPosition, pixelValue);
			}
			features.add(new PBMFeature(pixelPrediction, 3));
		}

		return features;
	}

	/**
	 * Create one PBMImage from text that represents one image.
	 * @param image An optionally null string with the following format:
	 *
	 * #comment
	 * width height
	 * xy
	 *
	 * Where x and y are parsed according to PerceptronRunner.intToBoolean.
	 * @return Optional.of(a new PBMImage) iff image could be parsed.
	 * Optional.empty() otherwise.
	 */
	private static Optional<PBMImage> parsePBM(String image)
	{
		if (image == null || image.split("\n").length < 3)
		{
			return Optional.empty();
		}

		try
		{
			String[] properties = image.split("\n");
			String comment = properties[0];
			String[] rawDimensions = properties[1].split(" ");
			String[] rawStringData = CollectionUtil.arrayConcat(Arrays.copyOfRange(properties, 2, properties.length), "").split("");

			int[] rawIntData = Arrays.stream(rawStringData).mapToInt(Integer::parseInt).toArray();

			String type = comment.substring(1);
			int width = Integer.parseInt(rawDimensions[0]);
			int height = Integer.parseInt(rawDimensions[1]);
			boolean[][] data = new boolean[height][width];

			for (int i = 0, j = 0; i < rawIntData.length && j < data.length; i += width, j++)
			{
				int[] intSlice = Arrays.copyOfRange(rawIntData, i, i + width);
				data[j] = intToBoolean(intSlice);
			}

			return Optional.of(new PBMImage(type, data));
		}
		catch (NumberFormatException // Width, height, or data are malformed
				| IndexOutOfBoundsException e) // Comment is malformed or empty
		{
			return Optional.empty();
		}
	}

	/**
	 * Convert an array of integers to an array of booleans.
	 * ints == 1 are converted to true, all others are false.
	 * @param intSlice The integer array to convert.
	 * @return A new array, as specified above.
	 */
	private static boolean[] intToBoolean(int[] intSlice)
	{
		boolean[] bool = new boolean[intSlice.length];

		for (int i = 0; i < intSlice.length; i++)
		{
			bool[i] = (intSlice[i] == 1);
		}

		return bool;
	}

	/**
	 * Split a list of lines based on some delimiter.
	 * The delimiter is removed from the final set.
	 * @param lines The lines that contain images.
	 * @param delimiter The start of each image.
	 * @return A stream of strings, the length of the number of images in lines.
	 * Properties of the image are new line separated.
	 */
	private static Stream<String> splitIntoImages(List<String> lines, String delimiter)
	{
		return Stream.of(CollectionUtil.arrayConcat(lines.toArray(), "\n").split(delimiter))
				.filter(string -> !string.equals("")) // Remove the empty entry at the start. Don't know why it shows up.
				.map(string -> {
					if (string.length() >= 2)
					{
						// New line at the start is from the delimiter, and new line at the end is from the last item.
						return string.substring(1, string.length() - 1); // Remove the new lines at the start and end.
					}
					else
					{
						return string;
					}
				});
	}

	private static String usageInformation()
	{
		return "USAGE:\n"
				+ "Arguments:\n"
				+ "imageDataFilename\n"
				+ "imageData may contain any number of pbm images concatenated";
	}
}
