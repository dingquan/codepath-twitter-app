package com.codepath.apps.twitterclient;

import android.app.DialogFragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class ComposeTweetDialog extends DialogFragment {
	private ImageView ivProfileImage;
	private TextView tvUserName;
	private TextView tvScreenName;
	private EditText etTweet;

	public ComposeTweetDialog() {
		// Empty constructor required for DialogFragment
	}

	public interface SearchFilterDialogListener {
		void onFinishDialog(String tweetJson);
	}

	public static ComposeTweetDialog newInstance() {
		ComposeTweetDialog frag = new ComposeTweetDialog();
		Bundle args = new Bundle();
		frag.setArguments(args);
		return frag;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater
				.inflate(R.layout.fragment_compose_tweet, container);
		String title = getArguments().getString("title", "Enter Name");
		getDialog().setTitle(title);

		setupViews(view);
		return view;

	}

	private void setupViews(View v) {
		ivProfileImage = (ImageView) v.findViewById(R.id.ivProfileImage);
		tvUserName = (TextView) v.findViewById(R.id.tvUserName);
		tvScreenName = (TextView) v.findViewById(R.id.tvScreenName);
		etTweet = (EditText) v.findViewById(R.id.etTweet);
	}
}
