package ch.pascal.glimp2sidiary;

import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

public class FileSaver {
	private static final Calendar CALENDAR = Calendar.getInstance();
	private static final SimpleDateFormat SI_DIARY_DAY_FORMAT = new SimpleDateFormat("dd.MM.yyyy");
	private static final String FILE_NAME_BASE = "siDiary";
	
	public static void saveFiles(List<String[]> glimpData, String outputDir, boolean groupByMonth, int splitAfterLinesCount) {
		Map<String, List<String[]>> separatedFileData = new HashMap<>();
		if(groupByMonth) {
			for(String[] glimpRecord: glimpData) {
				Date date;
				try {
					date = SI_DIARY_DAY_FORMAT.parse(glimpRecord[0]);
					String fileName = generateFileName(date);
					
					List<String[]> fileData;
					if((fileData = separatedFileData.get(fileName)) != null) {
						fileData.add(glimpRecord);
					} else {
						fileData = new ArrayList<>();
						fileData.add(glimpRecord);
						separatedFileData.put(fileName, fileData);
					}
				} catch (ParseException e) {
					// This should never happen as the dates have already been parsed during the conversion
					e.printStackTrace();
					System.exit(-1);
				}
			}
		}
		if(separatedFileData.isEmpty()) {
			separatedFileData.put(FILE_NAME_BASE, glimpData);
		}
		if(splitAfterLinesCount > 0 && glimpData.size() > splitAfterLinesCount) {
			for (Map.Entry<String, List<String[]>> entry : separatedFileData.entrySet()) {
			     if(entry.getValue().size() > splitAfterLinesCount) {
			    	 String fileNameBase = entry.getKey();
			     }
			}
		}
	}
	
	private static String generateFileName(Date date) {
		CALENDAR.setTime(date);
		return FILE_NAME_BASE + CALENDAR.get(Calendar.YEAR) + "-" + CALENDAR.get(Calendar.MONTH) + 1;
	}
	
	private static void saveFile(List<String[]> glimpData, String outputDir, String fileName) {
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
