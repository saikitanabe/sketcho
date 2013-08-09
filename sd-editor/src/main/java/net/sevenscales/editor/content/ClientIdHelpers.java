package net.sevenscales.editor.content;

import net.sevenscales.domain.api.IDiagramItem;

public class ClientIdHelpers {
	public interface UniqueChecker {
		boolean isUnique(String clientId);
	}
	
	public static String generateClientId(int i, UniqueChecker uniqueChecker) {
		if (uniqueChecker != null) {
			String result = null;
			do {
				result = generateClientId(i);
			}	while (!uniqueChecker.isUnique(result));
			
			return result;
		} else {
			return generateClientId(i);
		}
	}

	private static String generateClientId(int i) {
		return ("F" + System.currentTimeMillis() + "" + i + 2012 + Math.abs(Math.random() * 16)).substring(0, 16);
	}
	
  public static void generateClientIdIfNotSet(IDiagramItem item, int i, UniqueChecker uniqueChecker) {
  	if ("".equals(item.getClientId()) || item.getClientId() == null) {
  		item.setClientId(ClientIdHelpers.generateClientId(i, uniqueChecker));
  	}
	}
	
	private static native String s4()/*-{
	  return Math.floor((1 + Math.random()) * 0x10000)
        .toString(16)
        .substring(1);
	}-*/;
	
	public static String guid() {
	  return s4() + s4() + '-' + s4() + '-' + s4() + '-' +
	         s4() + '-' + s4() + s4() + s4();
	}
}
