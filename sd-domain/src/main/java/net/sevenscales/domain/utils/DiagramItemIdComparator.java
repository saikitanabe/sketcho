package net.sevenscales.domain.utils;

import java.util.Comparator;

import net.sevenscales.domain.IDiagramItemRO;

public class DiagramItemIdComparator implements Comparator<IDiagramItemRO> {
	public int compare(IDiagramItemRO diagram, IDiagramItemRO theother) {
		return compareClientId(diagram, theother);
	}
	
	public static int compareClientId(IDiagramItemRO item, IDiagramItemRO theother) {
		String clientId = item.getClientId();
		String theOtherClientId = theother.getClientId();

		// ST 28.10.2018: Diagram can be CircleElement that is not a shape and
		// doesn't contain client id
		if (clientId == null) {
			return 1;
		} else if (theOtherClientId == null) {
			return -1;
		}

		int result = clientId.compareTo(theOtherClientId);
		return result;
	}
	
}
