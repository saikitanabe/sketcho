package net.st.shareddesign.editor.content.utils;

import java.io.UnsupportedEncodingException;
import java.util.zip.CRC32;

import net.sevenscales.editor.content.utils.ChecksumHelpers;
import junit.framework.TestCase;

public class ChecksumHelpersTest extends TestCase {
	public void testSimple() {
		long start = System.currentTimeMillis();
		int c = ChecksumHelpers.checksum("[{\"text\":\"Use Case\",\"elementType\":\"ellipseitem\",\"shape\":\"365,99,63,19\",\"backgroundColor\":\"204,204,255,0:51,51,51,1\",\"textColor\":\"68,68,68,1\",\"version\":1, \"id\":764611191, \"clientId\":\"F135871246864212\",\"cd\":\"\"}]");
		System.out.println("time: " + (System.currentTimeMillis() - start));
		System.out.println(c);
	}
	
	public void testcrc32kala() throws UnsupportedEncodingException {
		long start = System.currentTimeMillis();
		
		String sample = "kala";
		long c = ChecksumHelpers.crc32(sample);
		System.out.println("time: " + (System.currentTimeMillis() - start));
		System.out.println(c);
		
		CRC32 crc32 = new CRC32();
		crc32.update(sample.getBytes("UTF-8"));
		assertEquals(crc32.getValue(), c);
	}
	
	public void testcrc32() throws UnsupportedEncodingException {
		long start = System.currentTimeMillis();
		
		String sample = "[{\"text\":\"Server\",\"elementType\":\"server\",\"shape\":\"403,110,60,80\",\"backgroundColor\":\"102,153,255,0:51,51,51,1\",\"textColor\":\"68,68,68,1\",\"version\":2, \"id\":1021157283, \"clientId\":\"F135894057551812\",\"cd\":\"\"}]";
		long c = ChecksumHelpers.crc32(sample);
		System.out.println("time: " + (System.currentTimeMillis() - start));
		System.out.println(c);
		
		CRC32 crc32 = new CRC32();
		crc32.update(sample.getBytes("UTF-8"));
		assertEquals(crc32.getValue(), c);
	}
}
