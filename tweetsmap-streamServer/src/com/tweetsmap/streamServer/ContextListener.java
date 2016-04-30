package com.tweetsmap.streamServer;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class ContextListener implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		Initializer.startTrackingTweets();
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {}

}
