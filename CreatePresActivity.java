package com.example.kenjiquik.specialistcusdis;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.kenjiquik.specialistcusdis.SQLRequest.RegisterPrescriptionRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Date;
import java.util.Calendar;

/**
 * Created by Kenji Quik (I.D 1304332) & Matthew Cho (I.D 188515)
 */

public class CreatePresActivity extends AppCompatActivity {

    private int year1, month1, day1;
    private int year2, month2, day2;
    static final int START_DIALOG_ID = 0, EXPIRY_DIALOG_ID = 1;
    private Button setStartDate, setExpiryDate;
    private Date sdate, edate;

    private static final long FNV_64_INIT = 0xcbf29ce484222325L;
    private static final long FNV_64_PRIME = 0x100000001b3L;

    private String spec_fname, spec_lname, spec_fullname, spec_username, spec_phone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_pres);

        Intent intent = getIntent();
        spec_fullname = intent.getStringExtra("approvedBy");
        final Calendar cal = Calendar.getInstance();
        year1 = cal.get(Calendar.YEAR);
        month1 = cal.get(Calendar.MONTH);
        day1 = cal.get(Calendar.DAY_OF_MONTH);
        year2 = cal.get(Calendar.YEAR);
        month2 = cal.get(Calendar.MONTH);
        day2 = cal.get(Calendar.DAY_OF_MONTH) + 1;

        setStartDate = (Button) findViewById(R.id.setStartButton);
        setStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(START_DIALOG_ID);
            }
        });
        setExpiryDate = (Button) findViewById(R.id.setExpiryButton);
        setExpiryDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(EXPIRY_DIALOG_ID);
            }
        });

        final EditText fnameTxtField = (EditText) findViewById(R.id.regFullName);
        final EditText phoneTxtField = (EditText) findViewById(R.id.regPatientPhone);
        final EditText ageField = (EditText) findViewById(R.id.regAge);
        final EditText insuranceField = (EditText) findViewById(R.id.insuranceType);
        final EditText medicineField = (EditText) findViewById(R.id.presMedicine);

        Button presRegister = (Button) findViewById(R.id.CreatePrescriptionButton);
        assert presRegister != null;
        presRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String fname = fnameTxtField.getText().toString();
                final String phone = phoneTxtField.getText().toString();
                final int age = Integer.parseInt(ageField.getText().toString());
                final String insuranceType = insuranceField.getText().toString();
                final String medicine = medicineField.getText().toString();
                final String approvedBy = spec_fullname;
                final String startDate = sdate.toString();
                final String expiryDate = edate.toString();
                final String combine = fname + phone + age + insuranceType + medicine + approvedBy;
                final String FNVHash = "" + FNVhash64(combine);

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            if (success) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(CreatePresActivity.this);
                                builder.setMessage("Prescription Registered")
                                        .setNegativeButton("Great!", null)
                                        .create()
                                        .show();
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(CreatePresActivity.this);
                                builder.setMessage("Register Failed")
                                        .setNegativeButton("Retry", null)
                                        .create()
                                        .show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                RegisterPrescriptionRequest request = new RegisterPrescriptionRequest(fname, phone, age, insuranceType, medicine, approvedBy, startDate, expiryDate, FNVHash, responseListener);
                RequestQueue queue = Volley.newRequestQueue(CreatePresActivity.this);
                queue.add(request);
            }
        });
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if(id == START_DIALOG_ID) {
            return new DatePickerDialog(this, sdatePickerListener, year1, month1, day1);
        } else if (id == EXPIRY_DIALOG_ID) {
            return new DatePickerDialog(this, edatePickerListener, year2, month2, day2);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener sdatePickerListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            year1 = year;
            month1 = monthOfYear;
            day1 = dayOfMonth;
            sdate = new Date(year1 - 1900, month1, day1);
            setStartDate.setText(sdate.toString());
        }
    };

    private DatePickerDialog.OnDateSetListener edatePickerListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            year2 = year;
            month2 = monthOfYear;
            day2 = dayOfMonth;
            edate = new Date(year2 - 1900, month2, day2);
            setExpiryDate.setText(edate.toString());
        }
    };

    public static long FNVhash64(final String k) {
        long rv = FNV_64_INIT;
        final int len = k.length();
        for(int i = 0; i < len; i++) {
            rv ^= k.charAt(i);
            rv *= FNV_64_PRIME;
        }
        return rv;
    }
}
