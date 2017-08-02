package ch.pascal.glimp2sidiary;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

public class FileSaver {
	public static List<String> saveFiles(String[] siDiaryHeader, Map<String, List<String[]>> organizedData,
			String outputDir) {
		List<String> savedFiles = new ArrayList<>();

		for (Map.Entry<String, List<String[]>> entry : organizedData.entrySet()) {
			savedFiles.add(saveFile(siDiaryHeader, entry.getValue(), outputDir, entry.getKey()));
		}

		return savedFiles;
	}

	private static String saveFile(String[] siDiaryHeader, List<String[]> glimpData, String outputDir,
			String fileName) {
		CSVFormat csvFormat = CSVFormat.DEFAULT.withDelimiter(';').withHeader(siDiaryHeader);

		// make sure that outputDir exists
		File outputDirFile = new File(outputDir);
		outputDirFile.mkdirs();

		StringBuilder filePathSb = new StringBuilder();
		filePathSb.append(outputDir);
		String fileSeparator = System.getProperty("file.separator");
		if (!outputDir.endsWith(fileSeparator)) {
			filePathSb.append(fileSeparator);
		}
		filePathSb.append(fileName.concat(".csv"));

		try (FileWriter fileWriter = new FileWriter(filePathSb.toString());
				CSVPrinter csvFilePrinter = new CSVPrinter(fileWriter, csvFormat)) {
			csvFilePrinter.printRecords(glimpData);
			csvFilePrinter.flush();
		} catch (IOException e) {
			System.err.println("File can't be written to OUTPUT_DIR");
			System.exit(1);
		}

		return filePathSb.toString();
	}
}
