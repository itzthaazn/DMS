package com.example.kenjiquik.specialistcusdis;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.kenjiquik.specialistcusdis.prescriptionTools.PrescriptionAdapter;
import com.example.kenjiquik.specialistcusdis.prescriptionTools.PrescriptionBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Kenji Quik (I.D 1304332) & Matthew Cho (I.D 188515)
 */

public class ListPresActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{
    JSONObject jsonObject;
    JSONArray jsonArray;
    PrescriptionAdapter adapter;
    ListView listView;
    String GC;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_pres);
        listView = (ListView) findViewById(R.id.listView);
        adapter = new PrescriptionAdapter(this, R.layout.row_layout);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

        Intent intent = getIntent();
        String json_string = intent.getExtras().getString("jsonData");
        String signed = intent.getExtras().getString("approvedBy");
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
                if(signed.equals(JC.getString("approved"))) {
                    PrescriptionBean bean = new PrescriptionBean(name, medicine, phone, hash);
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
        Intent intent = new Intent(ListPresActivity.this, PresOptionActivity.class);
        PrescriptionBean retrieveBean = (PrescriptionBean)adapter.getItem(position);
        intent.putExtra("fname", retrieveBean.getName());
        intent.putExtra("phone", retrieveBean.getPhone());
        intent.putExtra("medicine", retrieveBean.getMedicine());
        intent.putExtra("GC", GC);
        intent.putExtra("hash", retrieveBean.getHash());
        ListPresActivity.this.startActivity(intent);
    }
}
