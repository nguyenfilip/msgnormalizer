package org.candlepin;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Hello world!
 *
 */
public class App {
	private static List<LineModifier> lineModifiers = new ArrayList<>();

	public static void main(String[] args) throws IOException {

		lineModifiers.add(new CaseNormalizer());
		lineModifiers.add(new TicksNormalizer());
		lineModifiers.add(new NoQuotesNormalizer());
		// /home/fnguyen/candlepin/candlepin/common/po
		if (args.length < 1)
			throw new IllegalArgumentException("Please provide path to directory with translation files");

		Path path = FileSystems.getDefault().getPath(args[0]);

		if (!path.toFile().exists())
			throw new IllegalArgumentException(String.format("Path %s doesn't exist", path.toString()));

		Files.walk(path).map(p -> p.toFile()).filter(f -> f.isFile())
				.filter(f -> f.getName().endsWith(".pot") || f.getName().endsWith(".po")).map(p -> p.toPath())
				.parallel()// TODO test perf
				.forEach(App::runNormalizer);
	}

	/**
	 * This method will be run in parallel (with different f parameters). Do not
	 * reuse any global state to avoid race condition.
	 * 
	 * @throws IOException
	 */
	private static void runNormalizer(Path p) {
		System.out.println("Normalizing" + p);
		Path newFile = p.getParent().resolve(p.getFileName()+".new");
		if (newFile.toFile().exists())
			newFile.toFile().delete();
		
		try (BufferedWriter writer = Files.newBufferedWriter(newFile)) {
			try (BufferedReader reader = Files.newBufferedReader(p)) {
				String line = null;
				while ((line = reader.readLine()) != null) {
					if (!line.startsWith("msgid")) {
						writer.write(line);
						continue;
					}
//					String oldString = line;
					String newString = line;
					for (LineModifier modifier : lineModifiers) {
						newString = modifier.modify(newString);
					}
					writer.write(newString);
				}
			}

		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
