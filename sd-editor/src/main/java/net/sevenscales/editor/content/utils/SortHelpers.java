package net.sevenscales.editor.content.utils;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

import net.sevenscales.editor.diagram.ContainerType;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.domain.utils.SLogger;

public class SortHelpers {
	private static final SLogger logger = SLogger.createLogger(SortHelpers.class);
	static {
		logger.addFilter(SortHelpers.class);
	}

	public static Diagram[] toArray(List<Diagram> diagrams, List<String> filter) {
    // Diagram[] items = new Diagram[diagrams.size()];
    // diagrams.toArray(items);
    List<Diagram> result = new ArrayList<Diagram>();
    for (Diagram d : diagrams) {
    	if (contains(filter, d)) {
	    	result.add(d);
    	}
    }
    Diagram[] items = new Diagram[result.size()];
    result.toArray(items);
    Arrays.sort(items, DiagramDisplaySorter.createDiagramComparator());
    return items;
	}

	private static boolean contains(List<String> filter, Diagram d) {
		for (String f : filter) {
			if (d != null && d.getDiagramItem() != null && f.equals(d.getDiagramItem().getClientId())) {
				return true;
			}
		}
		return false;
	}

	public static Diagram[] toArray(List<Diagram> diagrams) {
    Diagram[] items = new Diagram[diagrams.size()];
    diagrams.toArray(items);
    Arrays.sort(items, DiagramDisplaySorter.createDiagramComparator());
    return items;
	}

	public static Diagram[] toArray(Set<Diagram> diagrams) {
    Diagram[] items = new Diagram[diagrams.size()];
    diagrams.toArray(items);
    Arrays.sort(items, DiagramDisplaySorter.createDiagramComparator());
    return items;
	}
}
