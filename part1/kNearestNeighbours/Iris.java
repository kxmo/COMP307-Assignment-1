package part1.kNearestNeighbours;

/**
 * A flower in the Iris dataset.
 */
public class Iris
{
	private final double sepalLength;
	private final double sepalWidth;
	private final double petalLength;
	private final double petalWidth;

	private final Species classification;

	/**
	 * All lengths and widths are in cm.
	 * @param sepalLength
	 * @param sepalWidth
	 * @param petalLength
	 * @param petalWidth
	 * @param classification The current classification of this flower. May not be null.
	 * If the classification is unknown, then predictors will ignore any provided classification.
	 */
	public Iris(double sepalLength, double sepalWidth, double petalLength, double petalWidth, Species classification)
	{
		if (classification == null)
		{
			throw new IllegalArgumentException("Species may not be null");
		}
		
		this.sepalLength = sepalLength;
		this.sepalWidth = sepalWidth;
		this.petalLength = petalLength;
		this.petalWidth = petalWidth;

		this.classification = classification;
	}

	/**
	 * @return the sepalLength
	 */
	public double getSepalLength()
	{
		return sepalLength;
	}

	/**
	 * @return the sepalWidth
	 */
	public double getSepalWidth()
	{
		return sepalWidth;
	}

	/**
	 * @return the petalLength
	 */
	public double getPetalLength()
	{
		return petalLength;
	}

	/**
	 * @return the petalWidth
	 */
	public double getPetalWidth()
	{
		return petalWidth;
	}

	/**
	 * @return the species
	 */
	public Species getSpecies()
	{
		return classification;
	}

	@Override
	public boolean equals(Object o)
	{
		boolean isEqual = false;

		if (o instanceof Iris)
		{
			Iris other = (Iris) o;
			isEqual = petalLength == other.petalLength
					&& petalWidth == other.petalWidth
					&& sepalLength == other.sepalLength
					&& sepalWidth == other.sepalWidth
					&& this.classification.equals(other.classification);
		}

		return isEqual;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((classification == null) ? 0 : classification.hashCode());
		result = (int) (prime * result + sepalLength);
		result = (int) (prime * result + sepalWidth);
		result = (int) (prime * result + petalLength);
		result = (int) (prime * result + petalWidth);
		return result;
	}

	@Override
	public String toString()
	{
		return sepalLength + "  "
				+ sepalWidth + "  "
				+ petalLength + "  "
				+ petalWidth + "  "
				+ "Iris-" + classification.toString();
	}
}
