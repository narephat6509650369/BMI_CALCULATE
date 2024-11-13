package com.example.assignment1;



import android.content.Intent;
import android.os.Bundle;

import android.widget.Button;
import android.widget.EditText;

import android.widget.TextView;
import android.content.SharedPreferences;
import android.widget.Toast;
import android.text.InputFilter;
import android.text.Spanned;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;


import com.google.android.material.floatingactionbutton.FloatingActionButton;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    private final DecimalFormat formatter = new DecimalFormat("#,##0.00");
    private EditText weight_input;
    private EditText height_input;
    private TextView bmi_output;
    private TextView class_output;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // เชื่อมต่อกับ FloatingActionButton สำหรับเปิดประวัติ
        FloatingActionButton historyButton = findViewById(R.id.ID_history_btn);
        historyButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ColumnActivity.class);
            startActivity(intent);
        });

        // เชื่อมต่อกับ UI
        weight_input = findViewById(R.id.IPweight);
        height_input = findViewById(R.id.IPheight);
        bmi_output = findViewById(R.id.Show_bmi);
        class_output = findViewById(R.id.Show_class);
        Button calculate_btn = findViewById(R.id.IDcalculate);

        weight_input.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(8, 2)});
        height_input.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(8, 2)});

        calculate_btn.setOnClickListener(view -> {
            String weightStr = weight_input.getText().toString();
            String heightStr = height_input.getText().toString();

            try {
                double weightInKg = Double.parseDouble(weightStr);
                double heightInCm = Double.parseDouble(heightStr);
                double heightInMeters = heightInCm / 100.0;

                double bmi = weightInKg / (heightInMeters * heightInMeters);
                String formattedBmi = formatter.format(bmi);
                bmi_output.setText(formattedBmi);

                String classification = getBmiClassification(bmi);
                class_output.setText(classification);

                // บันทึกข้อมูลใน SharedPreferences
                saveDataToSharedPreferences(formattedBmi, weightStr, classification);

            } catch (NumberFormatException e) {
                Toast.makeText(view.getContext(), "กรุณาใส่ตัวเลขที่ถูกต้องสำหรับน้ำหนักและส่วนสูง.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ฟังก์ชันบันทึกข้อมูลลง SharedPreferences
    private void saveDataToSharedPreferences(String weight, String bmi, String classification) {
        SharedPreferences sharedPreferences = getSharedPreferences("BMIHistory", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        String currentDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        String dataString = sharedPreferences.getString("history", "");

        // เพิ่มข้อมูลใหม่
        dataString = currentDate + "|" + weight + "|" + bmi + "|" + classification + ";" + dataString;
        editor.putString("history", dataString);
        editor.apply();
    }

    private String getBmiClassification(double bmi) {
        Locale currentLocale = Locale.getDefault();
        boolean isThai = currentLocale.getLanguage().equals("th");

        class_output.setBackgroundResource(R.drawable.ic_box_design);

        if (bmi < 16) {
            class_output.setBackgroundColor(ContextCompat.getColor(this, R.color.severe_thinness));
            return isThai ? "ผอมมาก" : "Severe Thinness";
        } else if (bmi >= 16 && bmi < 17) {
            class_output.setBackgroundColor(ContextCompat.getColor(this, R.color.moderate_thinness));
            return isThai ? "ผอมปานกลาง" : "Moderate Thinness";
        } else if (bmi >= 17 && bmi < 18.5) {
            class_output.setBackgroundColor(ContextCompat.getColor(this, R.color.mild_thinness));
            return isThai ? "ผอมเล็กน้อย" : "Mild Thinness";
        } else if (bmi >= 18.5 && bmi < 25) {
            class_output.setBackgroundColor(ContextCompat.getColor(this, R.color.normal));
            return isThai ? "ปกติ" : "Normal";
        } else if (bmi >= 25 && bmi < 30) {
            class_output.setBackgroundColor(ContextCompat.getColor(this, R.color.overweight));
            return isThai ? "น้ำหนักเกิน" : "Overweight";
        } else if (bmi >= 30 && bmi < 35) {
            class_output.setBackgroundColor(ContextCompat.getColor(this, R.color.obese_class_1));
            return isThai ? "อ้วนชนิดที่ 1" : "Obese Class I";
        } else if (bmi >= 35 && bmi < 40) {
            class_output.setBackgroundColor(ContextCompat.getColor(this, R.color.obese_class_2));
            return isThai ? "อ้วนชนิดที่ 2" : "Obese Class II";
        } else {
            class_output.setBackgroundColor(ContextCompat.getColor(this, R.color.obese_class_3));
            return isThai ? "อ้วนชนิดที่ 3" : "Obese Class III";
        }
    }

    static class DecimalDigitsInputFilter implements InputFilter {
        private final Pattern mPattern;

        DecimalDigitsInputFilter(int digits, int digitsAfterZero) {
            mPattern = Pattern.compile("^(\\d{0," + (digits - digitsAfterZero) + "})(\\.\\d{0," + digitsAfterZero + "})?$");
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            String input = dest.subSequence(0, dstart) + source.toString() + dest.subSequence(dend, dest.length());
            Matcher matcher = mPattern.matcher(input);
            return matcher.matches() ? null : "";
        }
    }
}
