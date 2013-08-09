package net.sevenscales.server;

public interface Transformer {

  public Object transform(Object object);
  public void lazyClone();

}
