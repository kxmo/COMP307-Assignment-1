import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import part1.kNearestNeighbours.KNearestNeighboursRunner;
import part2.decisionTree.DecisionTreeRunner;
import part3.perceptron.PerceptronRunner;

public class Main
{
	public static void main(String[] args)
	{
		try
		{
			int selectedOption = Integer.parseInt(args[0]);
			Map<Integer, Consumer<String[]>> options = new HashMap<>();
			String[] consumerOptions = Arrays.copyOfRange(args, 1, args.length);

			options.put(1, KNearestNeighboursRunner::parse);
			options.put(2, DecisionTreeRunner::parse);
			options.put(3,  PerceptronRunner::parse);

			options.get(selectedOption).accept(consumerOptions);
		}
		catch (ArrayIndexOutOfBoundsException | NumberFormatException e)
		{
			System.out.println(usageInformation());
			System.exit(-1);
		}
		catch (NullPointerException e)
		{
			System.out.format("Option '%s' does not exist\n", args[0]);
		}
	}

	private static String usageInformation()
	{
		return "Usage: partNumber [part arguments]\n"
				+ "For example, to run the KNearestNeighbours implementation with k = 3:\n"
				+ "java -jar [jarname].jar 1 /path/to/trainingSet /path/to/testSet 3\n"
				+ "Where [jarname] is the name of the jar";

	}
}
