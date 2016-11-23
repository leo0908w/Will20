package com.org.iii.will20;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
    private AccountManager amgr;
    private TelephonyManager tmgr;
    private ContentResolver contentResolver;
    private ImageView imageView;
    private Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView) findViewById(R.id.img);
        contentResolver = getContentResolver();

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[] {
                            Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.GET_ACCOUNTS,
                            Manifest.permission.READ_CONTACTS,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                    }, 123);
        }else{
            init();
        }
    }

    private void init() {
        tmgr = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
        Log.v("will", "imi" + tmgr.getDeviceId());
        Log.v("will", "sim" + tmgr.getSubscriberId());

        tmgr.listen(new MyPhoneStateListener(), PhoneStateListener.LISTEN_CALL_STATE);

        amgr = (AccountManager)getSystemService(ACCOUNT_SERVICE);
        Account[] as = amgr.getAccounts();
        for (Account a : as) {
            Log.v("will", a.name + ":" + a.type );
        }

//        getContact();
        getPhoto();
    }

    private void getContact() {
        Log.v("will", "ok");

        String[] projection = {
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
        };
        cursor = contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection, null, null,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);

        Log.v("will", "count: " + cursor.getCount());

        while ( cursor.moveToNext()){
            String name = cursor.getString(0);
            String tel = cursor.getString(1);
            Log.v("will", name + ":" + tel);
        }
    }

    private void getPhoto() {
//        Log.v("will", "ok");

        cursor = contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
        Log.v("will", "photo: " + cursor.getCount());

        cursor.moveToLast();
        String data = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        Log.v("will", "photo: " + data);

        Bitmap photo = BitmapFactory.decodeFile(data);
        imageView.setImageBitmap(photo);
    }

    private class MyPhoneStateListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    Log.v("will", incomingNumber);
                    break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        init();
    }
}
