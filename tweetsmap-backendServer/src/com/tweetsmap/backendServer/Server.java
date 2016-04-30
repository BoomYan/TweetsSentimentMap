package com.tweetsmap.backendServer;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import com.amazonaws.util.json.JSONArray;
import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;

//handles all the communication with the frontend
/*
 * pushing commands
 * - searchResponse - for search request, with a tweet object(text, location, sentiment)
 * - newlyIndexedTweet - for newly indexed tweets, with a tweet object(text, location, sentiment)
 * - other commands - message to shown on server
 * 
 * receiving commands
 * - getTweets - a search request, with a query string and a query location 
 */
@ServerEndpoint("/server") 
public class Server {
	
	private static final String COMMAND = "command";
	private static final String TWEET = "tweet";
	
	//session and it's corresponding tracking words
	static final private Map<Session, String> sessionMap = new HashMap<>();
	
    @OnOpen
    public void onOpen(Session session){
        System.out.println(session.getId() + " has opened a connection"); 
        sessionMap.put(session, "");
        try {
        	JSONObject msg = new JSONObject().put(COMMAND, "Connection Established");
        	sendMessageToASession(session, msg);
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
    }
    
    //exp message
    //{"command":"getTweets","query":"Ted","location":"19,20"}
 
    @OnMessage
    public void onMessage(String message, Session session){
    	try {
	    	//receive a search request
	        System.out.println("Message from " + session.getId() + ": " + message);
	        //deal with geo data
	        JSONObject commands = new JSONObject(message);
	        if (commands.getString(COMMAND).equals("getTweets")) {
	        	sessionMap.put(session, commands.getString("query"));
	        	JSONObject msg = new JSONObject().put(COMMAND, "Start Tracking " + commands.getString("query"));
		        sendMessageToASession(session, msg);
		       	//flush messages stored in CloudSearch to client
		        queryTweetsAndFlush(session, commands);
	        }
	        
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    	}
    }
 
    @OnClose
    public void onClose(Session session){
        System.out.println("Session " +session.getId()+" has ended");
        sessionMap.remove(session);
    }
    
    //assume a new tweet
    public static void onNewTweet(JSONObject tweetObject) throws JSONException {
    	//send to elasticSearch
    	ElasticSearchHandler.sendATweet(tweetObject);
    	//send to all current clients
		JSONObject msg = new JSONObject();
		msg.put(TWEET, tweetObject);
		msg.put(COMMAND, "newlyIndexedTweet");
    	sendMessageToAll(msg);
    }
    

    
    //flush permanently stored texts to a session
    private static void queryTweetsAndFlush(Session session, JSONObject commands) throws JSONException{
    	JSONArray tweets = null;
    	if (commands.tryGetString("location") == null) {
    		tweets = new JSONArray(ElasticSearchHandler.searchTweets(commands.getString("query")).toString());
    	}
    	else {
    		String[] latlon = commands.getString("location").split(",");
    		tweets = new JSONArray(ElasticSearchHandler.searchTweetsByLocation(commands.getString("query"), latlon[0], latlon[1]).toString());
    	}
    	for (int i = 0; i < tweets.length(); i++) {
    		JSONObject tweet = tweets.getJSONObject(i);
    		JSONObject msg = new JSONObject();
    		msg.put(TWEET, tweet);
    		msg.put(COMMAND, "searchResponse");
    		sendMessageToASession(session, msg);
    	}
    }
    
    //send a serialized message to a session
    private static void sendMessageToASession(Session session, JSONObject msg) {
    	try {
			session.getBasicRemote().sendText(msg.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    //send a serialized message to all sessions    
    private static void sendMessageToAll(JSONObject msg) {
    	for (Session session : sessionMap.keySet()) {
    		sendMessageToASession(session, msg);
    	}
    }
   
    
}