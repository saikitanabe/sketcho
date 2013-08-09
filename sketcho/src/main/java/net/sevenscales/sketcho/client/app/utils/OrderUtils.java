package net.sevenscales.sketcho.client.app.utils;

import java.util.Iterator;

import net.sevenscales.domain.api.IPage;
import net.sevenscales.domain.api.IPageOrderedContent;

public class OrderUtils {
	
	public static void order(IPageOrderedContent orderedContent, int prevOrder) {
		IPage p = orderedContent.getPage();
		int dir = 0;
		int smallIndex = 0;
		int bigIndex = 0;
		if ((orderedContent.getOrderValue() == null || prevOrder == 0)) {
			return;
		}
		if ( orderedContent.getOrderValue().intValue() > prevOrder) {
			// new is bigger
			// loop from old to new
			// --other order value
			dir = -1;
			smallIndex = prevOrder;
			bigIndex = orderedContent.getOrderValue().intValue();
		}		
		if (orderedContent.getOrderValue().intValue() < prevOrder) {
			// new is smaller
			// loop from new to old
			// ++ other order value
			dir = 1;
			smallIndex = orderedContent.getOrderValue().intValue();
			bigIndex = prevOrder;
		}
		
		for (Iterator i = p.getContentItems().iterator(); i.hasNext();) {
			IPageOrderedContent c = (IPageOrderedContent) i.next();
			int order = c.getOrderValue().intValue();
			if (!c.getId().equals(orderedContent.getId()) &&
					order >= smallIndex &&
					order <= bigIndex) {
				c.setOrderValue(new Integer(c.getOrderValue().intValue() + dir));
			}
		}
	}
}
