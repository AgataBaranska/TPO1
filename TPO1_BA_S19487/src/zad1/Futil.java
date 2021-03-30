package zad1;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Futil {

	public static void processDir(String dirName, String resultFileName) {

		try {
			MyFileVisitor visitor = new MyFileVisitor(Paths.get(resultFileName));
			Files.walkFileTree(Paths.get(dirName), visitor);
		} catch (IOException e) {
			System.out.println(e);
		}

	}

}
