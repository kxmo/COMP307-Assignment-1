package part2.decisionTree;

public class Classifier
{
	private final String classifier;
	
	public Classifier(String classifier)
	{
		this.classifier = classifier;
	}
	
	@Override
	public boolean equals(Object o)
	{
		boolean isEqual = false;
		
		if (o instanceof Classifier)
		{
			Classifier other = (Classifier) o;
			isEqual = classifier.equals(other.classifier);
		}
		
		return isEqual;
	}
	
	@Override
	public String toString()
	{
		return classifier;
	}
}
