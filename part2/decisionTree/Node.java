package part2.decisionTree;

import java.util.Optional;

import util.Pair;

public interface Node
{
	/**
	 * Print out this node, and all sub nodes, in a human readable format.
	 * @param indent The current level of indent.
	 */
	public void report (String indent);

	/**
	 * Get the children of a node.
	 * If children are present then getAttribute returns a non null, non empty Attribute.
	 * @return The two children of this node, if any. Pair.getLeft() is the left node and Pair.getRight is the right node.
	 */
	public Optional<Pair<Node, Node>> getChildren();

	/**
	 * The result of a traversal of the tree.
	 * If children or attributes are present then classifier is not.
	 * @return Optional.of(classification) if this node is a leaf node. Optional.empty() otherwise.
	 */
	public Optional<Classifier> getClassifier();

	/**
	 * The attribute that this node is split on.
	 * @return Optional.of(attribute) if this node is not a leaf node. Optional.empty() otherwise.
	 * If attribute is present then getChildren returns a non null, non empty pair of children.
	 */
	public Optional<Attribute> getAttribute();
}
