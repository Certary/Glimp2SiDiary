package ch.pascal.glimp2sidiary;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

public class FileSaver {
	public static void saveFiles(List<String[]> glimpData, String outputDir, boolean groupByMonth, int splitAfterLinesCount) {
		FileWriter fileWriter = null;
		CSVPrinter csvFilePrinter = null;
		CSVFormat csvFormat = CSVFormat.DEFAULT;
		// Extract header and delete it from the list
		csvFormat.withHeader(glimpData.get(0));
		glimpData.remove(0);
		try {
			fileWriter = new FileWriter("test.csv");
	        csvFilePrinter = new CSVPrinter(fileWriter, CSVFormat.DEFAULT);
	        
	        csvFilePrinter.print(glimpData);
		} catch (IOException e) {
			System.err.println("Files can't be written to OUTPUT_DIR");
			System.exit(1);
		}
	}
}
