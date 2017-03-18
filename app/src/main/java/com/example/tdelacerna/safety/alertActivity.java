package com.example.tdelacerna.safety;


import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class alertActivity extends AppCompatActivity{

    TextView contactsDisplay;
    Button pickContacts;
    final int CONTACT_PICK_REQUEST = 65535;
    //Button sendSMS;
    String theNumber;
    String msg="I NEED HELP! Flat Tire at this location";
    String msg1="I NEED HELP! I had a Car Accident at this location";
    String msg2="I NEED HELP! I'm out of gas at this location";
    String msg3="I NEED HELP! I got injured at this location";
    String msg4="I NEED HELP! I fell down the stairs";
    String msg5="EMERGENCY! There's a fire at this location";
    Button FlatTire, Fall, CarAccident, Injured, OutofGas, FireAlert;
    LocationService appLocationService;
    String locationAddress;
    String loc;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.locate);

       // sendSMS = (Button) findViewById(R.id.sendBtn);
        contactsDisplay = (TextView) findViewById(R.id.txt_selected_contacts);
        pickContacts = (Button) findViewById(R.id.btn_pick_contacts);


        FlatTire = (Button) findViewById(R.id.flatTire);
        Fall = (Button) findViewById(R.id.fall);
        CarAccident = (Button) findViewById(R.id.caracc);
        Injured = (Button) findViewById(R.id.injured);
        OutofGas = (Button) findViewById(R.id.outofgas);
        FireAlert = (Button) findViewById(R.id.firealert);


        pickContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentContactPick = new Intent(alertActivity.this, ContactsPickerActivity.class);
                alertActivity.this.startActivityForResult(intentContactPick, CONTACT_PICK_REQUEST);

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

        FlatTire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //sendMsg(theNumber, msg);
                appLocationService = new LocationService(alertActivity.this);

                if(appLocationService.canGetLocation()) {
                    double latitude = appLocationService.getLatitude();
                    double longitude = appLocationService.getLongitude();

                    LocationAddress locationAddress = new LocationAddress();
                    locationAddress.getAddressFromLocation(latitude, longitude,
                            getApplicationContext(), new GeocoderHandler());

                    loc = latitude+ "," +longitude;

                } else {
                    appLocationService.showSettingsAlert();

                }
            }

            class GeocoderHandler extends Handler {
                @Override
                public void handleMessage(Message message) {
                    switch (message.what) {
                        case 1:
                            Bundle bundle = message.getData();
                            locationAddress = msg + "\nhttps://maps.google.com/?q=" + loc;
                            // locationAddress = bundle.getString("address");
                            break;
                        default:
                            locationAddress = null;
                    }
                    ArrayList<Contact> selectedContacts = data.getParcelableArrayListExtra("SelectedContacts");
                    for (int i = 0; i < selectedContacts.size(); i++) {
                        theNumber = selectedContacts.get(i).phone;
                        sendMsg(theNumber, locationAddress);

                    }
                }
        }
    });

        Injured.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                locationAddress = msg3 + "\nhttps://maps.google.com/?q=" + loc;
                sendMsg(theNumber, locationAddress);
            }
        });

        CarAccident.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                locationAddress = msg1 + "\nhttps://maps.google.com/?q=" + loc;
                sendMsg(theNumber, locationAddress);
            }
        });

        FireAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                locationAddress = msg5 + "\nhttps://maps.google.com/?q=" + loc;
                sendMsg(theNumber, locationAddress);
            }
        });

        OutofGas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                locationAddress = msg2 + "\nhttps://maps.google.com/?q=" + loc;
                sendMsg(theNumber, locationAddress);
            }
        });

        Fall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendMsg(theNumber, msg4);
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

                                Toast.makeText(alertActivity.this, "SMS SENT", Toast.LENGTH_LONG).show();
                                break;
                            case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                                Toast.makeText(alertActivity.this, "Generic Failure", Toast.LENGTH_LONG).show();
                                break;
                            case SmsManager.RESULT_ERROR_NO_SERVICE:
                                Toast.makeText(alertActivity.this, "No Service", Toast.LENGTH_LONG).show();
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

