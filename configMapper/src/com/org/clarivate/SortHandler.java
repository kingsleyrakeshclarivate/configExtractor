package com.org.clarivate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SortHandler {

	public static List<String> splitAndSortHorizontalByPosition(List<String> toSplitList, int position) {
		List<String> horizontalSortedString = new ArrayList<String>();
		for(String toSplitString : toSplitList) {
			String[] rawArray = toSplitString.split(",");
			String[] toSortArray = Arrays.stream(rawArray[position].split("\\|\\|")).distinct().toArray(String[]::new);
			Arrays.sort(toSortArray);
			String sortedString = String.join("||", toSortArray);
			rawArray[position] = sortedString;
			horizontalSortedString.add(String.join(",", rawArray));
		}
		return horizontalSortedString;
	}

	public static List<String> getHorizontalSortedListByPosition(List<String> toSortList, int position) {
		List<String> print = splitAndSortHorizontalByPosition(toSortList, position);
		return print;
	}

}
