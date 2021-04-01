package zad1;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;

public class MyFileVisitor extends SimpleFileVisitor<Path> {

	private Charset inputCharset = Charset.forName("Cp1250");
	private Charset outputCharset = Charset.forName("UTF-8");
	private FileChannel outputChannel;

	public MyFileVisitor(Path outputDirectory) throws IOException {
		this.outputChannel = FileChannel.open(outputDirectory, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
	}

	@Override
	public FileVisitResult visitFile(Path inputFile, BasicFileAttributes attrs) throws IOException {
		if (attrs.isRegularFile() && (!inputFile.getFileName().toString().startsWith("."))) {

			FileChannel inputChannel = FileChannel.open(inputFile, StandardOpenOption.READ);

			ByteBuffer buffer = ByteBuffer.allocate(100);
			while (inputChannel.read(buffer) > 0) {
				buffer.flip();
				CharBuffer decoded = inputCharset.decode(buffer);
				ByteBuffer encoded = outputCharset.encode(decoded);
				buffer.flip();
				outputChannel.write(encoded);
			}

			inputChannel.close();

		}

		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {

		System.err.println("Could't read the file" + exc.getMessage());
		return FileVisitResult.CONTINUE;
	}

}
