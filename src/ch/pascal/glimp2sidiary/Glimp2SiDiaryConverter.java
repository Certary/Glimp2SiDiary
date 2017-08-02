package ch.pascal.glimp2sidiary;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.input.BOMInputStream;

public class Glimp2SiDiaryConverter {
	/**
	 * Parses a Glimp CSV file and converts it to the SiDiary CSV format.
	 * 
	 * @param sourceFile
	 * @return
	 */
	public static List<String[]> convert(String sourceFile) {
		String[] siDiaryHeader = new String[] { "DAY", "TIME", "KETONE", "BG_LEVEL", "BG_LEVEL_MMOL", "CH_BE_KHE",
				"CH_GR", "BOLUS", "BASAL", "BLOODPRESSURE", "REMARK", "WEIGHT_LBS", "WEIGHT_KG" };
		SimpleDateFormat glimpDateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH.mm.ss");
		SimpleDateFormat siDiaryDayFormat = new SimpleDateFormat("dd.MM.yyyy");
		SimpleDateFormat siDiaryTimeFormat = new SimpleDateFormat("HH:mm:ss");

		// Parse Glimp data and convert it to SiDiary format
		List<String[]> siDiaryData = new ArrayList<>();
		siDiaryData.add(siDiaryHeader);
		URL url;
		try {
			url = new File(sourceFile).toURI().toURL();

			try (Reader reader = new InputStreamReader(new BOMInputStream(url.openStream()), StandardCharsets.UTF_16LE);
					CSVParser parser = new CSVParser(reader, CSVFormat.DEFAULT.withDelimiter(';'))) {
				String[] siDiaryRecord;
				for (final CSVRecord glimpRecord : parser) {
					if (glimpRecord.size() != 13)
						continue;

					siDiaryRecord = new String[siDiaryHeader.length];
					// DAY
					siDiaryRecord[0] = siDiaryDayFormat.format(glimpDateTimeFormat.parse(glimpRecord.get(1)));
					// TIME
					siDiaryRecord[1] = siDiaryTimeFormat.format(glimpDateTimeFormat.parse(glimpRecord.get(1)));
					// BG_LEVEL
					siDiaryRecord[3] = glimpRecord.get(4);
					// BOLUS
					siDiaryRecord[7] = glimpRecord.get(8);
					// REMARK
					if (!glimpRecord.get(9).isEmpty() && !glimpRecord.get(11).isEmpty())
						siDiaryRecord[10] = glimpRecord.get(9) + " - " + glimpRecord.get(11);

					siDiaryData.add(siDiaryRecord);
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				System.exit(-1);
			} catch (IOException e) {
				System.err.println("Cannot open the SOURCE_FILE");
				System.exit(1);
			} catch (ParseException e) {
				System.err.println("The SOURCE_FILE contains invalid dates");
				System.exit(1);
			}
		} catch (MalformedURLException e) {
			System.err.println("The SOURCE_FILE is not a valid file location");
			System.exit(1);
		}

		return siDiaryData;
	}
}
