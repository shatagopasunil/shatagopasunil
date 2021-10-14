package com.sunil45.crimeregadmin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.util.ArrayList;

public class ShowPieChartActivity extends AppCompatActivity {

    private PieChart pieChart;
    private ArrayList<MyPair> arrayList;
    private LinearLayout labelColorLayout, addCasesLayout;
    private TextView pieDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_pie_chart_activity);
        labelColorLayout = findViewById(R.id.addLabelsColors);
        addCasesLayout = findViewById(R.id.add_cases_report);
        pieDate = findViewById(R.id.pie_date);
        pieDate.append(getIntent().getStringExtra("year"));
        long total = getIntent().getLongExtra("total", 0);
        arrayList = (ArrayList<MyPair>) getIntent().getSerializableExtra("arrayList");
        pieChart = findViewById(R.id.pie_chart);
        setData(total);
    }

    private void setData(long total) {
        int n = arrayList.size();
        ArrayList<Integer> colors = new ArrayList<>();
        for(int i = 0; i < n; ++i){
            colors.add(((int)(Math.random()*16777215)) | (0xFF << 24));
        }
        int color;
        long cases;
        String key;
        float percent;
        for(int i = 0; i < n; ++i){
            color = colors.get(i);
            key = arrayList.get(i).getKey();
            cases = arrayList.get(i).getValue();
            percent = (float) (Math.round((cases * 100f/ total) * 100.0) / 100.0);
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View _itemRow = inflater.inflate(R.layout.add_label_color, null);
            ((View)_itemRow.findViewById(R.id.label_color)).setBackgroundColor(color);
            ((TextView)_itemRow.findViewById(R.id.label_text)).setText(key);
            labelColorLayout.addView(_itemRow);
            final View view1 = inflater.inflate(R.layout.single_case_report, null);
            ((TextView)view1.findViewById(R.id.case_percent)).setText(String.valueOf(percent) + "%");
            ((TextView)view1.findViewById(R.id.case_count)).setText(String.valueOf(cases));
            ((TextView)view1.findViewById(R.id.case_pincode)).setText(key);
            addCasesLayout.addView(view1);
            pieChart.addPieSlice(new PieModel(key, percent , color));
        }

    }
}