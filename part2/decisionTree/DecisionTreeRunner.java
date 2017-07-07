package part2.decisionTree;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import util.Assert;
import util.CollectionUtil;
import util.FileLoader;
import util.Pair;

public class DecisionTreeRunner
{
	public static void parse(String[] args) // TODO Does not handle malformed input correctly. An empty list is passed to DecisionTree.buildTree.
	{
		Assert.isTrue(args != null && args.length == 2, usageInformation());

		String trainingFilename = args[0];
		String testFilename = args[1];
		final String delimiter = "\\s+";

		Optional<List<String>> trainingContents = CollectionUtil.streamToList(FileLoader.getContents(FileLoader.getFileReader(trainingFilename)));
		Assert.isTrue(trainingContents.isPresent() && !trainingContents.get().isEmpty(), String.format("Could not read training set from '%s'", trainingFilename));

		Optional<List<String>> testContents = CollectionUtil.streamToList(FileLoader.getContents(FileLoader.getFileReader(testFilename)));
		Assert.isTrue(testContents.isPresent() && !testContents.get().isEmpty(), String.format("Could not read test set from '%s'", testFilename));

		List<Attribute> attributes = getAttributes(trainingContents.get(), delimiter);

		Optional<Collection<Patient>> trainingSet = FileLoader.parseFile(trainingContents.get().stream(), createPatientRequirements(attributes, delimiter));
		Assert.isTrue(trainingSet.isPresent() && !trainingSet.get().isEmpty(), "Training set is malformed");

		Optional<Collection<Patient>> testSet = FileLoader.parseFile(testContents.get().stream(), createPatientRequirements(attributes, delimiter));
		Assert.isTrue(testSet.isPresent() && !trainingSet.get().isEmpty(), "Test set is malformed");

		Node root = DecisionTree.buildTree(trainingSet.get(), attributes);
		Pair<Classifier, Double> mostProbableClass = DecisionTree.mostProbableClass(testSet.get());

		double decisionTreeAccuracy = treeAccuracy(root, testSet.get());

		System.out.println("Accuracy:");
		System.out.format("Decision tree accuracy: %.0f%%\n", decisionTreeAccuracy * 100);
		System.out.format("Baseline accuracy (%s): %.0f%%\n\n", mostProbableClass.getLeft().toString(), mostProbableClass.getRight() * 100);

		root.report("");
	}

	private static double treeAccuracy(Node root, Collection<Patient> testSet)
	{
		return testSet.stream()
				.filter(patient -> DecisionTree.test(root, patient).equals(patient.getClassifier()))
				.count() / (double) testSet.size();
	}

	private static List<Attribute> getAttributes(List<String> values, String delimiter)
	{
		return Stream.of(values.get(1))
				.flatMap(s -> { return Stream.of(s.split(delimiter)); })
				.map(Attribute::new)
				.collect(Collectors.toList());
	}

	private static Function<String, Optional<Patient>> createPatientRequirements(List<Attribute> attributes, String delimiter)
	{
		return (String s) ->
		{
			Optional<Patient> patient = Optional.empty();

			if (s != null)
			{
				String[] data = s.split(delimiter);

				if (data.length == attributes.size() + 1) // +1 for the classification value.
				{
					Map<Attribute, Boolean> status = new HashMap<>();
					Classifier dataClassifier = new Classifier(data[0]);

					for (int i = 1; i < data.length; i++)
					{
						status.put(attributes.get(i - 1), Boolean.parseBoolean(data[i]));
					}

					patient = Optional.of(new Patient(status, dataClassifier));
				}
			}

			return patient;
		};
	}

	private static String usageInformation()
	{
		return "USAGE:\n"
				+ "Arguments:\n"
				+ "trainingDataFilename testDataFilename\n"
				+ "Both the training data and the test data must be formatted as follows:\n"
				+ "First line: class1 class2\n"
				+ "Second line: Attributes\n"
				+ "Remaining lines: Attribute number of boolean values";
	}
}