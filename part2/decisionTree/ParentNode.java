package part2.decisionTree;

import java.util.Optional;

import util.Pair;

public class ParentNode implements Node
{
	private final Attribute attribute;
	private final Node left;
	private final Node right;

	public ParentNode(Attribute attribute, Node left, Node right)
	{
		this.attribute = attribute;
		this.left = left;
		this.right = right;
	}

	public String toString()
	{
		return attribute.toString() + "\nLEFT\n" + left.toString() + "RIGHT\n" + right.toString();
	}

	public void report(String indent)
	{
		String nextIndent = indent + "    ";
		System.out.format("%s%s = True:\n", indent, attribute.toString());
		left.report(nextIndent);
		System.out.format("%s%s = False:\n", indent, attribute.toString());
		right.report(nextIndent);
	}

	@Override
	public Optional<Pair<Node, Node>> getChildren()
	{
		return Optional.of(new Pair<Node, Node>(left, right));
	}

	@Override
	public Optional<Classifier> getClassifier()
	{
		return Optional.empty();
	}

	@Override
	public Optional<Attribute> getAttribute()
	{
		return Optional.of(attribute);
	}
}
