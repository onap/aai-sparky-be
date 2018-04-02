package org.onap.aai.sparky.config;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

public class StringPropertyDecryptor {
  
  private StandardPBEStringEncryptor decryptor;
  
  public StringPropertyDecryptor(String passwordEnvironmentVariableName) {
    
    this.decryptor = new StandardPBEStringEncryptor();
    this.decryptor.setPassword(System.getenv(passwordEnvironmentVariableName));
    
  }
  
  public String decrypt(String encrypted) {
    return this.decryptor.decrypt(encrypted);
  }

}
