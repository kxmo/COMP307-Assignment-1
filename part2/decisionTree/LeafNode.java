package part2.decisionTree;

import java.util.Optional;

import util.Pair;

public class LeafNode implements Node
{
	private final double probability;
	private final Classifier classifier;

	public LeafNode(Classifier classifier, double probability)
	{
		this.probability = probability;
		this.classifier = classifier;
	}

	public Optional<Classifier> getClassifier()
	{
		return Optional.of(classifier);
	}

	public double getProbability()
	{
		return probability;
	}

	public String toString()
	{
		return classifier.toString() + " (" + probability + ")\n";
	}

	public void report(String indent)
	{
		System.out.format("%sCategory %s, prob = %.0f%%\n", indent, classifier.toString(), (probability * 100));
	}

	@Override
	public Optional<Pair<Node, Node>> getChildren()
	{
		return Optional.empty();
	}

	@Override
	public Optional<Attribute> getAttribute()
	{
		return Optional.empty();
	}
}
