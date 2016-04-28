package com.tweetsmap.worker;

import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.util.json.JSONObject;

import utils.AwsUtils;

public class SNSHandler {
	
	private static final String TWEETS_ARN = "arn:aws:sns:us-east-1:117200880597:Tweets";

	private final AmazonSNSClient sns;
	
	public SNSHandler() {
		sns = new AmazonSNSClient(AwsUtils.getCredentials());
		sns.setRegion(AwsUtils.getRegion());
	}

	public void publish(JSONObject obj) {
		sns.publish(new PublishRequest(TWEETS_ARN, obj.toString()));
	}
	
}
