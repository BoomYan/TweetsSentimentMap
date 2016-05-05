package com.tweetsmap.worker;

import utils.Utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;

public class AlchemySentimentAnalysisHandler {

	private static final String apiEndpoint = "http://gateway-a.watsonplatform.net/calls/text/TextGetTextSentiment?apikey=822230cafbc2af33c7ab734e2c8e07ae5024df9c&outputMode=json&text=";

	public static float analyzeAText(String text) {
		try {
			String encodedText = URLEncoder.encode(text, "UTF-8");
			String url = Utils.buildURL(apiEndpoint, encodedText);
			JSONObject response = Utils.sendGetRequest(url);
			JSONObject docSentiment = Utils.getJSONObject(response, "docSentiment");
			if (docSentiment.getString("type").equals("neutral")) {
				return 0;
			}
			else {
				return Float.parseFloat(docSentiment.getString("score"));
			}
		} catch (UnsupportedEncodingException | JSONException e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
	}
}
