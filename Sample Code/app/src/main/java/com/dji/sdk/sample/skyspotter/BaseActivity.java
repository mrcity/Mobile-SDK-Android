/**
 * Base Activity for printing
 *
 * @author Brother Industries, Ltd.
 * @version 2.2
 */

package com.dji.sdk.sample.skyspotter;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Message;
import android.view.KeyEvent;

import com.brother.ptouch.sdk.PrinterInfo;
import com.dji.sdk.sample.R;

public abstract class BaseActivity extends Activity {

    static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @TargetApi(12)
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    if (intent.getBooleanExtra(
                            UsbManager.EXTRA_PERMISSION_GRANTED, false))
                        Common.mUsbRequest = 1;
                    else
                        Common.mUsbRequest = 2;
                }
            }
        }
    };
    BasePrint myPrint = null;
    MsgHandle mHandle;
    MsgDialog mDialog;

    public abstract void selectFileButtonOnClick();

    public abstract void printButtonOnClick();

    /**
     * Called when [Printer Settings] button is tapped
     */
    void printerSettingsButtonOnClick() {
        startActivity(new Intent(this, Activity_Settings.class));
    }

    /**
     * show message when BACK key is clicked
     */
    @Override
    public boolean onKeyDown(final int keyCode, final KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            showTips();
        }
        return false;
    }

    /**
     * show the BACK message
     */
    private void showTips() {

        final AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle(R.string.end_title)
                .setMessage(R.string.end_message)
                .setCancelable(false)
                .setPositiveButton(R.string.button_ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialog,
                                                final int which) {

                                finish();
                            }
                        })
                .setNegativeButton(R.string.button_cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialog,
                                                final int which) {
                            }
                        }).create();
        //alertDialog.show();
    }

    /**
     * get the BluetoothAdapter
     */
    BluetoothAdapter getBluetoothAdapter() {
        final BluetoothAdapter bluetoothAdapter = BluetoothAdapter
                .getDefaultAdapter();
        if (bluetoothAdapter != null && !bluetoothAdapter.isEnabled()) {
            final Intent enableBtIntent = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
            enableBtIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(enableBtIntent);
        }
        return bluetoothAdapter;
    }

    @TargetApi(12)
    UsbDevice getUsbDevice(UsbManager usbManager) {
        if (myPrint.getPrinterInfo().port != PrinterInfo.Port.USB) {
            return null;
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR1) {
            Message msg = mHandle.obtainMessage(Common.MSG_WRONG_OS);
            mHandle.sendMessage(msg);
            return null;
        }
        UsbDevice usbDevice = myPrint.getUsbDevice(usbManager);
        if (usbDevice == null) {
            Message msg = mHandle.obtainMessage(Common.MSG_NO_USB);
            mHandle.sendMessage(msg);
            return null;
        }

        return usbDevice;
    }

    @TargetApi(12)
    boolean checkUSB() {
        if (myPrint.getPrinterInfo().port != PrinterInfo.Port.USB) {
            return true;
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR1) {
            Message msg = mHandle.obtainMessage(Common.MSG_WRONG_OS);
            mHandle.sendMessage(msg);
            return false;
        }
        UsbManager usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        UsbDevice usbDevice = myPrint.getUsbDevice(usbManager);
        if (usbDevice == null) {
            Message msg = mHandle.obtainMessage(Common.MSG_NO_USB);
            mHandle.sendMessage(msg);
            return false;
        }
        PendingIntent permissionIntent = PendingIntent.getBroadcast(this, 0,
                new Intent(ACTION_USB_PERMISSION), 0);
        registerReceiver(mUsbReceiver, new IntentFilter(ACTION_USB_PERMISSION));
        if (!usbManager.hasPermission(usbDevice)) {
            Common.mUsbRequest = 0;
            usbManager.requestPermission(usbDevice, permissionIntent);
        } else {
            Common.mUsbRequest = 1;
        }
        return true;
    }
}