package com.example.kenjiquik.specialistcusdis;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.kenjiquik.specialistcusdis.SQLRequest.RegisterSpecRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {

    private String role = "Customer";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final EditText phoneTxtField = (EditText) findViewById(R.id.regPhone);
        final EditText fnameTxtField = (EditText) findViewById(R.id.regFirstName);
        final EditText lnameTxtField = (EditText) findViewById(R.id.regLastname);
        final EditText usernameTxtField = (EditText) findViewById(R.id.regUsername);
        final EditText passwordTxtField = (EditText) findViewById(R.id.regPassword);
        final Switch roleSwitch = (Switch)findViewById(R.id.switch1);
        roleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    role = "Health Specialist";
                } else {
                    role = "Customer";
                }
            }
        });
        final Button bRegister = (Button) findViewById(R.id.bRegister);

        bRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = usernameTxtField.getText().toString();
                final String fname = fnameTxtField.getText().toString();
                final String lname = lnameTxtField.getText().toString();
                final String phone = phoneTxtField.getText().toString();
                final String password = passwordTxtField.getText().toString();

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            if (success) {
                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                RegisterActivity.this.startActivity(intent);
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                builder.setMessage("Register Failed").setNegativeButton("Retry", null).create().show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };

                RegisterSpecRequest registerUserRequest = new RegisterSpecRequest(fname, lname, username, phone, password, role, responseListener);
                RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
                queue.add(registerUserRequest);
            }
        });
    }
}
