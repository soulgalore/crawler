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
package com.soulgalore.crawler.run;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * Class that implements the basic configuration used for every runnner.
 * 
 * 
 */
public abstract class AbstractRunner {

  /**
   * Used for converting milliseconds to seconds.
   */
  protected static final int MILLISECONDS_PER_SECOND = 1000;

  private final CommandLineParser clp = new GnuParser();

  private final CommandLine line;

  /**
   * Setup general default values needed when running url checker.
   * 
   * @param args the args that is default for all runners
   * @throws ParseException if the input parameter couldn't be parsed
   */
  protected AbstractRunner(String[] args) throws ParseException {

    try {
      line = clp.parse(this.getOptions(), args);

    } catch (MissingOptionException moe) {

      final HelpFormatter hf = new HelpFormatter();
      hf.printHelp(this.getClass().getSimpleName(), getOptions(), true);
      throw moe;
    }

  }

  /**
   * Get the command line, used when fetching options.
   * 
   * @return the command line
   */
  protected CommandLine getLine() {
    return line;
  }

  /**
   * Get the options.
   * 
   * @return the basic options
   */
  protected Options getOptions() {

    return new Options();
  }

}
