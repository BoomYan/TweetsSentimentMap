package com.tweetsmap.backendServer;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import utils.Utils;

public class ElasticSearchHandler {

	private static final String SERVER_ENDPOINT = "http://search-tweetssearch-e6a525fepergjwmiitxz5gkqla.us-east-1.es.amazonaws.com";
	private static final String INDEX = "/tweets";
	private static final String TWEET = "/tweet";
	private static final String SEARCH = "/_search";
	private static final String SLASH = "/";
	private static final String DISTANCE = "1000km";

	public static JSONObject sendATweet(JSONObject tweetObject) throws ParseException {
		String url = Utils.buildURL(SERVER_ENDPOINT, INDEX, TWEET, SLASH, ((Integer) tweetObject.hashCode()).toString());
		JSONObject res = Utils.sendPutRequest(url, tweetObject.toString());
		return res;
	}

	public static JSONArray searchTweets(String queryString) throws ParseException {
		JSONObject queryObject = generateQueryJSONObject(queryString);
		return searchTweets(queryObject);
	}

	public static JSONArray searchTweetsByLocation(String queryString, String lat, String lon) throws ParseException {
		JSONObject queryObject = generateQueryJSONObject(queryString, lat, lon);
		return searchTweets(queryObject);
	}

	@SuppressWarnings("unchecked")
	private static JSONArray searchTweets(JSONObject queryObject) throws ParseException {
		String url = Utils.buildURL(SERVER_ENDPOINT, INDEX, TWEET, SEARCH);
		JSONObject res = Utils.sendGetRequest(url, queryObject.toString());
		JSONArray hits = Utils.getJSONArray(Utils.getJSONObject(res, "hits"), "hits");
		JSONArray result = new JSONArray();
		for (int i = 0; i < hits.size(); i++) {
			JSONObject tweet = Utils.getJSONObject(Utils.getJSONObject(hits, i), "_source");
			result.add(tweet);
		}
		return result;
	}

	// '{"query": {"match": {"message": "testText"}}}'
	@SuppressWarnings("unchecked")
	private static JSONObject generateQueryJSONObject(String queryString) {
		JSONObject query = new JSONObject();
		JSONObject queryContents = new JSONObject();
		JSONObject match = new JSONObject();
		match.put("text", queryString);
		queryContents.put("match", match);
		query.put("query", queryContents);
		return query;
	}

//	{
//		  "query": {
//		    "filtered" : {
//		        "query" : {
//		            "match" : {}
//		        },
//		        "filter" : {
//		            "geo_distance" : {
//		                "distance" : "20km",
//		                "location" : {
//		                    "lat" : 37.9174,
//		                    "lon" : -122.3050
//		                }
//		            }
//		        }
//		    }
//		  }
//		}'
	@SuppressWarnings("unchecked")
	public static JSONObject generateQueryJSONObject(String queryString, String lat, String lon) {
		JSONObject innerQuery = generateQueryJSONObject(queryString);
		JSONObject location = new JSONObject();
		location.put("lat", lat);
		location.put("lon", lon);
		JSONObject geoDistance = new JSONObject();
		geoDistance.put("distance", DISTANCE);
		geoDistance.put("location", location);
		JSONObject filter = new JSONObject();
		filter.put("geo_distance", geoDistance);
		JSONObject filtered = new JSONObject();
		filtered.putAll(innerQuery);
		filtered.put("filter", filter);
		JSONObject query = new JSONObject();
		query.put("filtered", filtered);
		JSONObject result = new JSONObject();
		result.put("query", query);
		return result;
	}

}
