package net.sevenscales.editor.content.utils;

import java.util.List;

public class IntegerHelpers {
	public static int[] toIntArray(List<Integer> list) {
		int[] result = new int[list.size()];
		int i = 0;
		for (Integer v : list) {
			result[i++] = v;
		}
		return result;
	}
}
