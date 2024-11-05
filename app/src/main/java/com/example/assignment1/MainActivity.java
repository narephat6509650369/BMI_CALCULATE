package com.example.assignment1;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.text.InputFilter;
import android.text.Spanned;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;


import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    private final DecimalFormat formatter = new DecimalFormat("#,##0.00");
    private EditText weight_input;
    private EditText height_input;
    private TextView bmi_output; // TextView สำหรับแสดงผล BMI
    private TextView class_output; // TextView สำหรับแสดงผลประเภท

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // ตั้งค่า layout ของคุณ

        // เชื่อมต่อกับ UI
        weight_input = findViewById(R.id.IPweight);
        height_input = findViewById(R.id.IPheight);
        bmi_output = findViewById(R.id.Show_bmi);
        class_output = findViewById(R.id.Show_class);
        Button calculate_btn = findViewById(R.id.IDcalculate);

        // ตั้งค่า InputFilter สำหรับ EditText
        weight_input.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(8, 2)});
        height_input.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(8, 2)});

        calculate_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // รับค่าน้ำหนักและส่วนสูงที่ป้อน
                String weightStr = weight_input.getText().toString();
                String heightStr = height_input.getText().toString();

                try {
                    // แปลงน้ำหนักและส่วนสูงเป็นตัวเลข
                    double weightInKg = Double.parseDouble(weightStr);
                    double heightInCm = Double.parseDouble(heightStr);

                    // แปลงส่วนสูงจากเซนติเมตรเป็นเมตร
                    double heightInMeters = heightInCm / 100.0;

                    // คำนวณ BMI โดยใช้กิโลกรัมและเมตร
                    double bmi = weightInKg / (heightInMeters * heightInMeters);

                    // แสดงผลลัพธ์ BMI ใน TextView โดยใช้ formatter
                    String formattedBmi = formatter.format(bmi);
                    bmi_output.setText(formattedBmi);

                    // กำหนดประเภท BMI
                    String classification = getBmiClassification(bmi);
                    class_output.setText(classification);


                } catch (NumberFormatException e) {
                    // จัดการกับกรณีที่มีการป้อนข้อมูลที่ไม่ถูกต้อง
                    Toast.makeText(view.getContext(), "กรุณาใส่ตัวเลขที่ถูกต้องสำหรับน้ำหนักและส่วนสูง.", Toast.LENGTH_SHORT).show();
                }
            }
        });
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
            if (!matcher.matches()) {
                return ""; // คืนค่าว่างหากไม่ตรงตาม Pattern
            }
            return null; // คืนค่า null หากค่าถูกต้อง
        }
    }
}