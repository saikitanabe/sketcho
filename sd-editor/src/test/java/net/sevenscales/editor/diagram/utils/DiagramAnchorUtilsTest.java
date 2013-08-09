package net.sevenscales.editor.diagram.utils;

import net.sevenscales.editor.diagram.utils.DiagramAnchorUtils.RelDirection;
import junit.framework.Assert;
import junit.framework.TestCase;

public class DiagramAnchorUtilsTest extends TestCase {

	public void test() {
		Assert.assertEquals(RelDirection.TOP_TO_DOWN, DiagramAnchorUtils.dir(0, 0, 3, 5));
		Assert.assertEquals(RelDirection.RIGHT_TO_LEFT, DiagramAnchorUtils.dir(10, 10, 3, 5));
		Assert.assertEquals(RelDirection.DOWN_TO_TOP, DiagramAnchorUtils.dir(10, 25, 3, 10));
		Assert.assertEquals(RelDirection.LEFT_TO_RIGHT, DiagramAnchorUtils.dir(10, 25, 30, 10));
		
		Assert.assertEquals(RelDirection.TOP_TO_DOWN, DiagramAnchorUtils.dir(10, 10, 10, 20));

	}
}
