# Java web crawler [![Build Status](https://secure.travis-ci.org/soulgalore/crawler.png?branch=master)](http://travis-ci.org/soulgalore/crawler)

Simple java crawler to crawl webpages on one and same domain. Basicly you can do this:
<ul>
<li>Crawl from a start point, defining the depth of the crawl and decide to crawl only a specific path</li>
<li>Output all working urls</li>
<li>Output the data to a csv file, separated by working (200 response code) and non working url</li>
<li>Output url:s that contains a keyword in the html</li>
</ul>


## How to crawl
A simple crawl have the following options, and will output the url:s crawled to system out:
<pre>
usage: CrawlToSystemOut [-l <LEVEL>] [-p <PATH>] -u <URL>
 -l,--level <LEVEL>       how deep the crawl should be done, default is 2
                          [optional]
 -p,--followPath <PATH>   stay on this path when crawling [optional]
 -u,--url <URL>           the page that is the startpoint of the crawl,
                          examle http://mydomain.com/mypage
</pre>

You can choose to output the result in a csv file, and separate the urls by working and non working:
<pre>
usage: CrawlToCsv [-f <FILENAME>] [-l <LEVEL>] [-p <PATH>] -u <URL>
 -f,--filename <FILENAME>   the name of the csv ouput file, default name
                            is result.csv [optional]
 -l,--level <LEVEL>         how deep the crawl should be done, default is
                            2 [optional]
 -p,--followPath <PATH>     stay on this path when crawling [optional]
 -u,--url <URL>             the page that is the startpoint of the crawl,
                            examle http://mydomain.com/mypage
</pre>

Crawl and output urls that contains specific keyword in the html
<pre>
usage: CrawlToPlainTxtOnlyMatching -k <KEYWORD> [-l <LEVEL>] [-p <PATH>] -u <URL>
 -k,--keyword <KEYWORD>   the keyword to search for in the page
                          [required]
 -l,--level <LEVEL>       how deep the crawl should be done, default is 2
                          [optional]
 -p,--followPath <PATH>   stay on this path when crawling [optional]
 -u,--url <URL>           the page that is the startpoint of the crawl,
                          examle http://mydomain.com/mypage
</pre>


## Configuration
There are also configuration that you either configure in the crawler.properties file or override them by adding them as a system property. By default they are configured:
<pre>
## Override these properties by set a system property
com.soulgalore.crawler.nrofhttpthreads=5
com.soulgalore.crawler.threadsinworkingpool=5
com.soulgalore.crawler.http.socket.timeout=5000
com.soulgalore.crawler.http.connection.timeout=5000
# Request headers like:
# header1:value1,header2:value2
com.soulgalore.crawler.requestheaders=
# Auth like:
# soulislove.com:80:username:password,...
com.soulgalore.crawler.auth=
</pre>


## Examples

Running from the jar, fetching two levels depth and only fetch urls that contains "/tagg/"
<pre>
java -jar crawler-0.5.full.jar -u http://soulislove.com -l 2 -p /tagg/
</pre>

Running from the jar, adding base auth
<pre>
java -jar -Dcom.soulgalore.crawler.auth=soulgalore.com:80:peter:secret crawler-0.5-full.jar -u http://soulislove.com
</pre>

Running from the jar, output urls in csv file
<pre>
java -cp crawler-0.5-full.jar com.soulgalore.crawler.run.CrawlToCsv -u http://soulislove.com
</pre>

## License

Copyright 2012 Peter Hedenskog

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
