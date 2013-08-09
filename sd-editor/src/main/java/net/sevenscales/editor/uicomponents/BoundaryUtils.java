package net.sevenscales.editor.uicomponents;

public class BoundaryUtils {
	public static boolean equalVariance(int x, int px, int variance) {
		if (x >= (px - variance) && x <= (px + variance) ) {
			return true;
		}
		return false;
	}

	public static boolean equalOrBiggerWithVariance(int val, int pval, int v) {
		return (val >= pval && val <= (pval + v));
	}

	public static boolean equalOrSmallerWithVariance(int val, int pval, int v) {
		return (val >= (pval - v) && val <= pval);
	}
}
