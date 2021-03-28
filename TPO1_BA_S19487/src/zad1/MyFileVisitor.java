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
import java.nio.file.attribute.BasicFileAttributes;

public class MyFileVisitor extends SimpleFileVisitor<Path> {
	
	private Charset inputCharset = Charset.forName("Cp1250");
	private Charset outputCharset = Charset.forName("UTF-8");
	private FileChannel outputChannel;
	

	public MyFileVisitor(String outputDirectory) throws IOException {
		RandomAccessFile out = new RandomAccessFile(outputDirectory,"rw");
		this.outputChannel = out.getChannel();
	}

	@Override
	public FileVisitResult visitFile(Path inputFile, BasicFileAttributes attrs) throws IOException {
		if(attrs.isRegularFile()&&(!inputFile.getFileName().toString().contains(".DS_Store"))) {
			System.out.println("Visits file " + inputFile);
		
			RandomAccessFile randomAccessFile = new RandomAccessFile(inputFile.toFile(),"r");
			FileChannel fc = randomAccessFile.getChannel();
			ByteBuffer buf = ByteBuffer.allocate((int)fc.size());
			fc.read(buf);
			fc.close();
			buf.flip();
			CharBuffer cbuf = inputCharset.decode(buf);
			ByteBuffer bbuf = outputCharset.encode(cbuf);
			System.out.println(bbuf);
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
