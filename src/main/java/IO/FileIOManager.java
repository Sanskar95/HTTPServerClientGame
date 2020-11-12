package IO;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileIOManager {

	public static byte[] readFileBytes(String path) throws IOException {
		return Files.readAllBytes(Paths.get(path));
	}
}
