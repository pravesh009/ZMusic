package com.example.pravesh.music009;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.ottr006.musicjo.R;

import java.util.ArrayList;
import java.util.HashMap;

public class PlayListActivity extends ListActivity {

	public ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.playlist);

		ArrayList<HashMap<String, String>> songsListData = new ArrayList<HashMap<String, String>>();

		SongsManager plm = new SongsManager(PlayListActivity.this);

		this.songsList = plm.getPlayList();

		for (int i = 0; i < songsList.size(); i++) {
			HashMap<String, String> song = songsList.get(i);
			songsListData.add(song);
		}


		ListAdapter adapter = new SimpleAdapter(this, songsListData,
				R.layout.playlist_item, new String[] { "songTitle" }, new int[] {
				R.id.songTitle });

		setListAdapter(adapter);


		ListView lv = getListView();

		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				int songIndex = position;

				Intent in = new Intent(getApplicationContext(),
						MusicPlayerActivity.class);

				in.putExtra("songIndex", songIndex);
				setResult(100, in);
				finish();
			}
		});

	}
}
