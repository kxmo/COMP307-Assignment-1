package util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileLoader
{

	public static <T> Optional<Collection<T>> parseFile(Stream<String> contents, Function<String, Optional<T>> parseSingleItem)
	{
			return Optional.of(
					contents
					.map(s -> { return parseSingleItem.apply(s); })
					.filter(Optional::isPresent)
					.map(Optional::get)
					.collect(Collectors.toCollection(() -> { return new ArrayList<>(); })));
	}
	/**
	 * Get the contents of a file.
	 * @param reader A reader of a suitable file. getFileReader(path) can be used.
	 * @return Optional.of(reader lines) iff reader is non null and present.
	 * Optional.empty() otherwise.
	 */
	public static Optional<Stream<String>> getContents(Optional<BufferedReader> reader)
	{
		if (reader == null || !reader.isPresent())
		{
			return Optional.empty();
		}

		return Optional.of(reader.get().lines());
	}

	/**
	 * Get a reader of a file from a path.
	 * @param path The path to generate the reader from.
	 * @return Optional.of(contents of file in path) iff the file at path exists and
	 * is readable. Optional.empty() otherwise.
	 */
	public static Optional<BufferedReader> getFileReader(String path)
	{
		try
		{
			FileReader file = new FileReader(path);
			BufferedReader reader = new BufferedReader(file);
			return Optional.of(reader);
		}
		catch (IOException e)
		{
			return Optional.empty();
		}
	}
}
