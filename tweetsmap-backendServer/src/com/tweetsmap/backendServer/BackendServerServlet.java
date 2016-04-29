package com.tweetsmap.backendServer;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.amazonaws.util.IOUtils;
import utils.Utils;


public class BackendServerServlet extends HttpServlet {
	
	private static final JSONParser JSON_PARSER = new JSONParser();

	private static final long serialVersionUID = 1L;
	
	//receive post request from SNS
	protected void doPost(final HttpServletRequest request, final HttpServletResponse response)
			throws ServletException, IOException {
		String requestBody = IOUtils.toString(request.getInputStream());
		if (requestBody != null) {
			//get tweets
			JSONObject tweetObject = new JSONObject();
			try {
				JSONObject requestBodyObject = (JSONObject) JSON_PARSER.parse(requestBody);
				tweetObject = Utils.getJSONObject(requestBodyObject, "Message");
			} catch (ParseException e) {
				e.printStackTrace();
				response.setStatus(400);
				return;
			}
			try {
				ElasticSearchHandler.sendATweet(tweetObject);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			response.setStatus(200);
			//TODO send to all current session
		}
		
	}

}
