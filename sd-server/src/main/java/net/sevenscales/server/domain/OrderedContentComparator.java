package net.sevenscales.server.domain;

import java.util.Comparator;

public class OrderedContentComparator implements Comparator<PageOrderedContent> {

	public int compare(PageOrderedContent arg0, PageOrderedContent arg1) {
    return arg0.compareTo(arg1);
	}

}
