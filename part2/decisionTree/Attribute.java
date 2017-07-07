package part2.decisionTree;

public class Attribute
{
	private final String value;

	public Attribute(String value)
	{
		this.value = value;
	}

	@Override
	public boolean equals(Object o)
	{
		boolean isEqual = false;
		
		if (o instanceof Attribute)
		{
			Attribute other = (Attribute) o;
			isEqual = value.equals(other.value);
		}
		
		return isEqual;
	}

	@Override
	public String toString()
	{
		return value.toString();
	}
}
