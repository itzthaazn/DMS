package com.example.kenjiquik.specialistcusdis;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class SpecialistActivity extends AppCompatActivity {

    private String fname, lname, username, phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specialist);

        Intent intent = getIntent();

        // Obtain User Information from login
        fname = intent.getStringExtra("fname");
        lname = intent.getStringExtra("lname");
        username = intent.getStringExtra("username");
        phone = intent.getStringExtra("phone");

        TextView getName = (TextView) findViewById(R.id.viewName);
        TextView getUsername = (TextView) findViewById(R.id.viewUsername);
        TextView getPhone = (TextView) findViewById(R.id.viewPhone);

        // Display user details
        if (getName != null) {
            getName.setText("Welcome, Dr." + lname);
        }
        if (getUsername != null) {
            getUsername.setText("Username: " + username);
        }
        if (getPhone != null) {
            getPhone.setText("Phone: " + phone);
        }

        Button createPresButton = (Button) findViewById(R.id.createPresButton);
        createPresButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent CreatePresIntent = new Intent(SpecialistActivity.this, CreatePresActivity.class);
                CreatePresIntent.putExtra("approvedBy", "Dr." + fname + " " + lname);
                SpecialistActivity.this.startActivity(CreatePresIntent);
            }
        });

        Button listPresButton = (Button) findViewById(R.id.ListPresButton);
        listPresButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new BackgroundTask().execute();
            }
        });
    }

    // Background Task that retrieve the list of prescription
    private class BackgroundTask extends AsyncTask<Void,Void,String> {

        public String json_url;
        String JSON_String;
        @Override
        protected void onPreExecute() {
            json_url = "http://pharmacusdis.netai.net/ListPrescription.php";
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                URL url = new URL(json_url);
                HttpURLConnection httpConnect = (HttpURLConnection)url.openConnection();
                InputStream iStream = httpConnect.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(iStream));
                StringBuilder builder = new StringBuilder();
                while((JSON_String = reader.readLine()) != null) {
                    builder.append(JSON_String + "\n");
                }
                reader.close();
                iStream.close();
                httpConnect.disconnect();
                return builder.toString().trim();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result) {
            String jsonList = result;
            Intent toDisplay = new Intent(SpecialistActivity.this, ListPresActivity.class);
            toDisplay.putExtra("jsonData", jsonList);
            toDisplay.putExtra("approvedBy", "Dr." + fname + " " + lname);
            startActivity(toDisplay);
        }
    }
}
