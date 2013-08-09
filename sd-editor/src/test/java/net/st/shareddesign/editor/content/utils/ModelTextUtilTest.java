package net.st.shareddesign.editor.content.utils;

import net.sevenscales.editor.content.utils.ModelTextUtil;
import junit.framework.TestCase;

public class ModelTextUtilTest extends TestCase {
	public void test1() {
		ModelTextUtil mtu = new ModelTextUtil("[[wiki(pallo,keno)]]");
		String link = mtu.parseLink();
		assertEquals("pallo", link);
	}
}
