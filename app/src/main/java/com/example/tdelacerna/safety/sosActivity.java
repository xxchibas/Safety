package com.example.tdelacerna.safety;


import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class sosActivity extends AppCompatActivity {

    TextView contactsDisplay;
    Button pickContacts;
    final int CONTACT_PICK_REQUEST = 65535;
    Button sendSMS;
    String theNumber;
    LocationService appLocationService;
    String msg = "HELP! I AM IN DANGER!";
    String locationAddress;
    String loc;
    int counter =0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sos_main);

        sendSMS = (Button) findViewById(R.id.sendBtn);
        appLocationService= new LocationService(sosActivity.this);
        contactsDisplay = (TextView) findViewById(R.id.txt_selected_contacts);
        pickContacts = (Button) findViewById(R.id.btn_pick_contacts);

        pickContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentContactPick = new Intent(sosActivity.this, ContactsPickerActivity.class);
                sosActivity.this.startActivityForResult(intentContactPick, CONTACT_PICK_REQUEST);

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CONTACT_PICK_REQUEST && resultCode == RESULT_OK) {

            ArrayList<Contact> selectedContacts = data.getParcelableArrayListExtra("SelectedContacts");

            String display = "";
            for (int i = 0; i < selectedContacts.size(); i++) {


                display += (i + 1) + "   " + selectedContacts.get(i).toString() + "\n";

                theNumber = selectedContacts.get(i).phone;

            }
            contactsDisplay.setText("Selected Contacts : \n\n" + display);


        }

        sendSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ArrayList<Contact> selectedContacts = data.getParcelableArrayListExtra("SelectedContacts");
                //for (int i = 0; i < selectedContacts.size(); i++) {
                //  theNumber = selectedContacts.get(i).phone;
                //sendMsg(theNumber, locationAddress );

                appLocationService = new LocationService(sosActivity.this);

                if(appLocationService.canGetLocation()) {
                    double latitude = appLocationService.getLatitude();
                    double longitude = appLocationService.getLongitude();

                    LocationAddress locationAddress = new LocationAddress();
                    locationAddress.getAddressFromLocation(latitude, longitude,
                     getApplicationContext(), new GeocoderHandler());

                    loc = latitude+ "," +longitude;

                } else {
                    appLocationService.showSettingsAlert();

                //Location networkLocation = appLocationService.getLocation
                      //  (LocationManager.NETWORK_PROVIDER);
                //if (networkLocation != null) {
                  //  double latitude = networkLocation.getLatitude();
                   // double longitude = networkLocation.getLongitude();
                    //LocationAddress locationAddress = new LocationAddress();
                   //locationAddress.getAddressFromLocation(latitude, longitude,
                     //      getApplicationContext(), new GeocoderHandler());
                    //loc=latitude+","+longitude;

                }
            }

            class GeocoderHandler extends Handler {
                @Override
                public void handleMessage(Message message) {
                    switch (message.what) {
                        case 1:
                            Bundle bundle = message.getData();
                            locationAddress = msg + "\nCheck my location:\nhttps://maps.google.com/?q=" + loc;
                           // locationAddress = bundle.getString("address");
                            break;
                        default:
                            locationAddress = null;
                    }
                    ArrayList<Contact> selectedContacts = data.getParcelableArrayListExtra("SelectedContacts");
                    for (int i = 0; i < selectedContacts.size(); i++) {
                        theNumber = selectedContacts.get(i).phone;
                      //  if(theNumber.length()>0 && locationAddress.length()>0)
                        sendMsg(theNumber, locationAddress);

                    }
                }
            }

        });

    }

    protected void sendMsg(String theNumber, String msg) {
        String SENT = "Message Sent";
        String DELIVERED = "Message Delivered";


        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(SENT), 0);
        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0, new Intent(DELIVERED), 0);

        registerReceiver(new BroadcastReceiver() {
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:

                        Toast.makeText(sosActivity.this, "SMS SENT", Toast.LENGTH_LONG).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(sosActivity.this, "Generic Failure", Toast.LENGTH_LONG).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(sosActivity.this, "No Service", Toast.LENGTH_LONG).show();
                        break;
                }
            }
        }, new IntentFilter(SENT));

        registerReceiver(new BroadcastReceiver() {

            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS delivered", Toast.LENGTH_LONG).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(getBaseContext(), "SMS not delivered", Toast.LENGTH_LONG).show();
                        break;
                }

            }
        }, new IntentFilter(DELIVERED));


        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(theNumber, null, msg, sentPI, deliveredPI);





    }



}

