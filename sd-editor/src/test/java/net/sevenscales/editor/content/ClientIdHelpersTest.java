package net.sevenscales.editor.content;

import junit.framework.TestCase;
import net.sevenscales.domain.utils.SLogger;
import net.sevenscales.editor.content.ClientIdHelpers.UniqueChecker;

public class ClientIdHelpersTest extends TestCase {
	private static class Unique implements UniqueChecker {
		private int failTimes;
		private int iterationCount;

		public Unique(int failTimes) {
			this.failTimes = failTimes;
		}
		
		public boolean isUnique(String clientId) {
			boolean result = false;
			if (failTimes == ++iterationCount) {
				result = true;
			}
			System.out.println(SLogger.format("isUnique {} {}", String.valueOf(failTimes), String.valueOf(iterationCount)));
			return result;
		}
		
		public int getIterationCount() {
			return iterationCount;
		}
		
		public int getFailTimes() {
			return failTimes;
		}
	}
	
	public void test1() {
		Unique u = new Unique(3);
		int i = 0;
//		String result = ClientIdHelpers.generateClientId(i, u);
//		System.out.println(result);
//		assertTrue(result.length() > 0);
//		assertEquals(u.getFailTimes(), u.getIterationCount());
	}
}
