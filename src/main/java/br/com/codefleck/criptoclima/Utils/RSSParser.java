package br.com.codefleck.criptoclima.Utils;


import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

import java.io.IOException;
import java.net.URL;

public class RSSParser {
    public static String readRssFeed(String feedUrl) throws IOException, FeedException {
        URL feedSource = new URL(feedUrl);
        SyndFeedInput input = new SyndFeedInput();
        SyndFeed feed = input.build(new XmlReader(feedSource));
        String parsedFeed = "";
        for(Object o: feed.getEntries()) {
            SyndEntry entry = (SyndEntry) o;
            parsedFeed += "<p>" + "<h4>" + "<br>" + entry.getTitle() + "</br>" + "</h4>" + "</p>"
                    + "<p>" + entry.getPublishedDate() + "</p>"
                    + "<br>" + "<a href=" + entry.getLink() + ">" + "full article" + "</a>" + "</br>";
        }

        return parsedFeed;
    }
}