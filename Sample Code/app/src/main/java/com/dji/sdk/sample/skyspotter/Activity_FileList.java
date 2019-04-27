/**
 * File Explorer Activity for selecting Files and saving files.
 *
 * @author Brother Industries, Ltd.
 * @version 2.2
 */

package com.dji.sdk.sample.skyspotter;

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dji.sdk.sample.R;

import java.io.File;
import java.util.ArrayList;

@SuppressWarnings("ALL")
public class Activity_FileList extends ListActivity {

    private TextView tvFileName = null; // TextView for displaying the file path
    private int mType; // type flag
    private ArrayList<String> mItems = null; // items of folder or file
    private SharedPreferences mPrefs; // SharedPreferences
    private String mDirPath = ""; // file path

    /**
     * initialize activity
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filelist);


        Button btnFileListOk = (Button) findViewById(R.id.btnFileListOk);
        btnFileListOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonOkClicked();

            }
        });
        // set button enable
        btnFileListOk.setEnabled(false);

        Button btnFileListCancel = (Button) findViewById(R.id.btnFileListCancel);
        btnFileListCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonCancelClicked(view);
            }
        });


        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        // get typeFlag and fileName from LbxMain Activity.
        final Bundle extras = getIntent().getExtras();
        mType = extras.getInt(Common.INTENT_TYPE_FLAG);

        final String filePath = extras.getString(Common.INTENT_FILE_NAME);


        tvFileName = (TextView) findViewById(R.id.tvFolderName);


        if ((filePath == null) || (filePath.equals(""))) {
            mDirPath = Environment.getExternalStorageDirectory().toString();
        } else {
            mDirPath = getParentDirPath(filePath);
        }

        // list file and folder in the current folder
        fillList();
    }

    /**
     * Called when OK button is clicked. Send the selected file and the
     * RESULT_OK flag to Main Activity.
     */
    private void buttonOkClicked() {

        // store the select file's path
        saveFilePath(tvFileName.getText().toString());

		/* return to the previous Activity */
        final Intent returnIntent = new Intent();
        returnIntent.putExtra(Common.INTENT_FILE_NAME, tvFileName.getText()
                .toString());
        setResult(RESULT_OK, returnIntent);

        finish();
    }

    /**
     * save the file to SharedPreferences for storing
     */
    private void saveFilePath(final String fileName) {

        final SharedPreferences.Editor editor = mPrefs.edit();
        switch (mType) {
            case Common.FILE_SELECT_PRN_IMAGE: // save the prn/image file path
                editor.putString(Common.PREFES_IMAGE_PRN_PATH, fileName);
                break;

            case Common.FILE_SELECT_PDZ: // save the pdz file path
                editor.putString(Common.PREFES_PDZ_PATH, fileName);
                break;

            case Common.FILE_SELECT_PDF: // save the pdf file path
                editor.putString(Common.PREFES_PDF_PATH, fileName);
                break;
            case Common.FOLDER_SELECT: // save the pdf file path
                editor.putString(Common.PREFES_SAVE_FOLDER, fileName);
                break;
            default:
                break;
        }

        editor.commit();
    }

    /**
     * Called when Cancel button is clicked.
     */
    private void buttonCancelClicked(final View view) {

        finish();

    }

    /**
     * This method will be called when an item in the list is selected.
     * Subclasses should override. Subclasses can call
     * getListView().getItemAtPosition(position) if they need to access the data
     * associated with the selected item.
     *
     * @param listView : The ListView where the click happened .
     * @param view     : The view that was clicked within the ListView.
     * @param position : The position of the view in the list.
     * @param id       : The row id of the item that was clicked.
     * @return
     */
    @Override
    protected void onListItemClick(ListView listView, View view, int position,
                                   long id) {

        super.onListItemClick(listView, view, position, id);

        final String strItem = (String) getListAdapter().getItem(position);
        final Button buttonOk = (Button) findViewById(R.id.btnFileListOk);

		/*
         * if it is .. , then go back to the parent folder else if it is a
		 * folder, go into the folder else it is a file, set the file path to
		 * View Control
		 */
        if (strItem.equals("..")) {
            mDirPath = getParentDirPath(mDirPath);
            buttonOk.setEnabled(false);
            fillList();
        } else if (strItem.substring(strItem.length() - 1).equals(
                File.separator)) {

            if (mDirPath.equals(File.separator)) {
                mDirPath += strItem;
            } else {
                mDirPath = mDirPath + File.separator + strItem;
            }

            mDirPath = mDirPath.substring(0, mDirPath.length() - 1);
            if (mType != Common.FOLDER_SELECT) {
                buttonOk.setEnabled(false);
            } else {
                buttonOk.setEnabled(true);

            }
            fillList();
        } else {
            tvFileName.setText(mDirPath + File.separator + strItem);
            buttonOk.setEnabled(true);
        }
    }

    /**
     * list file and folder in the current folder
     */
    private void fillList() {

        // no file
        final File[] files = new File(mDirPath).listFiles();
        if (files == null) {
            Toast.makeText(this, getString(R.string.unable_access),
                    Toast.LENGTH_SHORT).show();
            mDirPath = getParentDirPath(mDirPath);
            return;
        }

        tvFileName.setText(mDirPath);

        if (mItems != null) {
            mItems.clear();
        }
        mItems = new ArrayList<String>();

        if (!mDirPath.equals(File.separator) && !mDirPath.equals("/mnt")) {
            mItems.add("..");
        }

        // list up the files and folders
        for (File file : files) {

			/* if folders, else files */
            if (file.isDirectory()) {
                mItems.add(file.getName() + File.separator);
            } else {
                // get file extension
                final String filename = file.getName();

                if ((mType == Common.FILE_SELECT_PRN_IMAGE)
                        && (Common.isImageFile(filename) || Common
                        .isPrnFile(filename)) || Common.isBinFile(filename)) {
                    mItems.add(filename);
                } else if ((mType == Common.FILE_SELECT_PDZ)
                        && (Common.isTemplateFile(filename))) {
                    mItems.add(filename);
                } else if ((mType == Common.FILE_SELECT_PDF)
                        && (Common.isPdfFile(filename))) {
                    mItems.add(filename);
                }
            }
        }

        // show the new file list
        final ArrayAdapter<String> fileList = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, mItems);
        setListAdapter(fileList);
    }

    /**
     * get the filePath's parent directory or the file's current directory
     *
     * @param strPath : file path
     * @return parent directory
     */
    private String getParentDirPath(String strPath) {

        if (strPath.lastIndexOf(File.separator) <= 0) {
            return strPath
                    .substring(0, strPath.lastIndexOf(File.separator) + 1);
        } else {
            return strPath.substring(0, strPath.lastIndexOf(File.separator));
        }
    }
}
