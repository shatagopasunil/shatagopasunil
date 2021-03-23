package com.vnrvjiet.tsraksha;


import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class ContributeFragment extends Fragment {
    private Resources res;

    public ContributeFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contribute, container, false);
        Context context = getContext();
        Application.getInstance().initAppLanguage(context);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.show();
        res = getResources();
        actionBar.setTitle(res.getString(R.string.contribute));
        initializeFields(view);
        return view;
    }

    private void initializeFields(final View view) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        dialog.setPositiveButton(res.getString(R.string.ok),null);
        dialog.setCancelable(true);
        view.findViewById(R.id.donate_cheque).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.setView(null);
                dialog.setTitle(res.getString(R.string.offline_donate));
                dialog.setMessage(res.getString(R.string.offline_address) +
                        "\n\nCM Relief Fund,\nBRKR Bhavan,\nTelangana Secretariat Hyderabad,\n500022.");
                dialog.show();
            }
        });
        view.findViewById(R.id.donate_website).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uriUrl = Uri.parse("https://cmrf.tsonline.gov.in/");
                Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                startActivity(launchBrowser);
            }
        });
        view.findViewById(R.id.scan_and_pay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.setMessage(null);
                dialog.setTitle(null);
                View view1 = LayoutInflater.from(getContext()).inflate(R.layout.contribute_scan_and_pay,null);
                dialog.setView(view1);
                dialog.show();
            }
        });
        view.findViewById(R.id.account_details).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.setView(null);
                dialog.setTitle(res.getString(R.string.online_donate));
                dialog.setMessage("Account Number: 62354157651\n" +
                        "Account Name: CM RELIEF FUND\n" +
                        "Bank Name: State Bank of India\n" +
                        "Branch: Secretariat, Hyderabad, Telangana\n" +
                        "Branch Code: 020077\n" +
                        "IFSC Code: SBIN0020077");
                dialog.show();
            }
        });
    }
}
