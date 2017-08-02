package ch.pascal.glimp2sidiary;

import java.util.List;
import java.util.Map;

public class Main {
	public static void main(String[] args) {
		if (args.length == 1 && args[0].equals("--help")) {
			printHelp();
		} else if (args.length < 2 || args.length > 5) {
			printHelp();
		} else {
			String sourceFile = args[0];
			String outputDir = args[1];
			boolean groupByMonth = false;
			// 0 means no splitting
			int splitNumber = 0;

			for (int i = 2; i < args.length; i++) {
				if (args[i].equals("-g")) {
					groupByMonth = true;
				} else if (args[i].equals("-s")) {
					// check if there is another args entry after this one
					if (args.length > i) {
						try {
							splitNumber = Integer.parseInt(args[i + 1]);
						} catch (NumberFormatException e) {
							System.err.println("The argument '-s' must be followed by an integer argument");
						}
						i++;
					} else {
						System.err.println("The argument '-s' must be followed by an integer argument");
					}
				}
			}

			System.out.print("Parsing and converting data... ");
			List<String[]> siDiaryData = Glimp2SiDiaryConverter.convert(sourceFile);
			System.out.println("Done");

			// Extract header and delete it from glimpData
			String[] siDiaryHeader = siDiaryData.get(0);
			siDiaryData.remove(0);

			System.out.print("Organizing data... ");
			Map<String, List<String[]>> organizedData = DataOrganizer.organizeData(siDiaryData, groupByMonth,
					splitNumber);
			siDiaryData = null;
			System.out.println("Done");

			System.out.print("Saving data to OUTPUT_DIR... ");
			List<String> savedFiles = FileSaver.saveFiles(siDiaryHeader, organizedData, outputDir);
			System.out.println("Done\n");

			System.out.println("The data was saved to the following files:");
			for (String filePath : savedFiles) {
				System.out.println(filePath);
			}
		}
	}

	private static void printHelp() {
		StringBuilder sb = new StringBuilder();
		sb.append("Usage: glimp2sidiary SOURCE_FILE OUTPUT_DIR [-g] [-s N]")
				.append(System.getProperty("line.separator"));
		sb.append(System.getProperty("line.separator"));
		sb.append("Convert a Glimp CSV file (GlicemiaMisurazioni.csv) to the SiDiary CSV format")
				.append(System.getProperty("line.separator"));
		sb.append(System.getProperty("line.separator"));
		sb.append("\t-g\tGroup ouput by month into separate files").append(System.getProperty("line.separator"));
		sb.append("\t-s N\tSplit data into separate files after N records")
				.append(System.getProperty("line.separator"));
		System.out.println(sb.toString());
	}
}
