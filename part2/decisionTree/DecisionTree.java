package part2.decisionTree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import util.CollectionUtil;
import util.Pair;
import util.MathUtil;

public class DecisionTree
{
	public static Pair<Classifier, Double> mostProbableClass(Collection<Patient> instances)
	{
		return new Pair<>(baselinePredictor(instances).getClassifier().get(), baselinePredictor(instances).getProbability());
	}

	/**
	 * Build a decision tree from a training set.
	 * The Patient must have values for all attributes assigned to them.
	 * No parameters may be null or empty.
	 * @param instances The training set from which to build the tree.
	 * @param attributes The attributes to split the tree against.
	 * @return The root node of the constructed tree.
	 */
	public static <T,V> Node buildTree(Collection<Patient> instances, List<Attribute> attributes)
	{
		Pair<Classifier, Double> mostProbableClass = mostProbableClass(instances);
		return buildTree(instances, attributes, mostProbableClass, instances.iterator().next().getClassifier());
	}

	/**
	 * Find the classification of an instance for a particular tree.
	 * No parameters may be null.
	 * @param root The root node of the tree.
	 * @param testInstance The instance to classify.
	 * @return The predicted classification of the test instance.
	 */
	public static Classifier test(Node root, Patient testInstance)
	{
		// This method foregos error checking on the assumption that all Nodes adhere to their contract as specified in the interface.
		Optional<Pair<Node, Node>> children = root.getChildren();

		if (children.isPresent())
		{
			boolean attributeTrue = testInstance.getAttributes().get(root.getAttribute().get()); // Guaranteed to have attributes, because we have children.
			Node left = children.get().getLeft();
			Node right = children.get().getRight();

			return test(attributeTrue ? left : right, testInstance);
		}
		else
		{
			return root.getClassifier().get(); // Guaranteed to be a leaf node, because there are no children.
		}
	}

	private static <V> Node buildTree(Collection<Patient> instances, List<Attribute> attributes, Pair<Classifier, Double> mostProbableClass, Classifier classifier)
	{
		if (instances.isEmpty())
		{
			return new LeafNode(mostProbableClass.getLeft(), mostProbableClass.getRight());
		}
		else if (isPure(instances, classifier))
		{
			return new LeafNode(instances.stream().findAny().get().getClassifier(), 1);
		}
		else if (attributes.isEmpty())
		{
			return baselinePredictor(instances);
		}
		else // Find best attribute
		{
			double bestNodePurity = Double.POSITIVE_INFINITY;
			Attribute bestAttribute = null;
			Collection<Patient> bestInstanceTrue = null;
			Collection<Patient> bestInstanceFalse = null;

			for (Attribute attribute : attributes)
			{
				Map<Boolean, List<Patient>> attributePartition = CollectionUtil.partitionBy(instances, a -> a.getAttributes().get(attribute).equals(true));

				double currentNodePurity = weightedImpurity(attributePartition.get(true), attributePartition.get(false), classifier);

				if (currentNodePurity < bestNodePurity)
				{
					bestNodePurity = currentNodePurity;
					bestAttribute = attribute;
					bestInstanceTrue = attributePartition.get(true);
					bestInstanceFalse = attributePartition.get(false);
				}
			}

			List<Attribute> attrWithoutBest = new ArrayList<>(attributes);
			attrWithoutBest.remove(bestAttribute);

			Node left = buildTree(bestInstanceTrue, attrWithoutBest, mostProbableClass, classifier);
			Node right = buildTree(bestInstanceFalse, attrWithoutBest, mostProbableClass, classifier);

			return new ParentNode(bestAttribute, left, right);
		}
	}

	/**
	 * A node containing the name and probability of the majority class
	 * of the instances (chosen randomly if classes are equal).
	 * @param instances
	 * @return
	 */
	private static LeafNode baselinePredictor(Collection<Patient> instances)
	{
		List<Classifier> c = instances.stream().map(m -> m.getClassifier()).collect(Collectors.toList());
		Classifier mostProbableClass = CollectionUtil.mostCommon(c);

		double mostProbableClassCount = instances.stream()
				.filter(p -> {return p.getClassifier().equals(mostProbableClass);})
				.count();
		return new LeafNode(mostProbableClass, mostProbableClassCount / (double) instances.size());
	}

	private static boolean isPure(Collection<Patient> instances, Classifier classifier)
	{
		return impurity(instances, classifier) == 0;
	}

	private static <T> double impurity(Collection<Patient> instances, Classifier classifier)
	{
		if (instances.isEmpty())
		{
			return 0;
		}

		// Split into true and false for the classifier.
		Map<Boolean, List<Patient>> partition = CollectionUtil.partitionBy(instances, patient -> patient.getClassifier().equals(classifier));
		int n = partition.get(true).size();
		int m = partition.get(false).size();
		double result = (double) (m * n) / MathUtil.square(m + n);

		assert n + m == instances.size() : "Missing instances from partitioned set";
		assert result >= 0 && result <= 0.25 : String.format("Result is not valid (%s)", result);

		return result;
	}

	/**
	 * Weighted purity is the sum of Probability of getting to this node, multiplied by the purity of this node.
	 * @param trueInstances The instances for which some attribute is true.
	 * @param falseInstances The instances for which some attribute is false.
	 * @param classifier The outcome of this instance.
	 * @return The impurity of the items in this node.
	 */
	private static double weightedImpurity(Collection<Patient> trueInstances, Collection<Patient> falseInstances, Classifier classifier)
	{
		double instancesSize = trueInstances.size() + falseInstances.size();

		double probabilityLeft = trueInstances.size() / instancesSize;
		double purityLeft = impurity(trueInstances, classifier);

		double probabilityRight = falseInstances.size() / instancesSize;
		double purityRight = impurity(falseInstances, classifier);

		return (probabilityLeft * purityLeft) + (probabilityRight * purityRight);
	}
}
