package com.example.kenjiquik.specialistcusdis;

import android.app.AlertDialog;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.kenjiquik.specialistcusdis.SQLRequest.GetPresRequest;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Kenji Quik (I.D 1304332) & Matthew Cho (I.D 188515)
 */

public class NFCTaggedActivity extends AppCompatActivity {

    private String hash;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfctagged);

        final TextView fnameView = (TextView) findViewById(R.id.nameView);
        final TextView phoneView = (TextView) findViewById(R.id.phoneView);
        final TextView medicineView = (TextView) findViewById(R.id.medicineView);
        final TextView GCView = (TextView) findViewById(R.id.GCView);
        final TextView startView = (TextView) findViewById(R.id.startView);
        final TextView expiryView = (TextView) findViewById(R.id.expiryView);
        final TextView hashView = (TextView) findViewById(R.id.hashView);

        Intent intent = getIntent();
        //Check mime type, get ndef message  from intent and display the message in text view
        if(intent.getType() != null && intent.getType().equals("application/pharmacusdis.prescription")) {
            Parcelable[] rawMsgs = getIntent().getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage msg = (NdefMessage) rawMsgs[0];
            NdefRecord record = msg.getRecords()[0];
            hash = new String(record.getPayload());
            hashView.setText("Hash Found: " + hash);
        }

        Button getPresButton = (Button) findViewById(R.id.identifyButton);
        assert getPresButton != null;
        getPresButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Response received from the server
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            if (success) {
                                // Obtain user information from the json response
                                String patient_id = jsonResponse.getString("patient_id");
                                String fname = jsonResponse.getString("full_name");
                                String medicine = jsonResponse.getString("medicine");
                                String phone = jsonResponse.getString("phone");
                                String approved = jsonResponse.getString("approved");
                                String start = jsonResponse.getString("start");
                                String expiry = jsonResponse.getString("expiry");
                                fnameView.setText("Full name: " + fname);
                                medicineView.setText("Medicine: " + medicine);
                                phoneView.setText("Phone No.: " + phone);
                                GCView.setText("GC: " + approved);
                                startView.setText("Start Date: " + start);
                                expiryView.setText("Expiry Date: " + expiry);
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(NFCTaggedActivity.this);
                                builder.setMessage("Prescription Not Found")
                                        .setNegativeButton("Retry", null)
                                        .create()
                                        .show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };

                GetPresRequest request = new GetPresRequest(hash, responseListener);
                RequestQueue queue = Volley.newRequestQueue(NFCTaggedActivity.this);
                queue.add(request);
            }
        });
    }
}
