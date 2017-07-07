package part3.perceptron;

import java.awt.Dimension;
import java.util.Arrays;

/**
 * A storage object for a black and white PBM.
 */
public class PBMImage
{
	private final String type;
	private final boolean[][] data;

	/**
	 * Create a new PBM image.
	 * @param type The case sensitive class of the image. TODO change type to own class
	 * @param data The image represented as an array.
	 */
	public PBMImage(String type, boolean[][] data)
	{
		this.type = type;
		this.data = data;
	}

	/**
	 * @return A non null case sensitive representation of the type of this image.
	 */
	public String getType()
	{
		return type;
	}

	/**
	 * Get the value present at position d in the image.
	 * @param d A non null, positive dimension within the bounds of the image.
	 * @return The value at position d of the image.
	 * @throws IndexOutOfBoundsException e Iff the value at d is not between 
	 * 0 and length - 1 for both width and height.
	 */
	public boolean getValue(Dimension d)
	{
		return data[d.height][d.width];
	}

	@Override
	public String toString()
	{
		return type.toString() + Arrays.deepToString(data);
	}
}
