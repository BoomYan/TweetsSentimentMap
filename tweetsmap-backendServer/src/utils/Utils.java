package utils;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.github.kevinsawicki.http.HttpRequest;

public class Utils {
	
	private static final JSONParser JSON_PARSER = new JSONParser();

	// link several strings to one string
	public static String buildURL(String... strings) {
		StringBuilder sb = new StringBuilder();
		for (String str : strings) {
			sb.append(str);
		}
		return sb.toString();
	}

	public static JSONObject getJSONObject(JSONObject jsonObject, String key) throws ParseException {
		return (JSONObject) JSON_PARSER.parse((String)jsonObject.get(key));
	}

	public static JSONObject getJSONObject(JSONArray jsonArray, int index) {
		return (JSONObject) jsonArray.get(index);
	}

	public static JSONArray getJSONArray(JSONObject jsonObject, String key) {
		return (JSONArray) jsonObject.get(key);
	}
	
	public static JSONObject ObjectToJSONObject(Object obj) throws ParseException {
		return (JSONObject) JSON_PARSER.parse((String)obj);
	}
	
	// take get http url and return response object as JSONObject
	public static JSONObject sendGetRequest(String url) throws ParseException{
		HttpRequest res = HttpRequest.get(url);
		return handleResponse(res, url);
	}
	
	public static JSONObject sendGetRequest(String url, CharSequence data) throws ParseException{
		HttpRequest res = HttpRequest.get(url).send(data);
		return handleResponse(res, url);
	}
	
	public static JSONObject sendPutRequest(String url, CharSequence data) throws ParseException {
		HttpRequest res = HttpRequest.put(url).send(data);
		return handleResponse(res, url);
	}
	
	private static JSONObject handleResponse(HttpRequest res, String url) throws ParseException {
		if (res.badRequest()) {
			throw new IllegalArgumentException("Invalid URL:" + url);
		}
		String response = res.body();
		return (JSONObject) JSON_PARSER.parse(response);
	}
}
