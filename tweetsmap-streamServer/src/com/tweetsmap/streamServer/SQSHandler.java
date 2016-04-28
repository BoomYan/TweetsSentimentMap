package com.tweetsmap.streamServer;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.util.json.JSONObject;

import utils.AwsUtils;

public class SQSHandler {

	private static final String QUEUE_URL = "https://sqs.us-east-1.amazonaws.com/117200880597/Tweets";

	private final AmazonSQS sqs;

	public SQSHandler() {
		sqs = new AmazonSQSClient(AwsUtils.getCredentials());
		sqs.setRegion(AwsUtils.getRegion());
	}

	public void sendMessage(JSONObject obj) {
		sqs.sendMessage(new SendMessageRequest(QUEUE_URL, obj.toString()));
	}

}
