package com.vnrvjiet.tsraksha;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;

import static com.vnrvjiet.tsraksha.LocaleUtils.ENG;
import static com.vnrvjiet.tsraksha.LocaleUtils.HIN;
import static com.vnrvjiet.tsraksha.LocaleUtils.TEL;

public class SelectLanguage {

    private Context context;


    public SelectLanguage(Context context) {
        this.context = context;
    }

    public void changeLanguage() {
        final AlertDialog dialog;
        final AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomAlertDialog);
        View languageView = LayoutInflater.from(context).inflate(R.layout.language_selection, null);
        final Button selectEnglish = languageView.findViewById(R.id.select_english);
        Button selectHindi = languageView.findViewById(R.id.select_hindi);
        Button selectTelugu = languageView.findViewById(R.id.select_telugu);
        builder.setView(languageView);
        builder.setCancelable(true);
        dialog = builder.create();
        selectEnglish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                setLanguage(ENG);
            }
        });
        selectHindi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                setLanguage(HIN);
            }
        });
        selectTelugu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                setLanguage(TEL);
            }
        });
        dialog.show();
    }

    public void setLanguage(String language) {
        LocaleUtils.setSelectedLanguageId(language);
        Intent i = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }
}
