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

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.http.HttpStatus;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.soulgalore.crawler.core.Crawler;
import com.soulgalore.crawler.core.CrawlerResult;
import com.soulgalore.crawler.core.HTMLPageResponse;
import com.soulgalore.crawler.core.CrawlerURL;
import com.soulgalore.crawler.guice.CrawlModule;
import com.soulgalore.crawler.util.StatusCode;

/**
 * Crawl to File. To files will be created, one with the working urls &amp; one with the none working
 * urls. Each url will be on one new line.
 * 
 * @author peter
 * 
 */
public class CrawlToFile extends AbstractCrawl {

  public static final String DEFAULT_FILENAME = "urls.txt";
  public static final String DEFAULT_ERROR_FILENAME = "errorurls.txt";

  private final String fileName;
  private final String errorFileName;
  private final boolean verbose;

  CrawlToFile(String[] args) throws ParseException {
    super(args);
    fileName = getLine().getOptionValue("filename", DEFAULT_FILENAME);
    errorFileName = getLine().getOptionValue("errorfilename", DEFAULT_ERROR_FILENAME);
    verbose = Boolean.valueOf(getLine().getOptionValue("verbose", "false"));

  }

  /**
   * Run.
   * 
   * @param args the args
   */
  public static void main(String[] args) {

    try {
      final CrawlToFile crawl = new CrawlToFile(args);
      crawl.crawl();

    } catch (ParseException e) {
      System.err.print(e.getMessage());
    } catch (IllegalArgumentException e) {
      System.err.println(e.getMessage());
    }

  }

  private void crawl() {
    final Injector injector = Guice.createInjector(new CrawlModule());
    final Crawler crawler = injector.getInstance(Crawler.class);

    final CrawlerResult result = crawler.getUrls(getConfiguration());

    final StringBuilder workingUrls = new StringBuilder();
    final StringBuilder nonWorkingUrls = new StringBuilder();

    String separator = System.getProperty( "line.separator" ); 
    
    for (CrawlerURL workingUrl : result.getUrls()) {
      workingUrls.append(workingUrl.getUrl()).append(separator);

    }

    if (verbose) System.out.println("Start storing file working urls " + fileName);

    writeFile(fileName, workingUrls.toString());


    if (result.getNonWorkingUrls().size() > 0) {
      for (HTMLPageResponse nonWorkingUrl : result.getNonWorkingUrls()) {
        nonWorkingUrls.append(StatusCode.toFriendlyName(nonWorkingUrl.getResponseCode()))
            .append(",").append(nonWorkingUrl.getUrl());
        if (nonWorkingUrl.getResponseCode() >= HttpStatus.SC_NOT_FOUND)
          nonWorkingUrls.append(" from ").append(nonWorkingUrl.getPageUrl().getReferer());
        nonWorkingUrls.append(separator);
      }

      if (verbose) System.out.println("Start storing file non working urls " + errorFileName);
      writeFile(errorFileName, nonWorkingUrls.toString());
    }

    crawler.shutdown();
  }

  /**
   * Get the options.
   * 
   * @return the specific CrawlToCsv options
   */
  @Override
  protected Options getOptions() {
    final Options options = super.getOptions();

    final Option filenameOption =
        new Option("f", "the name of the output file, default name is " + DEFAULT_FILENAME
            + " [optional]");
    filenameOption.setArgName("FILENAME");
    filenameOption.setLongOpt("filename");
    filenameOption.setRequired(false);
    filenameOption.setArgs(1);

    options.addOption(filenameOption);

    final Option errorFilenameOption =
        new Option("ef", "the name of the error output file, default name is "
            + DEFAULT_ERROR_FILENAME + " [optional]");
    errorFilenameOption.setArgName("ERRORFILENAME");
    errorFilenameOption.setLongOpt("errorfilename");
    errorFilenameOption.setRequired(false);
    errorFilenameOption.setArgs(1);

    options.addOption(errorFilenameOption);

    final Option verboseOption = new Option("ve", "verbose logging, default is false [optional]");
    verboseOption.setArgName("VERBOSE");
    verboseOption.setLongOpt("verbose");
    verboseOption.setRequired(false);
    verboseOption.setArgs(1);
    verboseOption.setType(Boolean.class);

    options.addOption(verboseOption);

    return options;

  }

  private void writeFile(String fileName, String output) {
    Writer out = null;
    try {
      out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName), "UTF-8"));
      out.write(output);
    } catch (UnsupportedEncodingException e) {
      // TODO Auto-generated catch block
      System.err.println(e);
    } catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      System.err.println(e);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      System.err.println(e);
    } finally {
      if (out != null) try {
        out.close();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        System.err.println(e);
      }
    }
  }
}
