package ch.pascal.glimp2sidiary;

import java.util.List;

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
			int splitAfterLinesCount = 0;

			for (int i = 2; i < args.length; i++) {
				if (args[i].equals("-g")) {
					groupByMonth = true;
				} else if (args[i].equals("-s")) {
					// check if there is another args entry after this one
					if (args.length > i) {
						try {
							splitAfterLinesCount = Integer.parseInt(args[i + 1]);
						} catch (NumberFormatException e) {
							System.out.println("The argument '-s' must be followed by an integer argument");
						}
						i++;
					} else {
						System.out.println("The argument '-s' must be followed by an integer argument");
					}
				}
			}
			
			List<String[]> glimpData = Glimp2SiDiaryConverter.convert(sourceFile);
			
			FileSaver.saveFiles(glimpData, outputDir, groupByMonth, splitAfterLinesCount);
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
		sb.append("\t-s N\tSplit output after N lines into separate files")
				.append(System.getProperty("line.separator"));
		System.out.println(sb.toString());
	}
}
