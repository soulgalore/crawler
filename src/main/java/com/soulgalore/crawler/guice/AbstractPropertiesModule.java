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
package com.soulgalore.crawler.guice;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

/**
 * Base class for modules, fetches and loads the property file. By default, all properties are read
 * from {@value #PROPERTY_FILE}, and each property can be overridden by adding the same property as
 * a System property.
 * 
 */
public abstract class AbstractPropertiesModule extends AbstractModule {

  /**
   * The properties file in the class path. You can override these properties by system properties.
   */
  protected static final String PROPERTY_FILE = "crawler.properties";

  /**
   * Properties read from {@link #PROPERTY_FILE}.
   * 
   */
  private final Properties properties = new Properties();

  @Override
  protected void configure() {



    InputStream is = null;
    try {
      is = getClass().getResourceAsStream("/" + PROPERTY_FILE);
      properties.load(is);

      // override by file in the running dir
      File localFile =
          new File(new File(System.getProperty("com.soulgalore.crawler.propertydir", ".")),
              PROPERTY_FILE);

      if (localFile.exists()) {
        InputStream in = new FileInputStream(localFile);
        properties.load(in);
      }


      // override the properties by setting a system property
      properties.putAll(System.getProperties());

      Names.bindProperties(binder(), properties);

    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (is != null) try {
        is.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

  }

  /**
   * Get the properties for this module.
   * 
   * @return the properties
   */
  protected Properties getProperties() {
    return properties;
  }

}
