package com.vnrvjiet.tsrakshaadmin;


import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 */
public class UpdateCasesFragment extends Fragment {

    private EditText telConf, telAct, telRec, telDec, indConf, indAct, indRec, indDec,
            worConf, worAct, worRec, worDec;
    private String[][] cases = new String[3][4];
    private Button save,uploadDate,uploadTime;
    private DatabaseReference casesRef;
    private String[] states = {"Telangana", "India", "Global"};
    private String[] categories = {"Confirmed", "Active", "Recovered", "Deceased"};
    private Calendar calendar;
    private int mYear,mDay,mMonth,hour,minute;
    private DatePickerDialog.OnDateSetListener onDateSetListener;
    private TimePickerDialog.OnTimeSetListener onTimeSetListener;
    private String date = "",time = "";
    private String[] months = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};

    public UpdateCasesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_update_cases, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.update_cases));
        initializeFields(view);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveInfo();
                updateText();
                saveCasesToDatabase();
            }
        });
        uploadDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mYear = calendar.get(Calendar.YEAR);
                mMonth = calendar.get(Calendar.MONTH);
                mDay = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),onDateSetListener,mYear,mMonth,mDay);
                datePickerDialog.show();
            }
        });
        onDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int y, int m, int d) {
                date = d + "-" + months[m] + "-" + y;
                uploadDate.setText(date);
            }
        };
        uploadTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hour = calendar.get(Calendar.HOUR);
                minute = calendar.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),onTimeSetListener,hour,minute,false);
                timePickerDialog.show();
            }
        });
        onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                String am_pm;
                if(i < 12) {
                    am_pm = "AM";
                }
                else
                {
                    am_pm = "PM";
                    i -= 12;
                }
                if(i == 0)
                    i = 12;
                time = i +":" + i1 + " " + am_pm;
                uploadTime.setText(time);
            }
        };
        return view;
    }

    private void saveCasesToDatabase() {
        for(int i = 0; i < 3; ++i)
        {
            for(int j = 0; j < 4; ++j){
                if(!cases[i][j].isEmpty())
                casesRef.child(states[i]).child(categories[j]).setValue(cases[i][j]);
            }
        }
        if(!date.isEmpty())
        casesRef.child("Updated").child("date").setValue(date);
        if(!time.isEmpty())
        casesRef.child("Updated").child("time").setValue(time);
    }

    private void saveInfo() {
        cases[0][0] = telConf.getText().toString().trim();
        cases[0][1] = telAct.getText().toString().trim();
        cases[0][2] = telRec.getText().toString().trim();
        cases[0][3] = telDec.getText().toString().trim();
        cases[1][0] = indConf.getText().toString().trim();
        cases[1][1] = indAct.getText().toString().trim();
        cases[1][2] = indRec.getText().toString().trim();
        cases[1][3] = indDec.getText().toString().trim();
        cases[2][0] = worConf.getText().toString().trim();
        cases[2][1] = worAct.getText().toString().trim();
        cases[2][2] = worRec.getText().toString().trim();
        cases[2][3] = worDec.getText().toString().trim();
    }

    private void updateText() {
        for(int i = 0; i < 3; ++i)
        {
            for(int j = 0; j < 4; ++j)
                cases[i][j] = modify(cases[i][j]);
        }
    }

    private String modify(String value) {
        if(value.isEmpty())
            return value;
        value=value.replace(",","");
        char lastDigit=value.charAt(value.length()-1);
        String result = "";
        int len = value.length()-1;
        int nDigits = 0;

        for (int i = len - 1; i >= 0; i--)
        {
            result = value.charAt(i) + result;
            nDigits++;
            if (((nDigits % 2) == 0) && (i > 0))
            {
                result = "," + result;
            }
        }
        return (result+lastDigit);
    }


    private void initializeFields(View view) {
        telConf = view.findViewById(R.id.edit_tel_conf);
        telAct = view.findViewById(R.id.edit_tel_act);
        telRec = view.findViewById(R.id.edit_tel_rec);
        telDec = view.findViewById(R.id.edit_tel_dec);
        indConf = view.findViewById(R.id.edit_ind_conf);
        indAct = view.findViewById(R.id.edit_ind_act);
        indRec = view.findViewById(R.id.edit_ind_rec);
        indDec = view.findViewById(R.id.edit_ind_dec);
        worConf = view.findViewById(R.id.edit_wor_conf);
        worAct = view.findViewById(R.id.edit_wor_act);
        worRec = view.findViewById(R.id.edit_wor_rec);
        worDec = view.findViewById(R.id.edit_wor_dec);
        save = view.findViewById(R.id.save_cases);
        uploadDate = view.findViewById(R.id.select_date_cases);
        uploadTime = view.findViewById(R.id.select_time_cases);
        calendar = Calendar.getInstance();
        casesRef = FirebaseDatabase.getInstance().getReference().child("Upload").child("Cases");
    }
}
