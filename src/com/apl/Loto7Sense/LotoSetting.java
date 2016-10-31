package com.apl.Loto7Sense;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

public class LotoSetting extends PreferenceActivity implements OnPreferenceChangeListener {

	private ListPreference lPref = null;
	/**x
	 * ATTENTION: This was auto-generated to implement the App Indexing API.
	 * See https://g.co/AppIndexing/AndroidStudio for more information.
	 */
	private GoogleApiClient client;

	/**
	 *
	 */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		myPreferenceFragment myFragment;

		// 設定画面定義
		addPreferencesFromResource(R.xml.pref);

		//myFragment = new myPreferenceFragment();
		//getFragmentManager().beginTransaction().replace(android.R.id.content, myFragment).commit();

		// 表示数
		lPref = (ListPreference) findPreference(LotoConst.INI_KEY_SENSE_CNT);
		lPref.setSummary(getSummaryMsg(lPref.getValue()));
		lPref.setOnPreferenceChangeListener(this);

		// 予想回数 - 重複版のみ実行
		if (getString(R.string.app_mode).equals(LotoConst.APP_MODE_HIT)) {
			lPref = (ListPreference) findPreference(LotoConst.INI_KEY_REPEAT_CNT);
			lPref.setSummary(getSummaryMsg(lPref.getValue()));
			lPref.setOnPreferenceChangeListener(this);
		}

		// 表示スピード
		lPref = (ListPreference) findPreference(LotoConst.INI_KEY_ANIME_SPEED);
		lPref.setSummary(getSummaryMsg(lPref.getValue()));
		lPref.setOnPreferenceChangeListener(this);

		// 予想方法
		lPref = (ListPreference) findPreference(LotoConst.INI_KEY_SENSE_TYPE);
		lPref.setSummary(getSummaryMsg(lPref.getValue()));
		lPref.setOnPreferenceChangeListener(this);
		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
	}

	public static class myPreferenceFragment extends PreferenceFragment {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.pref); // => res/xml/my_pref.xml
		}
	}

	/**
	 * ATTENTION: This was auto-generated to implement the App Indexing API.
	 * See https://g.co/AppIndexing/AndroidStudio for more information.
	 */
	public Action getIndexApiAction() {
		Thing object = new Thing.Builder()
				.setName("LotoSetting Page") // TODO: Define a title for the content shown.
				// TODO: Make sure this auto-generated URL is correct.
				.setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
				.build();
		return new Action.Builder(Action.TYPE_VIEW)
				.setObject(object)
				.setActionStatus(Action.STATUS_TYPE_COMPLETED)
				.build();
	}

	@Override
	public void onStart() {
		super.onStart();

		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		client.connect();
		AppIndex.AppIndexApi.start(client, getIndexApiAction());
	}

	@Override
	public void onStop() {
		super.onStop();

		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		AppIndex.AppIndexApi.end(client, getIndexApiAction());
		client.disconnect();
	}

	/**
	 * ボタンクリック 時
	 */
	class PreferenceClick implements OnPreferenceClickListener {

		@Override
		public boolean onPreferenceClick(Preference preference) {
			return false;
		}
	}

	;

	/**
	 * 値選択時
	 */
	@Override
	public boolean onPreferenceChange(Preference arg0, Object arg1) {
		if (arg1 != null) {
			arg0.setSummary(getSummaryMsg(arg1));
			return true;
		}
		return false;
	}

	/**
	 * Summaryメッセージの作成
	 *
	 * @param inPara1
	 * @return
	 */
	private String getSummaryMsg(Object inPara1) {
		return "『" + inPara1.toString() + "』を選択しています。";
	}
}


    