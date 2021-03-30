package zad1;

import java.io.IOException;
import java.io.RandomAccessFile;
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
		if (attrs.isRegularFile() && (!inputFile.getFileName().toString().contains(".DS_Store"))) {
			FileChannel fc = FileChannel.open(inputFile, StandardOpenOption.READ);
			ByteBuffer buf = ByteBuffer.allocate((int) fc.size());
			fc.read(buf);
			fc.close();
			buf.flip();
			CharBuffer cbuf = inputCharset.decode(buf);
			ByteBuffer bbuf = outputCharset.encode(cbuf);
			outputChannel.write(bbuf);

		}

		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {

		System.err.println("Could't read the file" + exc.getMessage());
		return FileVisitResult.CONTINUE;
	}

}
