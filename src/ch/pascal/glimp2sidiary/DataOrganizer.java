package ch.pascal.glimp2sidiary;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class DataOrganizer {
	private static final String NAME_BASE = "siDiary";
	private static final String NAME_SEPARATOR = "_";
	private static final String NAME_DATE_FORMAT = "%d-%02d";
	private static final String NAME_PART_FORMAT = "Part" + NAME_SEPARATOR + "%d";

	public static Map<String, List<String[]>> organizeData(List<String[]> siDiaryData, boolean groupByMonth,
			int splitNumber) {
		Map<String, List<String[]>> organizedData = new HashMap<>();
		organizedData.put(NAME_BASE, siDiaryData);

		if (groupByMonth) {
			organizedData = groupDataByMonth(organizedData);
		}
		if (splitNumber > 0) {
			organizedData = splitByNumberOfRecords(organizedData, splitNumber);
		}

		return organizedData;
	}

	private static Map<String, List<String[]>> groupDataByMonth(Map<String, List<String[]>> ungroupedData) {
		Map<String, List<String[]>> groupedData = new HashMap<>();
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat SI_DIARY_DAY_FORMAT = new SimpleDateFormat("dd.MM.yyyy");

		for (Entry<String, List<String[]>> entry : ungroupedData.entrySet()) {
			if (entry.getValue().isEmpty()) {
				groupedData.put(entry.getKey(), entry.getValue());
				continue;
			}

			for (String[] glimpRecord : entry.getValue()) {
				Date date;
				try {
					date = SI_DIARY_DAY_FORMAT.parse(glimpRecord[0]);
					calendar.setTime(date);
					// name format: NAME_BASE + NAME_SEPARATOR + yyyy-MM
					String name = NAME_BASE + NAME_SEPARATOR + String.format(NAME_DATE_FORMAT,
							calendar.get(Calendar.YEAR), (calendar.get(Calendar.MONTH) + 1));

					List<String[]> groupedDataEntry;
					if ((groupedDataEntry = groupedData.get(name)) != null) {
						groupedDataEntry.add(glimpRecord);
					} else {
						groupedDataEntry = new ArrayList<>();
						groupedDataEntry.add(glimpRecord);
						groupedData.put(name, groupedDataEntry);
					}
				} catch (ParseException e) {
					// This should never happen as the dates have already been parsed during the
					// conversion
					e.printStackTrace();
					System.exit(-1);
				}
			}
		}

		return groupedData;
	}

	private static Map<String, List<String[]>> splitByNumberOfRecords(Map<String, List<String[]>> unsplitData,
			int splitAfterLinesCount) {
		if (splitAfterLinesCount <= 0) {
			return unsplitData;
		}

		Map<String, List<String[]>> splitData = new HashMap<>();

		for (Map.Entry<String, List<String[]>> entry : unsplitData.entrySet()) {
			if (entry.getValue().size() > splitAfterLinesCount) {
				int numberOfParts = entry.getValue().size() / splitAfterLinesCount;
				if (entry.getValue().size() % splitAfterLinesCount != 0)
					numberOfParts++;
				int toIndex;
				for (int i = 0; i < numberOfParts; i++) {
					toIndex = (i + 1) * splitAfterLinesCount;
					// Since sublist() excludes the toIndex from the list we don't have to subtract
					// 1 from size()
					if (toIndex > entry.getValue().size())
						toIndex = entry.getValue().size();
					List<String[]> fileData = new ArrayList<>(
							entry.getValue().subList(i * splitAfterLinesCount, toIndex));
					String name = entry.getKey() + NAME_SEPARATOR + String.format(NAME_PART_FORMAT, (i + 1));
					splitData.put(name, fileData);
				}
			} else {
				splitData.put(entry.getKey(), entry.getValue());
			}
		}

		return splitData;
	}
}
