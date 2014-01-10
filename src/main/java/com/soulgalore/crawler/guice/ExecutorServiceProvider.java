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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;

/**
 * Provide a Executor service.
 * 
 */
public class ExecutorServiceProvider implements Provider<ExecutorService> {

  /**
   * The number of threads used in this executor service.
   */
  private final int nrOfThreads;

  /**
   * Create a new ExecutorServiceProvider.
   * 
   * @param maxNrOfThreads the number of thread in this executor.
   */
  @Inject
  public ExecutorServiceProvider(
      @Named("com.soulgalore.crawler.threadsinworkingpool") int maxNrOfThreads) {
    nrOfThreads = maxNrOfThreads;
  }

  /**
   * Get the service.
   * 
   * @return the executor.
   */
  public ExecutorService get() {
    return Executors.newFixedThreadPool(nrOfThreads);
  }

}
