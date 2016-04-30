package com.tweetsmap.backendServer;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.amazonaws.util.IOUtils;
import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;

import utils.Utils;


public class SendTweetsServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	//receive post request from SNS
	//and send tweets to elasticsearch and all sections
	protected void doPost(final HttpServletRequest request, final HttpServletResponse response)
			throws ServletException, IOException {
		String requestBody = IOUtils.toString(request.getInputStream());
		if (requestBody != null) {
			//get tweets
			System.out.printf("Request Body: %s%n", requestBody);
			JSONObject tweetObject = new JSONObject();
			try {
				JSONObject requestBodyObject = new JSONObject(requestBody);
				tweetObject = Utils.getJSONObject(requestBodyObject, "Message");
			} catch (Exception e) {
				e.printStackTrace();
				response.setStatus(400);
				return;
			}
			try {
				Server.onNewTweet(tweetObject);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			response.setStatus(200);
		}
		
	}

}
