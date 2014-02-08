# Java web crawler [![Build Status](https://secure.travis-ci.org/sitespeedio/crawler.png?branch=master)](http://travis-ci.org/sitespeedio/crawler)

Simple java (1.6) crawler to crawl web pages on one and same domain. If your page is redirected to another domain, that page is not picked up EXCEPT if it is the first URL that is tested. Basicly you can do this:
<ul>
<li>Crawl from a start point, defining the depth of the crawl and decide to crawl only a specific path</li>
<li>Output all working urls</li>
<li>Output the data to a csv file, separated by working (200 response code) and non working url</li>
<li>Output the data to two text files, one with working urls and one with none working. Each url will be on one new line.</li>
<li>Output url:s that contains a keyword in the html</li>
<li>Exprimental support for verifying that assets on a page work</li>
</ul>


## How to crawl

A simple crawl have the following options, and will output the url:s crawled to system out. Note, only urls that returns 200 will be outputted by default:
<pre>
usage: CrawlToSystemOut [-l <LEVEL>] [-np <NOPATH>] [-p <PATH>] -u <URL> [-v <VERIFY>]
 -l,--level <LEVEL>            how deep the crawl should be done, default is 1 [optional]
 -np,--notFollowPath <NOPATH>  no url:s on this path will be crawled [optional]
 -p,--followPath <PATH>        stay on this path when crawling [optional]
 -u,--url <URL>                the page that is the startpoint of the crawl, examle http://mydomain.com/mypage
 -v,--verify <VERIFY>          verify that all links are returning 200, default is set to true [optional] 
 -rh,--requestHeaders <REQUEST-HEADERS>   the request headers by the form of header1:value1@header2:value2 [optional]
</pre>


You can choose to output the crawled list to two plain text files, one with working urls, and one with the none working:
<pre>
usage: CrawlToFile [-ef <ERRORFILENAME>] [-f <FILENAME>] [-l <LEVEL>] [-np <NOPATH>] [-p <PATH>] -u <URL> [-v <VERIFY>] [-ve <VERBOSE>]
 -ef,--errorfilename <ERRORFILENAME>   the name of the error output file, default name is errorurls.txt [optional]
 -f,--filename <FILENAME>              the name of the output file, default name is urls.txt [optional]
 -l,--level <LEVEL>                    how deep the crawl should be done, default is 1 [optional]
 -np,--notFollowPath <NOPATH>          no url:s on this path will be crawled [optional]
 -p,--followPath <PATH>                stay on this path when crawling [optional]
 -u,--url <URL>                        the page that is the startpoint of the crawl, examle http://mydomain.com/mypage
 -v,--verify <VERIFY>                  verify that all links are returning 200, default is set to true [optional]
 -ve,--verbose <VERBOSE>               verbose logging, default is false [optional]
 -rh,--requestHeaders <REQUEST-HEADERS>   the request headers by the form of header1:value1@header2:value2 [optional] 
</pre>


You can choose to output the result in a csv file, and separate the urls by working and non working:
<pre>
usage: CrawlToCsv [-f <FILENAME>] [-l <LEVEL>] [-np <NOPATH>] [-p <PATH>] -u <URL> [-v <VERIFY>]
 -f,--filename <FILENAME>       the name of the csv output file, default name is result.csv [optional]
 -l,--level <LEVEL>             how deep the crawl should be done, default is 1 [optional]
 -np,--notFollowPath <NOPATH>   no url:s on this path will be crawled [optional]
 -p,--followPath <PATH>         stay on this path when crawling [optional]
 -u,--url <URL>                 the page that is the startpoint of the crawl, examle http://mydomain.com/mypage
 -v,--verify <VERIFY>           verify that all links are returning 200, default is set to true [optional]
 -rh,--requestHeaders <REQUEST-HEADERS>   the request headers by the form of header1:value1@header2:value2 [optional] 
</pre>

Crawl and output urls that contains specific keyword in the html
<pre>
usage: CrawlToPlainTxtOnlyMatching -k <KEYWORD> [-l <LEVEL>] [-np <NOPATH>] [-p <PATH>] -u <URL> [-v <VERIFY>]
 -k,--keyword <KEYWORD>         the keyword to search for in the page [required]
 -l,--level <LEVEL>             how deep the crawl should be done, default is 1 [optional]
 -np,--notFollowPath <NOPATH>   no url:s on this path will be crawled [optional]
 -p,--followPath <PATH>         stay on this path when crawling [optional]
 -u,--url <URL>                 the page that is the startpoint of the crawl, examle http://mydomain.com/mypage
 -v,--verify <VERIFY>           verify that all links are returning 200, default is set to true [optional]
 -rh,--requestHeaders <REQUEST-HEADERS>   the request headers by the form of header1:value1@header2:value2 [optional] 
</pre>


## Configuration
There are also configuration that you either configure in the crawler.properties file or override them by adding them as a system property. By default they are configured:
<pre>
## Override these properties by set a system property
com.soulgalore.crawler.nrofhttpthreads=5
com.soulgalore.crawler.threadsinworkingpool=5
com.soulgalore.crawler.http.socket.timeout=5000
com.soulgalore.crawler.http.connection.timeout=5000
# Auth like:
# soulislove.com:80:username:password,...
com.soulgalore.crawler.auth=
# Proxy properties, if you are behind a proxy.                                                                                                                                                          
## The host by this special format: http:proxy.soulgalore.com:80                                                                                                                                        
com.soulgalore.crawler.proxy=
</pre>
The location of crawler.properties file can be set with the system property com.soulgalore.crawler.propertydir.

## Examples

Checkout the project and compile your own full jar (all dependencies included):
<pre>git clone git@github.com:soulgalore/crawler.git</pre>

or add it to Maven, if you want to include the crawler in your project:
<pre>
&lt;dependency&gt;
 &lt;groupId&gt;com.soulgalore&lt;/groupId&gt;
 &lt;artifactId&gt;crawler&lt;/artifactId&gt;
 &lt;version&gt;1.5.11&lt;/version&gt;
&lt;/dependency&gt;
</pre>

## Examples

Running from the jar, fetching two levels depth and only fetch urls that contains "/tagg/"
<pre>
java -jar crawler-1.5.11-full.jar -u http://soulislove.com -l 2 -p /tagg/
</pre>

Running from the jar, adding base auth
<pre>
java -jar -Dcom.soulgalore.crawler.auth=soulgalore.com:80:peter:secret crawler-1.5.11-full.jar -u http://soulislove.com
</pre>

Running from the jar, output urls in csv file
<pre>
java -cp crawler-1.5.11-full.jar com.soulgalore.crawler.run.CrawlToCsv -u http://soulislove.com
</pre>

Running from the jar, output urls into two text files: workingurls.txt and nonworkingurls.txt
<pre>
java -cp crawler-1.5.11-full.jar com.soulgalore.crawler.run.CrawlToFile -u http://soulislove.com -f workingurls.txt -ef nonworkingurls.txt
</pre>

Running from the jar, verify that assets are ok
<pre>
java -cp crawler-1.5.11-full.jar com.soulgalore.crawler.run.CrawlAndVerifyAssets -u http://www.peterhedenskog.com
</pre>

## License

Copyright 2014 Peter Hedenskog

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.


[![Bitdeli Badge](https://d2weczhvl823v0.cloudfront.net/soulgalore/crawler/trend.png)](https://bitdeli.com/free "Bitdeli Badge")

