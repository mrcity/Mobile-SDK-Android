/**
 * Activity of Searching Bluetooth Printers
 *
 * @author Brother Industries, Ltd.
 * @version 2.2
 */

package com.dji.sdk.sample.skyspotter;

import android.annotation.TargetApi;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.brother.ptouch.sdk.NetPrinter;
import com.dji.sdk.sample.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Activity_BluetoothPrinterList extends ListActivity {

    private NetPrinter[] mBluetoothPrinter; // array of storing Printer information
    private ArrayList<String> mItems = null; // List of storing the printer's information

    /**
     * initialize activity
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_netprinterlist);

        Button btnRefresh = (Button) findViewById(R.id.btnRefresh);
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refreshButtonOnClick();

            }
        });


        Button btPrinterSettings = (Button) findViewById(R.id.btPrinterSettings);
        btPrinterSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settingsButtonOnClick();

            }
        });


        getPairedPrinters();
        this.setTitle(R.string.bluetooth_printer_list_title_label);
    }

    /**
     * Called when [Settings] button is tapped
     */
    private void settingsButtonOnClick() {
        Intent bluetoothSettings = new Intent(
                android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
        startActivityForResult(bluetoothSettings,
                Common.ACTION_BLUETOOTH_SETTINGS);
    }

    /**
     * Called when [Refresh] button is tapped
     */
    private void refreshButtonOnClick() {
        getPairedPrinters();

    }

    /**
     * Called when the Settings activity exits
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Common.ACTION_BLUETOOTH_SETTINGS) {
            getPairedPrinters();
        }
    }

    /**
     * get paired printers
     */
    private void getPairedPrinters() {
        // get the BluetoothAdapter
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter
                .getDefaultAdapter();
        if (bluetoothAdapter != null) {
            if (!bluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(
                        BluetoothAdapter.ACTION_REQUEST_ENABLE);
                enableBtIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(enableBtIntent);
            }
        } else {
            return;
        }

        try {
            if (mItems != null) {
                mItems.clear();
            }
            mItems = new ArrayList<String>();

			/*
             * if the paired devices exist, set the paired devices else set the
			 * string of "No Bluetooth Printer."
			 */
			List<BluetoothDevice> pairedDevices = getPairedBluetoothDevice(bluetoothAdapter);
            if (pairedDevices.size() > 0) {
                mBluetoothPrinter = new NetPrinter[pairedDevices.size()];
                int i = 0;
                for (BluetoothDevice device : pairedDevices) {
                    String strDev = "";
                    strDev += device.getName() + "\n" + device.getAddress();
                    mItems.add(strDev);

                    mBluetoothPrinter[i] = new NetPrinter();
                    mBluetoothPrinter[i].ipAddress = "";
                    mBluetoothPrinter[i].macAddress = device.getAddress();
                    mBluetoothPrinter[i].modelName = device.getName();
                    i++;
                }
            } else {
                mItems.add(getString(R.string.no_bluetooth_device));
            }
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ArrayAdapter<String> fileList = new ArrayAdapter<String>(
                            Activity_BluetoothPrinterList.this,
                            android.R.layout.test_list_item, mItems);
                    Activity_BluetoothPrinterList.this.setListAdapter(fileList);
                }
            });
        } catch (Exception ignored) {
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private List<BluetoothDevice> getPairedBluetoothDevice(BluetoothAdapter bluetoothAdapter) {
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if (pairedDevices == null || pairedDevices.size() == 0) {
            return new ArrayList<>();
        }
        ArrayList<BluetoothDevice> devices = new ArrayList<>();
        for (BluetoothDevice device : pairedDevices) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                devices.add(device);
            }
            else {
                if (device.getType() != BluetoothDevice.DEVICE_TYPE_LE) {
                    devices.add(device);
                }
            }
        }

        return devices;
    }

    /**
     * Called when an item in the list is tapped
     */
    @Override
    protected void onListItemClick(ListView listView, View view, int position,
                                   long id) {

        final String item = (String) getListAdapter().getItem(position);
        if (!item.equalsIgnoreCase(getString(R.string.no_bluetooth_device))) {
            // send the selected printer info. to Settings Activity and close
            // the current Activity
            final Intent settings = new Intent(this, Activity_Settings.class);
            settings.putExtra("ipAddress",
                    mBluetoothPrinter[position].ipAddress);
            settings.putExtra("macAddress",
                    mBluetoothPrinter[position].macAddress);
            settings.putExtra("localName", "");
            settings.putExtra("printer", mBluetoothPrinter[position].modelName);
            setResult(RESULT_OK, settings);
        }
        finish();
    }

}