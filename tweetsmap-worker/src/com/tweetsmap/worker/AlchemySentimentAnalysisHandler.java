package com.tweetsmap.worker;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import utils.Utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class AlchemySentimentAnalysisHandler {

	private static final String apiEndpoint = "http://gateway-a.watsonplatform.net/calls/text/TextGetTextSentiment?apikey=2f063efc3e9012e00b8dacda1411881aece00ea7&outputMode=json&text=";

	public static int analyzeAText(String text) {
		try {
			String encodedText = URLEncoder.encode(text, "UTF-8");
			String url = Utils.buildURL(apiEndpoint, encodedText);
			JSONObject response = Utils.sendGetRequest(url);
			System.out.println(response);
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
		} catch (UnsupportedEncodingException | ParseException e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
	}

}
