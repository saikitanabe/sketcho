package net.sevenscales.domain.utils;

public class DiagramItemUtils {
	public static <T> boolean checkIfNotSame(T me, T other) {
		if ((me != null && other == null) || (me == null && other != null) || me != null && !me.equals(other)) {
			return true;
		}
		return false;
	}

}