package part2.decisionTree;

import java.util.Map;

import util.Assert;

public class Patient
{
	private final Classifier classifier;
	private final Map<Attribute, Boolean> status;

	public Patient(Map<Attribute, Boolean> properties, Classifier classifier)
	{
		Assert.isTrue(properties != null, "properties may not be null");
		status = properties;
		this.classifier = classifier;
	}

	public Classifier getClassifier()
	{
		return classifier;
	}
	
	public Map<Attribute, Boolean> getAttributes()
	{
		return status;
	}
	
	public String toString()
	{
		return classifier.toString() + " " + status.toString();
	}
}
