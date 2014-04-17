package com.iqbal.contactsyncbg;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.iqbal.contactsyncbg.sync.SyncUtils;

public class MainActivity extends ActionBarActivity {

	private MainActivity instance;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}

		instance = this;
		
		//Populate Contact Json and post to server
		SyncUtils.sendContactsSync(instance, "3456", "iqbal");
		
		// Add Sync Account
		SyncUtils.CreateAccount(instance, "iqbal", "1234");

	}

	@Override
	protected void onResume() {
		super.onResume();

		// After Adding Account
		// Request the account to sync contacts

		AccountManager am = AccountManager.get(instance);
		Account[] acclist = am.getAccounts();
		for (Account acc : acclist) {
			if (acc.type.equals(SyncUtils.ACCOUNT_TYPE)) {
				// You need pass bundle to sync
				Bundle b = new Bundle();
				b.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
				b.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
				ContentResolver.requestSync(acc, SyncUtils.AUTHORITY, b);

				Log.i("MainActivity", "Request to sync Contacts");

			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}

}
