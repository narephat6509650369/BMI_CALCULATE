package com.example.assignment1;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.HashMap;


public class ColumnActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_column);

        // เชื่อมต่อกับ ListView และ TextView
        ListView listView = findViewById(R.id.listView);
        TextView textView = findViewById(R.id.IDmyhis);

        // โหลดข้อมูลจาก SharedPreferences
        ArrayList<HashMap<String, String>> MyArrList = loadDataFromSharedPreferences();

        // ตั้งค่า SimpleAdapter เพื่อแสดงผลข้อมูลใน ListView
        SimpleAdapter simpleAdapter = new SimpleAdapter(
                this,
                MyArrList,
                R.layout.activity_column, // เปลี่ยนเป็น layout สำหรับแต่ละรายการใน ListView
                new String[]{"trans_id", "name", "msg", "amt"},
                new int[]{R.id.col_trans_id, R.id.col_name, R.id.col_msg, R.id.col_amt}
        );

        // ตรวจสอบว่ามีข้อมูลหรือไม่
        if (MyArrList.isEmpty()) {
            // ถ้าข้อมูลว่าง แสดงข้อความว่าไม่มีประวัติ
            textView.setVisibility(View.VISIBLE); // แสดง TextView
            listView.setVisibility(View.GONE); // ซ่อน ListView
        } else {
            // ถ้ามีข้อมูล แสดงประวัติ
            listView.setAdapter(simpleAdapter);
            listView.setVisibility(View.VISIBLE); // แสดง ListView
            textView.setVisibility(View.VISIBLE); // แสดง TextView
        }
    }

    // ฟังก์ชันโหลดข้อมูลจาก SharedPreferences
    private ArrayList<HashMap<String, String>> loadDataFromSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("BMIHistory", MODE_PRIVATE);
        String dataString = sharedPreferences.getString("history", "");
        ArrayList<HashMap<String, String>> data = new ArrayList<>();

        if (!dataString.isEmpty()) {
            String[] items = dataString.split(";"); // แยกแต่ละรายการ
            for (String itemString : items) {
                String[] itemData = itemString.split("\\|"); // แยกข้อมูลในแต่ละรายการ
                if (itemData.length == 4) {
                    HashMap<String, String> item = new HashMap<>();
                    item.put("trans_id", itemData[0]);
                    item.put("name", itemData[1]);
                    item.put("msg", itemData[2]);
                    item.put("amt", itemData[3]);
                    data.add(item);
                }
            }
        }

        return data;
    }
}
