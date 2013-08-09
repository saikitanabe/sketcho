package net.sevenscales.editor.gfx.dojosvg;

import junit.framework.Assert;
import junit.framework.TestCase;
import net.sevenscales.editor.content.utils.DiagramHelpers;
import net.sevenscales.editor.uicomponents.uml.StorageElement;

public class DiagramHelpersTest extends TestCase {
	public void testApply() {
		String shape = "M379,187 Q379,216 390,233 409,239 428,233 436,212";
		String result = DiagramHelpers.applyTransformToShape(shape, 1, 2, null);
		
		String expected = "M380,189 Q380,218 391,235 410,241 429,235 437,214";

		System.out.println(result);
		Assert.assertEquals(expected, result);
	}
	
	public void testApply2() {
		String shape = "M700,150 Q722,153 741,152 748,152";
		String result = DiagramHelpers.applyTransformToShape(shape, -47, -7, null);
		
		String expected = "M653,143 Q675,146 694,145 701,145";

		System.out.println(result);
		Assert.assertEquals(expected, result);
	}
	
	public void testApplyWithTransformer() {
		String shape = "M899,237 0 a16,8 0 1,0 46,0";
		String result = DiagramHelpers.applyTransformToShape(shape, 6, 4, new StorageElement.HalfEllipseTransformer());

		String expected = "M905,241 0 a16,8 0 1,0 52,4";
		System.out.println(result);
		Assert.assertEquals(expected, result);
	}

}
