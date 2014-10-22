package net.sevenscales.editor.content;

import net.sevenscales.domain.api.IDiagramItem;
import net.sevenscales.domain.constants.Constants;

public class ClientIdHelpers {

	public interface UniqueChecker {
		boolean isUnique(String clientId);
	}

	public interface ClockValueFactory {
		String getSiteId();
		int newClockValue();
	}

	public static String generateClientId(int i, UniqueChecker uniqueChecker, ClockValueFactory clockValueFactory) {
		if (uniqueChecker != null) {
			String result = null;
			do {
				if (clockValueFactory.getSiteId() == null) {
					result = generateClientId(i);
				} else {
					result = clockValueFactory.getSiteId() + Constants.ID_SEPARATOR + clockValueFactory.newClockValue();
				}
			}	while (!uniqueChecker.isUnique(result));
			
			return result;
		} else {
			return generateClientId(i);
		}
	}

	private static String generateClientId(int i) {
		return ("F" + System.currentTimeMillis() + "" + i + 2012 + Math.abs(Math.random() * 16)).substring(0, 16);
	}
	
  public static void generateClientIdIfNotSet(IDiagramItem item, int i, UniqueChecker uniqueChecker, ClockValueFactory clockValueFactory) {
  	if ("".equals(item.getClientId()) || item.getClientId() == null) {
  		item.setClientId(ClientIdHelpers.generateClientId(i, uniqueChecker, clockValueFactory));
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
