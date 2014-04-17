package com.iqbal.contactsyncbg.sync;

import java.io.UnsupportedEncodingException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.util.Base64;
import android.util.Log;

public class SyncUtils {

	public static String AUTHORITY = "com.android.contacts";
	public static String ACCOUNT_TYPE = "com.iqbal.account";

	public static void CreateAccount(Context ctxt, String user, String pass) {
		Account account = new Account(user, ACCOUNT_TYPE);
		AccountManager am = AccountManager.get(ctxt);
		if (am.addAccountExplicitly(account, pass, null)) {
			Bundle result = new Bundle();
			result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
			result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);

			ContentResolver.setIsSyncable(account, "com.android.contacts", 1);
			ContentResolver.setSyncAutomatically(account, AUTHORITY, true);

			Log.i("SyncUtils", "Account Added Successfully");

		}
	}

	public static void sendContactsSync(final Context context, final String id,
			final String username) {
		if (context != null && id != null && username != null)
			new Handler(Looper.getMainLooper()).post(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					new SyncContact(context, id, username).execute();

				}
			});
	}

	public static String GetContact_forSync(Context context, String id,
			String username) {

		int size = 0;
		int totalread = 0;
		JSONObject parent = new JSONObject();
		JSONObject jsonObject = new JSONObject();
		JSONArray jsonarray = new JSONArray();
		String json = null;
		ContentResolver cr = context.getContentResolver();
		Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null,
				null, null, null);
		size = cur.getCount();
		if (cur.getCount() > 0) {
			while (cur.moveToNext()) {
				String ids = cur.getString(cur.getColumnIndex(BaseColumns._ID));
				String name = cur
						.getString(cur
								.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
				if (Integer
						.parseInt(cur.getString(cur
								.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
					Cursor pCur = cr.query(
							ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
							null,
							ContactsContract.CommonDataKinds.Phone.CONTACT_ID
									+ " = ?", new String[] { id }, null);
					while (pCur.moveToNext()) {
						int phoneType = pCur
								.getInt(pCur
										.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
						String phoneNumber = pCur
								.getString(
										pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
								.replaceAll("\\s", "");
						;
						JSONObject jsonid = new JSONObject();
						try {
							jsonid.put("id", id);
							jsonid.put("nos",
									phoneNumber.replaceAll("[-()+]", ""));
							jsonarray.put(jsonid);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						switch (phoneType) {
						case Phone.TYPE_MOBILE:
							// Log.e(name + "(mobile number)", phoneNumber);
							break;
						case Phone.TYPE_HOME:
							// Log.e(name + "(home number)", phoneNumber);
							break;
						case Phone.TYPE_WORK:
							// Log.e(name + "(work number)", phoneNumber);
							break;
						case Phone.TYPE_OTHER:
							// Log.e(name + "(other number)", phoneNumber);
							break;
						default:
							break;
						}
					}
					pCur.close();
				}
				totalread++;

			}
		}
		try {
			jsonObject.put("cid", id);
			jsonObject.put("user", username);

			jsonObject.put("contacts", jsonarray);
			jsonObject.put("country", "india");
			parent.put("query", jsonObject);

			Log.i("sync json", parent.toString());

			try {
				json = Base64.encodeToString(parent.toString()
						.getBytes("utf-8"), Base64.DEFAULT);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (JSONException e) {

			e.printStackTrace();
		}

		return json;
	}
}
