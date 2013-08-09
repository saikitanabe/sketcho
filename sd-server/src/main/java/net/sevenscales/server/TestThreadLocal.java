package net.sevenscales.server;


public class TestThreadLocal {
  private static ThreadLocal<String> paydload = new ThreadLocal<String>();
  
  public static String getPayload() {
    return paydload.get();
  }
  
  public static void setPayload(String payload) {
    TestThreadLocal.paydload.set(payload);
  }

}
