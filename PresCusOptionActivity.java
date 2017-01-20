package com.example.kenjiquik.specialistcusdis;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.kenjiquik.specialistcusdis.SMS.SMSDeliveredBroadcastReceiver;
import com.example.kenjiquik.specialistcusdis.SMS.SMSSentBroadcastReceiver;

/**
 * Created by Kenji Quik (I.D 1304332) & Matthew Cho (I.D 188515)
 */

public class PresCusOptionActivity extends AppCompatActivity {
    private Button sendButton;
    private SMSSentBroadcastReceiver sentBroadcastReceiver;
    private SMSDeliveredBroadcastReceiver deliveredBroadcastReceiver;
    private final String SMS_SENT_ACTION = "SMS_SENT";
    private final String SMS_DELIVERED_ACTION = "SMS_DELIVERED";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pres_cus_option);
        final Intent intent = getIntent();
        sendButton = (Button) findViewById(R.id.sendFeedbackButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String numberString = intent.getStringExtra("phone");
                EditText messageTextView
                        = (EditText) findViewById(R.id.feedbackEditText);
                String messageString = messageTextView.getText().toString();
                PendingIntent sentPendingIntent = PendingIntent.getBroadcast
                        (PresCusOptionActivity.this, 0, new Intent(SMS_SENT_ACTION), 0);
                PendingIntent deliveredPendingIntent
                        = PendingIntent.getBroadcast
                        (PresCusOptionActivity.this, 0, new Intent(SMS_DELIVERED_ACTION), 0);
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(numberString, null, messageString,
                        sentPendingIntent, deliveredPendingIntent);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // create broadcast receivers that get notified when SMS sent
        // or delivered
        sentBroadcastReceiver = new SMSSentBroadcastReceiver();
        registerReceiver(sentBroadcastReceiver,
                new IntentFilter(SMS_SENT_ACTION));
        deliveredBroadcastReceiver = new SMSDeliveredBroadcastReceiver();
        registerReceiver(deliveredBroadcastReceiver,
                new IntentFilter(SMS_DELIVERED_ACTION));
    }

    @Override
    public void onStop() {
        super.onStop();
        if (sentBroadcastReceiver != null)
            unregisterReceiver(sentBroadcastReceiver);
        if (deliveredBroadcastReceiver != null)
            unregisterReceiver(deliveredBroadcastReceiver);
    }
}
