package com.soulgalore.crawler.util;


public class Auth {

  private String scope;
  private int port;
  private String userName;
  private String password;

  Auth(String theScope, String thePort, String theUserName, String thePassword) {

    scope = theScope;
    port = Integer.parseInt(thePort);
    userName = theUserName;
    password = thePassword;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    final Auth other = (Auth) obj;
    if (password == null) {
      if (other.password != null) return false;
    } else if (!password.equals(other.password)) return false;
    if (port != other.port) return false;
    if (scope == null) {
      if (other.scope != null) return false;
    } else if (!scope.equals(other.scope)) return false;
    if (userName == null) {
      if (other.userName != null) return false;
    } else if (!userName.equals(other.userName)) return false;
    return true;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((password == null) ? 0 : password.hashCode());
    result = prime * result + port;
    result = prime * result + ((scope == null) ? 0 : scope.hashCode());
    result = prime * result + ((userName == null) ? 0 : userName.hashCode());
    return result;
  }

  /**
   * Get the password.
   * 
   * @return password
   */
  public String getPassword() {
    return password;
  }

  /**
   * Get the port.
   * 
   * @return the port
   */
  public int getPort() {
    return port;
  }

  /**
   * Get the scope.
   * 
   * @return scope
   */
  public String getScope() {
    return scope;
  }

  /**
   * Get the user name.
   * 
   * @return user name
   */
  public String getUserName() {
    return userName;
  }

}
