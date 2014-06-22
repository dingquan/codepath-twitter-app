package com.codepath.apps.twitterclient.models;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;

public class Tweet {
	private String body;
	private long uid;
	private String createdAt;
	private User user;

	public static Tweet fromJSON(JSONObject json){
		Tweet tweet = new Tweet();
		try{
			tweet.body = json.getString("text");
			tweet.uid = json.getLong("id");
			tweet.createdAt = json.getString("created_at");
			tweet.user = User.fromJSON(json.getJSONObject("user"));
		}catch(JSONException e){
			e.printStackTrace();
			return null;
		}
		return tweet;
	}
	
	public static List<Tweet> fromJSONArray(JSONArray json){
		List<Tweet> tweets = new ArrayList<Tweet>();
		for (int i = 0; i < json.length(); i++){
			JSONObject tweetJson = null;
			try{
				tweetJson = json.getJSONObject(i);
			}
			catch(JSONException e){
				e.printStackTrace();
				continue;
			}
			
			Tweet tweet = Tweet.fromJSON(tweetJson);
			if (tweet != null){
				tweets.add(tweet);
			}
		}
		return tweets;
	}
	
	public String getBody() {
		return body;
	}

	public long getUid() {
		return uid;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public User getUser() {
		return user;
	}
	
	public String toString(){
		return body;
	}

}
