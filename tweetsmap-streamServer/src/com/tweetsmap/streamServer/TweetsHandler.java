package com.tweetsmap.streamServer;

import java.util.Collection;

import twitter4j.FilterQuery;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;

public class TweetsHandler {
	
	public static interface TweetsCallback {
		//called when a new tweet come
		void handleTweets(Status status);
	}
	
	private static boolean isRemote = true;
	
	private final TwitterStream twitterStream;
	
	private final StatusListener listener;

	public TweetsHandler(final TweetsCallback tweetsCallback) {
		ConfigurationBuilder cb = new ConfigurationBuilder();
		if (isRemote) {
			//for remote server
			cb.setDebugEnabled(true)
			  .setOAuthConsumerKey("5fpkfDr4r7XBH6iK1vw25Tzfy")
			  .setOAuthConsumerSecret("4cCctCkteE8ZsqlYODx5UtNMjVp8RZSypo0oLreIQJeV6kRYJ1")
			  .setOAuthAccessToken("257398944-1dMaIeaiCxuFCXAQpSP18GlCh1TDcMrlsrjw6sg0")
			  .setOAuthAccessTokenSecret("xgMBcoGE8UHHO1Thp8eLglOTnIe4R9SBASSm6YrrcISoe");
		}
		else {
			//for local server
			cb.setDebugEnabled(true)
			  .setOAuthConsumerKey("xLb2tPNxYM9IiGVrklqGMllp5")
			  .setOAuthConsumerSecret("u3W5HYVjUvGBGmvD0F3IJtzbRy0Hs4gcYTvPvgdC7NuN0iu6bg")
			  .setOAuthAccessToken("257398944-qpHWdE1DcK6Rm1IsR5xYhEJukNrRzWiIXG2ZEJrd")
			  .setOAuthAccessTokenSecret("YdREydvbSwCmNigmmcnuvL2XIAwayPLr31aKrq4jb2rX5");
		}
		cb.setJSONStoreEnabled(true);
		twitterStream = new TwitterStreamFactory(cb.build()).getInstance();
		listener = new StatusListener() {
            @Override
            public void onStatus(Status status) {
                if (status.getGeoLocation() != null && status.getLang() != null && status.getLang().equals("en")) {
                    System.out.println("@" + status.getUser().getScreenName() + " - " + status.getText());
                	tweetsCallback.handleTweets(status);
                }
            }

            @Override
            public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
                System.out.println("Got a status deletion notice id:" + statusDeletionNotice.getStatusId());
            }

            @Override
            public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
                System.out.println("Got track limitation notice:" + numberOfLimitedStatuses);
            }

            @Override
            public void onScrubGeo(long userId, long upToStatusId) {
                System.out.println("Got scrub_geo event userId:" + userId + " upToStatusId:" + upToStatusId);
            }

            @Override
            public void onStallWarning(StallWarning warning) {
                System.out.println("Got stall warning:" + warning);
            }

            @Override
            public void onException(Exception ex) {
                ex.printStackTrace();
            }
        };
        twitterStream.addListener(listener);
	}
	
	public void setTrackingAndStart(Collection<String> words) {
		String[] wordArray = words.toArray(new String[words.size()]);
		setTrackingAndStart(wordArray);
	}
	
	public void setTrackingAndStart(String... wordArray) {
		FilterQuery query = new FilterQuery();
		query.track(wordArray);
		twitterStream.filter(query);		
	}
	
	public void stop() {
		twitterStream.cleanUp();
	}

}
