package com.tweetsmap.worker;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.amazonaws.util.IOUtils;
import com.amazonaws.util.json.JSONObject;

public class WorkerServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	private final SNSHandler snsHandler;

	public WorkerServlet() {
		snsHandler = new SNSHandler();
	}

	protected void doPost(final HttpServletRequest request, final HttpServletResponse response)
			throws ServletException, IOException {
		try {
			String tweetString = IOUtils.toString(request.getInputStream());
			JSONObject tweet = new JSONObject(tweetString);
			float sentiment = AlchemySentimentAnalysisHandler.analyzeAText(tweet.getString("text"));
			tweet.put("sentiment", sentiment);
			snsHandler.publish(tweet);
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			//make sure the msg would be deleted after the process
			response.setStatus(200);
		}
		
	}

}
