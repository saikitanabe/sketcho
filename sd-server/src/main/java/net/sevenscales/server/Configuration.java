package net.sevenscales.server;

import java.io.Serializable;

public class Configuration implements Serializable {
  public String springConfigurationFile;
  public Configuration(String springConfigurationFile) {
    this.springConfigurationFile = springConfigurationFile;
  }
}
