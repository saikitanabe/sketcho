package net.sevenscales.domain.utils;

import java.util.Comparator;

import net.sevenscales.domain.IDiagramItemRO;

public class DiagramItemIdComparator implements Comparator<IDiagramItemRO> {
	@Override
	public int compare(IDiagramItemRO diagram, IDiagramItemRO theother) {
		return compareClientId(diagram, theother);
	}
	
	public static int compareClientId(IDiagramItemRO item, IDiagramItemRO theother) {
		String clientId = item.getClientId();
		String theOtherClientId = theother.getClientId();
		int result = clientId.compareTo(theOtherClientId);
		return result;
	}
	
}
