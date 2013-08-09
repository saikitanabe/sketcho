package net.sevenscales.domain.utils;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;
import junit.framework.TestCase;

public class StringUtilTest extends TestCase {
	public void test1() {
		String template = "%text";
		Map<String, String> params = new HashMap<String, String>();
		params.put("%text", "");
		String result = StringUtil.parse(template, params);
		Assert.assertEquals("", result);
	}
	
	public void testTrimSpaces() {
		String v = "[{\"text\":\"Server\",\"elementType\":\"server\",\"shape\":\"416, 61,60,80\", \"backgroundColor\":\"204,255, 102,0.85:183,229,91,1\",\"textColor\":\"68,68,68,1\",\"version\":2, \"id\":1021157283, \"clientId\":\"F135894057551812\",\"cd\":\"\"}]";
		String expected = "[{\"text\":\"Server\",\"elementType\":\"server\",\"shape\":\"416, 61,60,80\",\"backgroundColor\":\"204,255, 102,0.85:183,229,91,1\",\"textColor\":\"68,68,68,1\",\"version\":2,\"id\":1021157283,\"clientId\":\"F135894057551812\",\"cd\":\"\"}]";

		String actual = StringUtil.trimSpaces(v);
		assertEquals(expected, actual);
	}
	
	public void testTrimSpaces2() {
		String v = "[{\"text\":\"Server pallo ja \\\"meri on täällä\\\" \",\"elementType\":\"server\",\"shape\":\"416, 61,60,80\", \"backgroundColor\":\"204,255, 102,0.85:183,229,91,1\",\"textColor\":\"68,68,68,1\",\"version\":2, \"id\":1021157283, \"clientId\":\"F135894057551812\",\"cd\":\"\"}]";
		String expected = "[{\"text\":\"Server pallo ja \\\"meri on täällä\\\" \",\"elementType\":\"server\",\"shape\":\"416, 61,60,80\",\"backgroundColor\":\"204,255, 102,0.85:183,229,91,1\",\"textColor\":\"68,68,68,1\",\"version\":2,\"id\":1021157283,\"clientId\":\"F135894057551812\",\"cd\":\"\"}]";

		String actual = StringUtil.trimSpaces(v);
		assertEquals(expected, actual);
	}

  public void testTrimBackSlash0() {
    String v = "\"text\":\"\\\\\", \"elementType\":\"server\"";
    String expected = "\"text\":\"\\\\\",\"elementType\":\"server\"";

    String actual = StringUtil.trimSpaces(v);
    assertEquals(expected, actual);
  }

	public void testTrimBackSlash() {
		String v = "[{\"text\":\"\\\\\", \"elementType\":\"server\", \"shape\":\"416, 61,60,80\", \"backgroundColor\":\"204,255, 102,0.85:183,229,91,1\", \"textColor\":\"68,68,68,1\", \"version\":2, \"id\":1021157283, \"clientId\":\"F135894057551812\",\"cd\":\"\"}]";
		String expected = "[{\"text\":\"\\\\\",\"elementType\":\"server\",\"shape\":\"416, 61,60,80\",\"backgroundColor\":\"204,255, 102,0.85:183,229,91,1\",\"textColor\":\"68,68,68,1\",\"version\":2,\"id\":1021157283,\"clientId\":\"F135894057551812\",\"cd\":\"\"}]";

		String actual = StringUtil.trimSpaces(v);
		assertEquals(expected, actual);
	}

}
