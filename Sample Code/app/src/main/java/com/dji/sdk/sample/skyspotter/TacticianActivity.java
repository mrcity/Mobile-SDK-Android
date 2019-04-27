package com.dji.sdk.sample.skyspotter;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.dji.sdk.sample.R;

public class TacticianActivity extends AppCompatActivity {

    // accept input for unique identifiers for each person, then bundle & send to print screen

    private EditText etPerson1;
    private EditText etPerson2;
    private EditText etPerson3;
    private EditText etPerson4;
    private EditText etPerson5;
    private EditText etPerson6;
    private EditText etPerson7;
    private EditText etPerson8;
    private Button btnGotoPrintActivity;

    public static final int STORAGE_PERMISSION_REQUEST_CODE = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tactician);

        etPerson1 = (EditText) findViewById(R.id.et_person1);
        etPerson2 = (EditText) findViewById(R.id.et_person2);
        etPerson3 = (EditText) findViewById(R.id.et_person3);
        etPerson4 = (EditText) findViewById(R.id.et_person4);
        etPerson5 = (EditText) findViewById(R.id.et_person5);
        etPerson6 = (EditText) findViewById(R.id.et_person6);
        etPerson7 = (EditText) findViewById(R.id.et_person7);
        etPerson8 = (EditText) findViewById(R.id.et_person8);
        btnGotoPrintActivity = (Button) findViewById(R.id.btn_goto_print_qr_code_activity);
        btnGotoPrintActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoPrintQrCodeActivity();
            }
        });

        isStoragePermissionGranted();
    }

    private void gotoPrintQrCodeActivity() {

        if (isStoragePermissionGranted()) {
            String persons = etPerson1.getText().toString().trim() + "," + etPerson2.getText().toString().trim() + ","
                    + etPerson3.getText().toString().trim() + "," + etPerson4.getText().toString().trim() + ","
                    + etPerson5.getText().toString().trim() + "," + etPerson6.getText().toString().trim() + ","
                    + etPerson7.getText().toString().trim() + "," + etPerson8.getText().toString().trim();


            Intent startPrintQrCodeActivity = new Intent(this, Activity_PrintImagesForQrCodes.class);
            startPrintQrCodeActivity.putExtra(Activity_PrintImagesForQrCodes.QR_CODE_VALUE_BUNDLE_KEY, persons);
            startActivity(startPrintQrCodeActivity);
        }
    }

    public boolean isStoragePermissionGranted() {

        boolean permissionGranted = false;

        if (Build.VERSION.SDK_INT >= 23) {

            int storagePermissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

            if (storagePermissionCheck == PackageManager.PERMISSION_GRANTED) {
                permissionGranted = true;
            } else {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_REQUEST_CODE);
                permissionGranted = false;
            }
        } else {

            permissionGranted = true;
        }
        return permissionGranted;
    }


}
