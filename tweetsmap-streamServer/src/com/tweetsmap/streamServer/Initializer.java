package com.tweetsmap.streamServer;

import java.util.HashSet;
import java.util.Set;

import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;
import com.tweetsmap.streamServer.TweetsHandler.TweetsCallback;

import twitter4j.Status;

public class Initializer {
	private static TweetsHandler tweetsHandler;
	private static final SQSHandler SQS_HANDLER = new SQSHandler();

	// predefined tracked words
	private static final Set<String> TRACK_WORDS = new HashSet<>();
	static {
		TRACK_WORDS.add("bernie");
		TRACK_WORDS.add("sanders");
		TRACK_WORDS.add("hillary");
		TRACK_WORDS.add("clinton");
		TRACK_WORDS.add("donald");
		TRACK_WORDS.add("trump");
		TRACK_WORDS.add("ted");
		TRACK_WORDS.add("cruz");
	}

	public static void startTrackingTweets() {
		tweetsHandler = new TweetsHandler(new TweetsCallback() {
			@Override
			public void handleTweets(Status status) {
				JSONObject obj = new JSONObject();
				try {
					obj.put("text", status.getText());
					obj.put("location", ((Double) status.getGeoLocation().getLatitude()).toString() + ","
							+ ((Double) status.getGeoLocation().getLongitude()).toString());
					obj.put("user", status.getUser().getName());
					SQS_HANDLER.sendMessage(obj);
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
		});
		tweetsHandler.setTrackingAndStart(TRACK_WORDS);
	}

}
