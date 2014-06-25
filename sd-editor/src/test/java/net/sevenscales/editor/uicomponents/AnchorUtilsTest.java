package net.sevenscales.editor.uicomponents;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;
import net.sevenscales.editor.uicomponents.AnchorUtils.AnchorProperties;

public class AnchorUtilsTest extends TestCase {

	public void test1() {
		AnchorProperties tempAnchorProperties = new AnchorProperties();
		List<Integer> points = new ArrayList<Integer>();
		points.add(0); 		points.add(0);
		points.add(20); 		points.add(25);
		points.add(30); 		points.add(35);
		points.add(40); 		points.add(45);

		AnchorUtils.anchorPoint(10, 15, 0, 0, 100, 30, tempAnchorProperties, points);
		Assert.assertEquals(20, tempAnchorProperties.x);
		Assert.assertEquals(25, tempAnchorProperties.y);
	}
	
}
