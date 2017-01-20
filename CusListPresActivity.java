package com.example.kenjiquik.specialistcusdis;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.kenjiquik.specialistcusdis.SQLRequest.GetGCRequest;
import com.example.kenjiquik.specialistcusdis.prescriptionTools.PrescriptionAdapter;
import com.example.kenjiquik.specialistcusdis.prescriptionTools.PrescriptionBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Kenji Quik (I.D 1304332) & Matthew Cho (I.D 188515)
 */

public class CusListPresActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    JSONObject jsonObject;
    JSONArray jsonArray;
    PrescriptionAdapter adapter;
    ListView listView;
    String GC, GCphone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cus_list_pres);
        listView = (ListView) findViewById(R.id.listCusView);
        adapter = new PrescriptionAdapter(this, R.layout.row_layout);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

        Intent intent = getIntent();
        String json_string = intent.getExtras().getString("jsonData");
        String first = intent.getExtras().getString("fname");
        String last = intent.getExtras().getString("lname");
        try {
            jsonObject = new JSONObject(json_string);
            jsonArray = jsonObject.getJSONArray("server_response");
            int count = 0;
            String name, medicine, phone, hash;
            while(count < jsonArray.length()) {
                JSONObject JC = jsonArray.getJSONObject(count);
                name = JC.getString("full_name");
                phone = JC.getString("phone");
                medicine = JC.getString("medicine");
                hash = JC.getString("hash");
                GC = JC.getString("approved");
                if(name.equals(first + " " + last)) {
                    PrescriptionBean bean = new PrescriptionBean(GC, medicine, "", hash);
                    adapter.add(bean);
                }
                count ++;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    GCphone = jsonResponse.getString("phone");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        GetGCRequest request = new GetGCRequest(GC, responseListener);
        RequestQueue queue = Volley.newRequestQueue(CusListPresActivity.this);
        queue.add(request);

        Intent intent = new Intent(CusListPresActivity.this, PresCusOptionActivity.class);
        PrescriptionBean retrieveBean = (PrescriptionBean)adapter.getItem(position);
        intent.putExtra("fname", retrieveBean.getName());
        intent.putExtra("phone", GCphone);
        intent.putExtra("medicine", retrieveBean.getMedicine());
        intent.putExtra("hash", retrieveBean.getHash());
        CusListPresActivity.this.startActivity(intent);
    }
}
