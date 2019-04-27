/**
 * Activity of printing image/prn files
 *
 * @author Brother Industries, Ltd.
 * @version two.two
 */
package com.dji.sdk.sample.skyspotter;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.brother.ptouch.sdk.PrinterInfo;
import com.dji.sdk.sample.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class Activity_PrintImagesForQrCodes extends BaseActivity {

    // this to retrieve the list of QR code to generate and allow the user to print
    // this is modified from Brother's Activity_PrintImages

    private final ArrayList<String> mFiles = new ArrayList<String>();
    private ImageView mImageView;
    private Button mBtnPrint;
    private Button mMultiPrint;

    public static final String QR_CODE_VALUE_BUNDLE_KEY = "QR_CODE_VALUE_BUNDLE_KEY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_image);

        Intent intent = getIntent();
        if (intent != null) {
            String qrCodes = intent.getStringExtra(QR_CODE_VALUE_BUNDLE_KEY);
            final String[] qrCodesList = qrCodes.split(",");

            mFiles.clear();

            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {

                    for (String qrCode : qrCodesList) {
                        if (qrCode != null && !qrCode.isEmpty() && !qrCode.equals("")) {

                            Log.d("qrcoderrrr", qrCode);
                            String externalStorageDirectory = Environment.getExternalStorageDirectory().toString();

                            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                            try {
                                BitMatrix bitMatrix = multiFormatWriter.encode(qrCode, BarcodeFormat.QR_CODE, 600, 600);
                                BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                                Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);

                                File file = new File(externalStorageDirectory + "/Download", qrCode + ".jpg");
                                FileOutputStream fileOutputStream = null;
                                try {
                                    fileOutputStream = new FileOutputStream(file);
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                                try {
                                    fileOutputStream.flush();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    fileOutputStream.close();
                                    //mFiles.clear();
                                    mFiles.add(file.getAbsolutePath());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                            } catch (WriterException e) {
                                e.printStackTrace();
                            }

                            String str = "";
                            for (int i = 0; i < mFiles.size(); i++) {
                                str = str + mFiles.get(i) + "\n";
                            }
                            final String selectFiles = str;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // add to
                                    TextView tvSelectedFiles = (TextView) findViewById(R.id.tvSelectedFiles);
                                    tvSelectedFiles.setText(selectFiles);
                                }
                            });

                        }
                    }

                }
            });

        }


        Button btnSelectFile = (Button) findViewById(R.id.btnSelectFile);
        btnSelectFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectFileButtonOnClick();
            }
        });
        btnSelectFile.setEnabled(false);

        Button btnPrinterSettings = (Button) findViewById(R.id.btnPrinterSettings);
        btnPrinterSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                printerSettingsButtonOnClick();

            }
        });

        Button btnPrinterStatus = (Button) findViewById(R.id.btnPrinterStatus);
        btnPrinterStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                printerStatusButtonOnClick();

            }
        });
        Button btnSendFile = (Button) findViewById(R.id.btnSendFile);
        btnSendFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendFileButtonOnClick();

            }
        });
        btnSendFile.setEnabled(false);

        mMultiPrint = (Button) findViewById(R.id.btnMultiPrint);
        mMultiPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                printMultiFileButtonOnClick();

            }
        });

        mMultiPrint.setEnabled(true);

        // initialization for Activity
        mBtnPrint = (Button) findViewById(R.id.btnPrint);
        mBtnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                printButtonOnClick();

            }
        });

        mBtnPrint.setEnabled(false);

        CheckBox chkMutilSelect = (CheckBox) this.findViewById(R.id.chkMultipleSelect);
        chkMutilSelect.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton arg0,
                                         boolean arg1) {
                //showMultiSelect(arg1); //
            }
        });
        chkMutilSelect.setChecked(true);
        chkMutilSelect.setEnabled(false);

        mImageView = (ImageView) this.findViewById(R.id.imageView);

        // get data from other application by way of intent sending
        final Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String file = extras.getString(Common.INTENT_FILE_NAME);
            setDisplayFile(file);
            mBtnPrint.setEnabled(true);
        }

        // initialization for printing
        mDialog = new MsgDialog(this);
        mHandle = new MsgHandle(this, mDialog);
        myPrint = new ImagePrint(this, mHandle, mDialog);

        // when use bluetooth print set the adapter
        BluetoothAdapter bluetoothAdapter = super.getBluetoothAdapter();
        myPrint.setBluetoothAdapter(bluetoothAdapter);
    }

    /**
     * Called when [select file] button is tapped
     */
    @Override
    public void selectFileButtonOnClick() {

        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(this);

        // call File Explorer Activity to select a image or prn file
        final String imagePrnPath = prefs.getString(
                Common.PREFES_IMAGE_PRN_PATH, "");
        final Intent fileList = new Intent(Activity_PrintImagesForQrCodes.this,
                Activity_FileList.class);
        fileList.putExtra(Common.INTENT_TYPE_FLAG, Common.FILE_SELECT_PRN_IMAGE);
        fileList.putExtra(Common.INTENT_FILE_NAME, imagePrnPath);
        startActivityForResult(fileList, Common.FILE_SELECT_PRN_IMAGE);
    }

    /**
     * Called when [Print] button is tapped
     */
    @Override
    public void printButtonOnClick() {
        // set the printing data
        ((ImagePrint) myPrint).setFiles(mFiles);

        if (!checkUSB())
            return;

        // call function to print
        myPrint.print();
    }

    /**
     * Called when [Print] button is tapped
     */
    public void printMultiFileButtonOnClick() {
        myPrint = new MultiImagePrint(this, mHandle, mDialog);

        // set the printing data
        ((MultiImagePrint) myPrint).setFiles(mFiles);

        if (!checkUSB())
            return;

        // call function to print
        myPrint.print();

        myPrint = new ImagePrint(this, mHandle, mDialog);
    }

    /**
     * Called when [Printer Status] button is tapped
     */
    private void printerStatusButtonOnClick() {
        if (!checkUSB())
            return;
        myPrint.getPrinterStatus();
    }

    /**
     * Called when [Printer Status] button is tapped
     */
    private void sendFileButtonOnClick() {

        // set the printing data
        ((ImagePrint) myPrint).setFiles(mFiles);

        if (!checkUSB())
            return;

        sendFile();
    }


    /**
     * Launch the thread to print
     */
    private void sendFile() {


        SendFileThread getTread = new SendFileThread();
        getTread.start();
    }

    /**
     * Called when an activity you launched exits, giving you the requestCode
     * you started it with, the resultCode it returned, and any additional data
     * from it.
     */
    @Override
    protected void onActivityResult(final int requestCode,
                                    final int resultCode, final Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        // set the image/prn file
        if (resultCode == RESULT_OK
                && requestCode == Common.FILE_SELECT_PRN_IMAGE) {
            final String strRtn = data.getStringExtra(Common.INTENT_FILE_NAME);
            setImageOrPrnFile(strRtn);
        }
    }

    /**
     * set the image/prn file
     */
    private void setImageOrPrnFile(String file) {
        CheckBox chkMultiSelect = (CheckBox) this
                .findViewById(R.id.chkMultipleSelect);
        TextView tvSelectedFiles = (TextView) findViewById(R.id.tvSelectedFiles);

        if (chkMultiSelect.isChecked()) {
            if (!mFiles.contains(file)) {
                mFiles.add(file);

                int count = mFiles.size();
                String str = "";
                for (int i = 0; i < count; i++) {
                    str = str + mFiles.get(i) + "\n";
                }
                tvSelectedFiles.setText(str);
            }
        } else {
            setDisplayFile(file);
        }
        mMultiPrint.setEnabled(true);
        mBtnPrint.setEnabled(true);
    }

    /**
     * set the selected file to display
     */
    @SuppressWarnings("deprecation")
    private void setDisplayFile(String file) {
        mFiles.clear();
        mFiles.add(file);

        ((TextView) findViewById(R.id.tvSelectedFiles)).setText(file);
        if (Common.isImageFile(file)) {

            WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
            Display display = windowManager.getDefaultDisplay();
            int displayWidth = display.getWidth();
            int displayHeight = display.getHeight();
            Button btnMultiPrint = (Button) findViewById(R.id.btnMultiPrint);

            int[] location = new int[2];
            btnMultiPrint.getLocationOnScreen(location);

            int height = displayHeight - location[1]
                    - btnMultiPrint.getHeight();
            Bitmap mBitmap = Common.fileToBitmap(file, displayWidth, height);

            mImageView.setImageBitmap(mBitmap);
        } else {
            mImageView.setImageBitmap(null);
        }
    }

    /**
     * set the status of controls when the [Multi Select] CheckBox is checked or
     * not
     */
    private void showMultiSelect(boolean isVisible) {
        mFiles.clear();
        mBtnPrint.setEnabled(false);
        mMultiPrint.setEnabled(false);

        TextView tvSelectedFiles = (TextView) findViewById(R.id.tvSelectedFiles);
        tvSelectedFiles.setText("");

        if (isVisible) {
            //mImageView.setImageBitmap(null);
        }
    }

    /**
     * Thread for getting the printer's status
     */
    private class SendFileThread extends Thread {
        @Override
        public void run() {

            // set info. for printing
            myPrint.setPrinterInfo();

            // start message
            Message msg = mHandle.obtainMessage(Common.MSG_PRINT_START);
            mHandle.sendMessage(msg);

            myPrint.getPrinter().startCommunication();

            int count = ((ImagePrint) myPrint).getFiles().size();

            for (int i = 0; i < count; i++) {

                String strFile = ((ImagePrint) myPrint).getFiles().get(i);

                myPrint.setPrintResult(myPrint.getPrinter().sendBinaryFile(strFile));

                // if error, stop print next files
                if (myPrint.getPrintResult().errorCode != PrinterInfo.ErrorCode.ERROR_NONE) {
                    break;
                }
            }


            myPrint.getPrinter().endCommunication();

            // end message
            mHandle.setResult(myPrint.showResult());
            mHandle.setBattery(myPrint.getBatteryDetail());
            msg = mHandle.obtainMessage(Common.MSG_PRINT_END);
            mHandle.sendMessage(msg);

        }
    }
}
