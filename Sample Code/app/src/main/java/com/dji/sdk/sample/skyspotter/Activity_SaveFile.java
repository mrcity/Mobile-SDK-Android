/**
 * Activity of printing image/prn files
 *
 * @author Brother Industries, Ltd.
 * @version 2.2
 */
package com.dji.sdk.sample.skyspotter;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.dji.sdk.sample.R;

import java.io.File;

@SuppressWarnings("ALL")
public class Activity_SaveFile extends Activity {

    @SuppressWarnings("WeakerAccess")
    private
    TextView tvSelectedFolder;
    @SuppressWarnings("WeakerAccess")
    private
    EditText etSelectedFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_savefile_path);

        Button btnSelectFolder = (Button) findViewById(R.id.btnSelectFolder);
        btnSelectFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectFolderButtonOnClick();
            }
        });
        Button btnSetSavePrnPath = (Button) findViewById(R.id.btnSetSavePrnPath);
        btnSetSavePrnPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSavePrnPathOnClick();
            }
        });
        Button btnCancelSavePrnPath = (Button) findViewById(R.id.btnCancelSavePrnPath);
        btnCancelSavePrnPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelSavePrnPathOnClick();
            }
        });

        tvSelectedFolder = (TextView) findViewById(R.id.folderPath);
        etSelectedFile = (EditText) findViewById(R.id.savePrnPath);

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {

            SharedPreferences prefs = PreferenceManager
                    .getDefaultSharedPreferences(this);
            final String folder = prefs
                    .getString(Common.PREFES_SAVE_FOLDER, "");
            tvSelectedFolder.setText(folder);
        }

    }

    /**
     * Called when [select file] button is tapped
     */
    @SuppressWarnings("WeakerAccess")
    private void selectFolderButtonOnClick() {

        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(this);

        // call File Explorer Activity to select a image or prn file
        final String folder = prefs.getString(Common.PREFES_SAVE_FOLDER, "");
        final Intent fileList = new Intent(Activity_SaveFile.this,
                Activity_FileList.class);
        fileList.putExtra(Common.INTENT_TYPE_FLAG, Common.FOLDER_SELECT);
        fileList.putExtra(Common.INTENT_FILE_NAME, folder);
        startActivityForResult(fileList, Common.FOLDER_SELECT);
    }

    @SuppressWarnings("WeakerAccess")
    private void setSavePrnPathOnClick() {
        String fileName = etSelectedFile.getText().toString();
        String folder = (String) tvSelectedFolder.getText();
        File newdir = new File(folder);
        /* return to the previous Activity */
        final Intent returnIntent = new Intent();
        if ("".equals(fileName) || "".equals(folder)) {
            returnIntent.putExtra("savePrnPath", "");
            setResult(RESULT_OK, returnIntent);
            return;
        }
        if (newdir.exists()) {
            returnIntent.putExtra("savePrnPath", folder + "/" + fileName);
            setResult(RESULT_OK, returnIntent);

        } else {
            setResult(RESULT_CANCELED, returnIntent);

        }

        finish();

    }

    @SuppressWarnings("WeakerAccess")
    private void cancelSavePrnPathOnClick() {

        final Intent returnIntent = new Intent();
        setResult(RESULT_CANCELED, returnIntent);
        finish();

    }
}
