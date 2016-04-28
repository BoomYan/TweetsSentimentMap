package com.tweetsmap.streamServer;

import java.util.HashSet;
import java.util.Set;

import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;
import com.tweetsmap.streamServer.TweetsHandler.TweetsCallback;

import twitter4j.Status;

public class Initializer {
	private static final TweetsHandler tweetsHandler;
	private static final SQSHandler SQS_HANDLER = new SQSHandler();
	static {
		tweetsHandler = new TweetsHandler(new TweetsCallback() {
			@Override
			public void handleTweets(Status status) {
				// TODO Auto-generated method stub
				JSONObject obj = new JSONObject();
				try {
					obj.put("Text", status.getText());
					obj.put("location", ((Double) status.getGeoLocation().getLatitude()).toString() + ","
							+ ((Double) status.getGeoLocation().getLongitude()).toString());
					SQS_HANDLER.sendMessage(obj);
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
		});
	}

	// predefined tracked words
	private static final Set<String> TRACK_WORDS = new HashSet<>();
	static {
		TRACK_WORDS.add("sanders");
		TRACK_WORDS.add("clinton");
		TRACK_WORDS.add("trump");
		TRACK_WORDS.add("cruz");
	}

	public static void startTrackingTweets() {
		tweetsHandler.setTrackingAndStart(TRACK_WORDS);
	}

}
