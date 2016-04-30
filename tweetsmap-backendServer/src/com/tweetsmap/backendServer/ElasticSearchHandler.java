package com.tweetsmap.backendServer;


import com.amazonaws.util.json.JSONArray;
import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;

import utils.Utils;

public class ElasticSearchHandler {

	private static final String SERVER_ENDPOINT = "http://search-tweetssearch-e6a525fepergjwmiitxz5gkqla.us-east-1.es.amazonaws.com";
	private static final String INDEX = "/tweets";
	private static final String TWEET = "/tweet";
	private static final String SEARCH = "/_search";
	private static final String SLASH = "/";
	private static final String DISTANCE = "1000km";
	private static final Integer MAX_SIZE_RETURN = 2000; 

	public static JSONObject sendATweet(JSONObject tweetObject) throws JSONException{
		String url = Utils.buildURL(SERVER_ENDPOINT, INDEX, TWEET, SLASH, ((Integer) tweetObject.hashCode()).toString());
		JSONObject res = Utils.sendPutRequest(url, tweetObject.toString());
		return res;
	}

	public static JSONArray searchTweets(String queryString) throws JSONException{
		JSONObject queryObject = addMaxSize(generateQueryJSONObject(queryString));
		return searchTweets(queryObject);
	}

	public static JSONArray searchTweetsByLocation(String queryString, String lat, String lon) throws JSONException{
		JSONObject queryObject = addMaxSize(generateQueryJSONObject(queryString, lat, lon));
		return searchTweets(queryObject);
	}
	
	private static JSONObject addMaxSize(JSONObject obj) throws JSONException {
		obj.put("size", MAX_SIZE_RETURN);
		return obj;
	}

	private static JSONArray searchTweets(JSONObject queryObject) throws JSONException{
		String url = Utils.buildURL(SERVER_ENDPOINT, INDEX, TWEET, SEARCH);
		JSONObject res = Utils.sendGetRequest(url, queryObject.toString());
		JSONArray hits = Utils.getJSONArray(Utils.getJSONObject(res, "hits"), "hits");
		JSONArray result = new JSONArray();
		for (int i = 0; i < hits.length(); i++) {
			JSONObject tweet = Utils.getJSONObject(Utils.getJSONObject(hits, i), "_source");
			result.put(tweet);
		}
		return result;
	}

	// '{"query": {"match": {"message": "testText"}}}'
	private static JSONObject generateQueryJSONObject(String queryString) throws JSONException {
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
	public static JSONObject generateQueryJSONObject(String queryString, String lat, String lon) throws JSONException {
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
		Utils.putAll(filtered, innerQuery);
		filtered.put("filter", filter);
		JSONObject query = new JSONObject();
		query.put("filtered", filtered);
		JSONObject result = new JSONObject();
		result.put("query", query);
		return result;
	}

}
