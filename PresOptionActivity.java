package com.example.kenjiquik.specialistcusdis;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.kenjiquik.specialistcusdis.SMS.SMSDeliveredBroadcastReceiver;
import com.example.kenjiquik.specialistcusdis.SMS.SMSSentBroadcastReceiver;

import java.io.IOException;
import java.nio.charset.Charset;

public class PresOptionActivity extends AppCompatActivity {

    private NfcAdapter nfcAdapter;
    private boolean writeModeEnabled;
    private Button writeButton;
    private TextView statusView;

    private SMSSentBroadcastReceiver sentBroadcastReceiver;
    private SMSDeliveredBroadcastReceiver deliveredBroadcastReceiver;
    private final String SMS_SENT_ACTION = "SMS_SENT";
    private final String SMS_DELIVERED_ACTION = "SMS_DELIVERED";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pres_option);
        TextView txt1 = (TextView) findViewById(R.id.textView);
        TextView txt2 = (TextView) findViewById(R.id.textView2);
        TextView txt3 = (TextView) findViewById(R.id.textView3);
        Intent intent = getIntent();
        final String fname = intent.getStringExtra("fname");
        final String phone = intent.getStringExtra("phone");
        final String medicine = intent.getStringExtra("medicine");
        final String GC = intent.getStringExtra("GC");
        txt1.setText(fname);
        txt2.setText(phone);
        txt3.setText(medicine);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        writeButton = (Button)findViewById(R.id.button1);
        writeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                statusView.setText("WRITE MODE ENABLED, HOLD PHONE TO TAG");
                enableWriteMode();
            }
        });

        Button remindButton = (Button) findViewById(R.id.reminderButton);
        remindButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String reminder = "Reminder to " + fname + " from " + GC + ":\n" +
                        "Your prescribed medicine " + medicine +
                        " has been prepared for you.\n" +
                        "Pick them up at my location now.";
                String numberString = phone;
                // send the sms message
                PendingIntent sentPendingIntent = PendingIntent.getBroadcast
                        (PresOptionActivity.this, 0, new Intent(SMS_SENT_ACTION), 0);
                PendingIntent deliveredPendingIntent
                        = PendingIntent.getBroadcast
                        (PresOptionActivity.this, 0, new Intent(SMS_DELIVERED_ACTION), 0);
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(numberString, null, reminder,
                        sentPendingIntent, deliveredPendingIntent);
            }
        });
        statusView = (TextView)findViewById(R.id.textView1);
        writeModeEnabled = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        // Create broadcast receivers that get notified when SMS sent
        // or delivered
        sentBroadcastReceiver = new SMSSentBroadcastReceiver();
        registerReceiver(sentBroadcastReceiver,
                new IntentFilter(SMS_SENT_ACTION));
        deliveredBroadcastReceiver = new SMSDeliveredBroadcastReceiver();
        registerReceiver(deliveredBroadcastReceiver,
                new IntentFilter(SMS_DELIVERED_ACTION));
    }

    @Override
    public void onPause()
    {
        super.onPause();
        disableWriteMode();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (sentBroadcastReceiver != null)
            unregisterReceiver(sentBroadcastReceiver);
        if (deliveredBroadcastReceiver != null)
            unregisterReceiver(deliveredBroadcastReceiver);
    }

    @Override
    public void onNewIntent(Intent intent) {
        if(writeModeEnabled) {
            writeModeEnabled = false;
            // Write to newly scanned tag
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            writeTag(tag);
        }
    }

    private boolean writeTag(Tag tag) {

        // record that contains our custom data from textfield, using custom MIME_TYPE
        String textToSend = getIntent().getStringExtra("hash");
        byte[] payload = textToSend.getBytes();
        byte[] mimeBytes = "application/pharmacusdis.prescription".getBytes(Charset.forName("US-ASCII"));
        NdefRecord record = new NdefRecord(NdefRecord.TNF_MIME_MEDIA, mimeBytes,
                new byte[0], payload);
        NdefMessage message = new NdefMessage(new NdefRecord[] { record});

        try {
            // see if tag is already NDEF formatted
            Ndef ndef = Ndef.get(tag);
            if (ndef != null) {
                ndef.connect();

                if (!ndef.isWritable()) {
                    statusView.setText("Read-only tag");
                    return false;
                }

                // work out how much space we need for the data
                int size = message.toByteArray().length;
                if (ndef.getMaxSize() < size) {
                    statusView.setText("Tag doesn't have enough free space");
                    return false;
                }

                ndef.writeNdefMessage(message);
                statusView.setText("Tag written successfully.");
                return true;
            } else {
                // attempt to format tag
                NdefFormatable format = NdefFormatable.get(tag);
                if (format != null) {
                    try {
                        format.connect();
                        format.format(message);
                        statusView.setText("Tag written successfully!\nClose this app and scan tag.");
                        return true;
                    } catch (IOException e) {
                        statusView.setText("Unable to format tag to NDEF.");
                        return false;
                    }
                } else {
                    statusView.setText("Tag doesn't appear to support NDEF format.");
                    return false;
                }
            }
        } catch (Exception e) {
            statusView.setText("Failed to write tag");
        }
        return false;
    }

    private void enableWriteMode() {
        writeModeEnabled = true;

        // set up a PendingIntent to open the app when a tag is scanned
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        IntentFilter[] filters = new IntentFilter[] { tagDetected };

        nfcAdapter.enableForegroundDispatch(this, pendingIntent, filters, null);
    }

    private void disableWriteMode() {
        nfcAdapter.disableForegroundDispatch(this);
    }
}
