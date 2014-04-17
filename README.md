ContactSyncBG
=============

Hi,

This is ContactSyncBackground Demo as you see whatsapp,viber or other apps

To run this project you need to add appcompat library

I followed this process to achieve syncing in backgroun

1. Create AuthenticatorService and ContactsSyncAdapter Service class.
	AuthenticatorService Class is for when you create syncing account then this service run 
	so you can perform any operation during creating account or add account in setting.

2. To create account 
	SyncUtils.CreateAccount(instance, user, pass);

3. When account created successfully then you want to sync your contacts, ContactsSyncAdapterService
	is run when you perform syncing either manually(using setting) or automatically.

4. To perform syncing automatically 

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

5. After firing this operation onPerformSync() method called in ContactsSyncAdapterService 
   Here you can perform network syncing operation. I poupulated the contacts in json and post to server
   and server returning me intersection of contacts.
   
6. You need to register this service in Manifiest 

        <service
            android:name="com.iqbal.contactsyncbg.sync.ContactsSyncAdapterService"
            android:exported="true"
            android:process=":contacts" >
            <intent-filter>
                <action android:name="android.content.SyncAdapter" />
            </intent-filter>

            <meta-data
                android:name="android.content.SyncAdapter"
                android:resource="@xml/sync_contacts" />
            <meta-data
                android:name="android.provider.CONTACTS_STRUCTURE"
                android:resource="@xml/contacts" />
        </service>
        <service
            android:name="com.iqbal.contactsyncbg.sync.AuthenticatorService"
            android:exported="true"
            android:process=":auth" >
            <intent-filter>
                <action android:name="android.accounts.AccountAuthenticator" />
            </intent-filter>

            <meta-data
                android:name="android.accounts.AccountAuthenticator"
                android:resource="@xml/authenticator" />
        </service>
      
 7. Be sure of Account Type what you defined in sync_contacts and ContactSyncAdapter must be same.
 	Otherwise you will get this exception caller uid is different than the authenticator's uid to fix
 	this exception you need to check account type.
        
 8. At last you need to add permission
 
   <uses-permission android:name="android.permission.READ_CONTACTS" />
    <uses-permission android:name="android.permission.WRITE_CONTACTS" />
    <uses-permission android:name="android.permission.GET_ACCOUNTS" />
    <uses-permission android:name="android.permission.MANAGE_ACCOUNTS" />
    <uses-permission android:name="android.permission.AUTHENTICATE_ACCOUNTS" />
    <uses-permission android:name="android.permission.READ_SYNC_SETTINGS" />
    <uses-permission android:name="android.permission.WRITE_SYNC_SETTINGS" />

