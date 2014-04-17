package com.iqbal.contactsyncbg.sync;

import android.content.Context;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

public class SyncContact extends AsyncTask<String, Void, String> {
	Context mContext;
	String usertoken;
	String username, id;
	int type;

	public SyncContact(Context act, String mid, String name) {
		this.mContext = act;
		this.username = name;
		this.id = mid;

	}

	@Override
	public String doInBackground(String... params) {
		String jsonContac=SyncUtils.GetContact_forSync(mContext, id,
				username);
		//Http Operation 
		//Post jsoncontact to server and fetch existing contact in server or intersaction of contacts
		
		return "";
		
	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
	}

	@Override
	public void onPostExecute(String result) {
		if (result != null && result.length() != 0) {
			PreferenceManager.getDefaultSharedPreferences(mContext).edit()
					.putString("CONTACTS", result).commit();

		}

	}

}
