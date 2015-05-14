/******************************************************
 * Web crawler
 * 
 * 
 * Copyright (C) 2012 by Peter Hedenskog (http://peterhedenskog.com)
 * 
 ****************************************************** 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 * 
 ******************************************************* 
 */
package com.soulgalore.crawler.util;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;

/**
 * Creates an instance of HttpClient that accepts every HTTPS-cert.
 * 
 * 
 */
public final class HTTPSFaker {

  private static final int HTTPS_PORT = 443;
  private static final String HTTPS = "https";

  private HTTPSFaker() {}

  /**
   * Get a HttpClient that accept any HTTP certificate.
   *
   * @param cm the connection manager to use when creating the new HttpClient
   * @return a httpClient that accept any HTTP certificate
   */
  @SuppressWarnings("deprecation")
  public static DefaultHttpClient getClientThatAllowAnyHTTPS(ThreadSafeClientConnManager cm) {

    final TrustManager easyTrustManager = new X509TrustManager() {


      public void checkClientTrusted(X509Certificate[] xcs, String string)
          throws CertificateException {}

      public void checkServerTrusted(X509Certificate[] xcs, String string)
          throws CertificateException {}

      public X509Certificate[] getAcceptedIssuers() {
        return null;
      }
    };
    final X509HostnameVerifier easyVerifier = new X509HostnameVerifier() {

      public boolean verify(String string, SSLSession ssls) {
        return true;
      }

      public void verify(String string, SSLSocket ssls) throws IOException {}

      public void verify(String string, String[] strings, String[] strings1) throws SSLException {}

      public void verify(String string, X509Certificate xc) throws SSLException {}

    };

    SSLContext ctx = null;
    try {
      ctx = SSLContext.getInstance("TLS");
      ctx.init(null, new TrustManager[] {easyTrustManager}, null);
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    } catch (KeyManagementException e) {
      throw new RuntimeException(e);
    }

    final SSLSocketFactory ssf = new SSLSocketFactory(ctx);
    ssf.setHostnameVerifier(easyVerifier);
    cm.getSchemeRegistry().register(new Scheme(HTTPS, ssf, HTTPS_PORT));

    return new DefaultHttpClient(cm);


  }


}
