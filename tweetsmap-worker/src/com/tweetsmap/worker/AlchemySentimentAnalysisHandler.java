package com.tweetsmap.worker;

import utils.Utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;

public class AlchemySentimentAnalysisHandler {

	private static final String apiEndpoint = "http://gateway-a.watsonplatform.net/calls/text/TextGetTextSentiment?apikey=2f063efc3e9012e00b8dacda1411881aece00ea7&outputMode=json&text=";

	public static int analyzeAText(String text) {
		try {
			String encodedText = URLEncoder.encode(text, "UTF-8");
			String url = Utils.buildURL(apiEndpoint, encodedText);
			JSONObject response = Utils.sendGetRequest(url);
			String type = Utils.getJSONObject(response, "docSentiment").get("type").toString();
			switch (type) {
				case "positive":
					return 1;
				case "neutral":
					return 0;
				case "negative":
					return -1;
				default:
					return 0;
			}
		} catch (UnsupportedEncodingException | JSONException e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
	}

}
