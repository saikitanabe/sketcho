package net.sevenscales.editor.content.utils;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import net.sevenscales.editor.diagram.ContainerType;
import net.sevenscales.editor.diagram.Diagram;

public class SortHelpers {
	public static Diagram[] sortDiagramItems(List<Diagram> diagrams) {
    Diagram[] items = new Diagram[diagrams.size()];
    diagrams.toArray(items);

		Arrays.sort(items, new Comparator<Diagram>() {
			@Override
			public int compare(Diagram arg0, Diagram arg1) {
				if (arg0 instanceof ContainerType) {
					return -2;
		    }
//				else if (arg0.getType().equals("relationship")) {
//					return -1;
//		    }
		
				if (arg1 instanceof ContainerType) {
					return 2;
		    } 
//				else if (arg1.getType().equals("relationship")) {
//					return 1;
//		    }
				return 0;
			}
		});
		return items;
	}

}
