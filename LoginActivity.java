package com.example.kenjiquik.specialistcusdis;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.kenjiquik.specialistcusdis.SQLRequest.LoginRequest;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Kenji Quik (I.D 1304332) & Matthew Cho (I.D 188515)
 */

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        final EditText getUsername = (EditText) findViewById(R.id.getUsername);
        final EditText getPassword = (EditText) findViewById(R.id.getPassword);
        final TextView registerLink = (TextView) findViewById(R.id.tvRegisterLink);
        final Button bLogin = (Button) findViewById(R.id.bSignIn);

        assert registerLink != null;
        registerLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                LoginActivity.this.startActivity(registerIntent);
            }
        });

        assert bLogin != null;
        bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = getUsername.getText().toString();
                final String password = getPassword.getText().toString();

                // Response received from the server
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");

                            if (success) {
                                // Obtain user information from the json response
                                String fname = jsonResponse.getString("first_name");
                                String lname = jsonResponse.getString("last_name");
                                String phone = jsonResponse.getString("phone");
                                String role = jsonResponse.getString("role");

                                if (role.equals("Health Specialist")) {
                                    Intent SpecialistIntent = new Intent(LoginActivity.this, SpecialistActivity.class);
                                    // Transfer User Information to the next Intent (Pharma Intent)
                                    SpecialistIntent.putExtra("fname", fname);
                                    SpecialistIntent.putExtra("lname", lname);
                                    SpecialistIntent.putExtra("phone", phone);
                                    SpecialistIntent.putExtra("username", username);
                                    LoginActivity.this.startActivity(SpecialistIntent);
                                } else if (role.equals("Customer")) {
                                    Intent CustomerIntent = new Intent(LoginActivity.this, CustomerActivity.class);
                                    // Transfer User Information to the next Intent (Pharma Intent)
                                    CustomerIntent.putExtra("fname", fname);
                                    CustomerIntent.putExtra("lname", lname);
                                    CustomerIntent.putExtra("phone", phone);
                                    CustomerIntent.putExtra("username", username);
                                    LoginActivity.this.startActivity(CustomerIntent);
                                }
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                builder.setMessage("Login Failed")
                                        .setNegativeButton("Retry", null)
                                        .create()
                                        .show();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };

                LoginRequest loginRequest = new LoginRequest(username, password, responseListener);
                RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
                queue.add(loginRequest);
            }
        });
    }
}
