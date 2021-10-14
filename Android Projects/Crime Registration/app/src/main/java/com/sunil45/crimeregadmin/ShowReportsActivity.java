package com.sunil45.crimeregadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class ShowReportsActivity extends AppCompatActivity {
    private LinearLayout reportLayout;
    private DatabaseReference reference;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_reports);
        reportLayout = findViewById(R.id.report_layout);
        loadingBar = new ProgressDialog(this);
        loadingBar.setCancelable(false);
        loadingBar.setMessage("Please wait while fetching reports..");
        loadingBar.show();
        reference = FirebaseDatabase.getInstance().getReference().child("Report");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long t_count = snapshot.getChildrenCount(), i = 0;
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    showReport(dataSnapshot, dataSnapshot.getKey());
                    ++i;
                    if(i == t_count){
                        loadingBar.dismiss();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showReport(DataSnapshot dataSnapshot, String s) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View _itemRow = inflater.inflate(R.layout.new_row, null);
        ((TextView) _itemRow.findViewById(R.id.show_year)).setText(s);
        ImageView openCloseImage = _itemRow.findViewById(R.id.open_close_image);
        LinearLayout openCloseMonth = _itemRow.findViewById(R.id.open_close_month);
        ((LinearLayout) _itemRow.findViewById(R.id.open_close_click)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(openCloseMonth.getVisibility() == View.VISIBLE){
                    openCloseMonth.setVisibility(View.GONE);
                    openCloseImage.setImageResource(R.drawable.close_details);
                }else{
                    openCloseMonth.setVisibility(View.VISIBLE);
                    openCloseImage.setImageResource(R.drawable.open_details);

                }
            }
        });
        ((Button)_itemRow.findViewById(R.id.jan_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPie(0, s, (String) ((Button)view).getText());
            }
        });
        ((Button)_itemRow.findViewById(R.id.feb_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPie(1, s, (String) ((Button)view).getText());
            }
        });
        ((Button)_itemRow.findViewById(R.id.mar_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPie(2, s, (String) ((Button)view).getText());
            }
        });
        ((Button)_itemRow.findViewById(R.id.apr_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPie(3, s, (String) ((Button)view).getText());
            }
        });
        ((Button)_itemRow.findViewById(R.id.may_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPie(4, s, (String) ((Button)view).getText());
            }
        });
        ((Button)_itemRow.findViewById(R.id.june_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPie(5, s, (String) ((Button)view).getText());
            }
        });
        ((Button)_itemRow.findViewById(R.id.july_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPie(6, s, (String) ((Button)view).getText());
            }
        });((Button)_itemRow.findViewById(R.id.aug_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPie(7, s, (String) ((Button)view).getText());
            }
        });
        ((Button)_itemRow.findViewById(R.id.sep_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPie(8, s, (String) ((Button)view).getText());
            }
        });
        ((Button)_itemRow.findViewById(R.id.oct_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPie(9, s, (String) ((Button)view).getText());
            }
        });((Button)_itemRow.findViewById(R.id.nov_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPie(10, s, (String) ((Button)view).getText());
            }
        });
        ((Button)_itemRow.findViewById(R.id.dec_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPie(11, s, (String) ((Button)view).getText());
            }
        });
        reportLayout.addView(_itemRow);
    }

    private void showPie(int i, String year, String month) {
        loadingBar.setMessage("Please wait");
        reference.child(year).child(String.valueOf(i)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()){
                    Toast.makeText(ShowReportsActivity.this, "No complaints found", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                    return;
                }
                ArrayList<MyPair> arrayList = new ArrayList<>();
                long i = 0, t_count = snapshot.getChildrenCount(), res = 0;
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    ++i;
                    res += dataSnapshot.getValue(Long.class);
                    arrayList.add(new MyPair(dataSnapshot.getKey(), dataSnapshot.getValue(Long.class)));
                    if(i == t_count){
                        loadingBar.dismiss();
                        startActivity(new Intent(ShowReportsActivity.this, ShowPieChartActivity.class).putExtra("year", month + " - " + year)
                                .putExtra("arrayList", (Serializable)arrayList).putExtra("total", res));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}