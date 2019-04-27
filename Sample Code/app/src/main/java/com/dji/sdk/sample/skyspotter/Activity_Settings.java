/**
 * Activity of printer settings
 *
 * @author Brother Industries, Ltd.
 * @version 2.2
 */

package com.dji.sdk.sample.skyspotter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;

import com.dji.sdk.sample.R;

import java.io.File;
import java.util.Arrays;

public class Activity_Settings extends PreferenceActivity implements
        OnPreferenceChangeListener {

    private SharedPreferences sharedPreferences;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // initialize the printerModel ListPreference
        ListPreference printerModelPreference = (ListPreference) getPreferenceScreen()
                .findPreference("printerModel");
        printerModelPreference.setEntryValues(PrinterModelInfo.getModelNames());
        printerModelPreference.setEntries(PrinterModelInfo.getModelNames());


        // initialize the settings
        setPreferenceValue("printerModel");
        String printerModel = sharedPreferences.getString("printerModel", "");

        // set paper size & port information
        printerModelChange(printerModel);

        setPreferenceValue("port");
        setEditValue("address");
        setEditValue("macAddress");
        setPreferenceValue("paperSize");
        setPreferenceValue("orientation");
        setEditValue("numberOfCopies");
        setPreferenceValue("halftone");
        setPreferenceValue("printMode");

        setPreferenceValue("overwrite");
        setPreferenceValue("mode9");
        setPreferenceValue("dashLine");

        setPreferenceValue("pjSpeed");
        setPreferenceValue("pjPaperKind");

        setPreferenceValue("printerCase");
        setPreferenceValue("skipStatusCheck");
        setPreferenceValue("checkPrintEnd");
        setPreferenceValue("printQuality");

        setEditValue("imageThresholding");
        setEditValue("scaleValue");

        setPreferenceValue("pjCarbon");
        setPreferenceValue("pjDensity");
        setPreferenceValue("pjFeedMode");
        setPreferenceValue("align");
        setEditValue("leftMargin");
        setPreferenceValue("valign");

        setEditValue("topMargin");
        setEditValue("customPaperWidth");
        setEditValue("customPaperLength");
        setEditValue("customFeed");
        setPreferenceValue("customSetting");
        setPreferenceValue("paperPosition");
        // initialize the custom paper size's settings
        File newdir = new File(Common.CUSTOM_PAPER_FOLDER);
        if (!newdir.exists()) {
            newdir.mkdir();
        }
        File[] files = new File(Common.CUSTOM_PAPER_FOLDER).listFiles();
        String[] entries = new String[files.length];
        String[] entryValues = new String[files.length];
        int i = 0;
        for (File file : files) {
            String filename = file.getName();
            String extention = filename.substring(
                    filename.lastIndexOf(".", filename.length()) + 1,
                    filename.length());
            if (extention.equalsIgnoreCase("bin")) {
                entries[i] = filename;
                entryValues[i] = filename;
                i++;
            }
        }
        Arrays.sort(entries);
        Arrays.sort(entryValues);

        ListPreference customSettingPreference = (ListPreference) getPreferenceScreen()
                .findPreference("customSetting");
        customSettingPreference.setEntries(entries);
        customSettingPreference.setEntryValues(entryValues);

        setPreferenceValue("rjDensity");
        setPreferenceValue("dashLine");
        setPreferenceValue("rotate180");
        setPreferenceValue("peelMode");
        setPreferenceValue("autoCut");
        setPreferenceValue("endCut");
        setPreferenceValue("specialType");
        setPreferenceValue("halfCut");
        setPreferenceValue("cutMark");
        setPreferenceValue("trimTapeAfterData");


        setEditValue("labelMargin");
        // initialization for printer
        PreferenceScreen printerPreference = (PreferenceScreen) getPreferenceScreen()
                .findPreference("printer");

        String printer = sharedPreferences.getString("printer", "");
        if (!printer.equals("")) {
            printerPreference.setSummary(printer);
        }

        printerPreference
                .setOnPreferenceClickListener(new OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        String printerModel = sharedPreferences.getString(
                                "printerModel", "");
                        setPrinterList(printerModel);
                        return true;
                    }
                });


        // set the BackgroundForPreferenceScreens to light
        setBackgroundForPreferenceScreens("prefIpMacAddress");
        setBackgroundForPreferenceScreens("prefAlignmentSettings");
        setBackgroundForPreferenceScreens("prefPJSettings");
        setBackgroundForPreferenceScreens("prefPJTDSettings");
        setBackgroundForPreferenceScreens("prefCutSettings");

        setBackgroundForPreferenceScreens("halfToningSetting");
        setBackgroundForPreferenceScreens("scaleModelSetting");

        setSavePathPreference();

        setEditValue("processTimeout");
        setEditValue("sendTimeout");
        setEditValue("receiveTimeout");
        setEditValue("connectionTimeout");
        setEditValue("closeWaitTime");

        setPreferenceValue("softFocusing");
        setPreferenceValue("enabledTethering");
        setPreferenceValue("rawMode");
        setPreferenceValue("useLegacyHalftoneEngine");
        setWorkPathPreference();

    }

    private void setSavePathPreference() {
        PreferenceScreen savePrnPathPreference = (PreferenceScreen) getPreferenceScreen()
                .findPreference("savePrnPath");
        String savePrnPath = sharedPreferences.getString("savePrnPath", "");
        if (!savePrnPath.equals("")) {
            savePrnPathPreference.setSummary(savePrnPath);
        }

        savePrnPathPreference
                .setOnPreferenceClickListener(new OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {

                        setSavePath();
                        return true;
                    }
                });
    }

    /**
     * Called when [printer] is tapped
     */
    private void setSavePath() {

        Intent savePath = new Intent(this, Activity_SaveFile.class);
        startActivityForResult(savePath, Common.SAVE_PATH);
    }

    private void setWorkPathPreference() {

        ListPreference printerValuePreference = (ListPreference) getPreferenceScreen()
                .findPreference("workPath");

        CharSequence[] entries = {"", getFilesDir().getAbsolutePath() + "/", Environment
                .getExternalStorageDirectory().toString()
                + "/com.brother.ptouch.sdk/template/"};
        printerValuePreference.setDefaultValue("");
        printerValuePreference.setEntryValues(entries);

    }

    /**
     * Called when a Preference has been changed by the user. This is called
     * before the state of the Preference is about to be updated and before the
     * state is persisted.
     */
    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {

        if (newValue != null) {
            if (preference.getKey().equals("printerModel")) {
                String printerModel = sharedPreferences.getString(
                        "printerModel", "");
                if (printerModel.equalsIgnoreCase(newValue.toString())) {
                    return true;
                }

                // initialize if printer model is changed
                printerModelChange(newValue.toString());
                ListPreference paperSizePreference = (ListPreference) getPreferenceScreen()
                        .findPreference("paperSize");
                paperSizePreference.setValue(paperSizePreference
                        .getEntryValues()[0].toString());
                paperSizePreference.setSummary(paperSizePreference
                        .getEntryValues()[0].toString());

                ListPreference portPreference = (ListPreference) getPreferenceScreen()
                        .findPreference("port");
                portPreference.setValue(portPreference.getEntryValues()[0]
                        .toString());
                portPreference.setSummary(portPreference.getEntryValues()[0]
                        .toString());

                setChangedData();
            }

            if (preference.getKey().equals("port")) {
                setChangedData();
            }

            preference.setSummary((CharSequence) newValue);

            return true;
        }

        return false;

    }

    /**
     * Called when the searching printers activity you launched exits.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Common.PRINTER_SEARCH == requestCode) {
            EditTextPreference addressPreference = (EditTextPreference) getPreferenceScreen()
                    .findPreference("address");
            EditTextPreference macAddressPreference = (EditTextPreference) getPreferenceScreen()
                    .findPreference("macAddress");
            PreferenceScreen printerPreference = (PreferenceScreen) getPreferenceScreen()
                    .findPreference("printer");

            if (resultCode == RESULT_OK) {
                // IP address
                String ipAddress = data.getStringExtra("ipAddress");
                addressPreference.setText(ipAddress);
                if (ipAddress.equalsIgnoreCase("")) {
                    ipAddress = getString(R.string.address_value);
                }
                addressPreference.setSummary(ipAddress);

                // MAC address
                String macAddress = data.getStringExtra("macAddress");
                macAddressPreference.setText(macAddress);
                macAddressPreference.setSummary(macAddress);

                // Printer name
                printerPreference.setSummary(data.getStringExtra("printer"));

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("printer", data.getStringExtra("printer"));
                editor.putString("localName", data.getStringExtra("localName"));
                editor.apply();
            }
        } else if (Common.SAVE_PATH == requestCode) {
            if (resultCode == RESULT_OK) {
                PreferenceScreen saveFilePreference = (PreferenceScreen) getPreferenceScreen()
                        .findPreference("savePrnPath");

                saveFilePreference.setSummary(data
                        .getStringExtra("savePrnPath"));
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("savePrnPath",
                        data.getStringExtra("savePrnPath"));
                editor.apply();
            }
        }
    }

    /**
     * set data of a particular ListPreference
     */
    private void setPreferenceValue(String value) {
        String data = sharedPreferences.getString(value, "");

        ListPreference printerValuePreference = (ListPreference) getPreferenceScreen()
                .findPreference(value);
        printerValuePreference.setOnPreferenceChangeListener(this);
        if (!data.equals("")) {
            printerValuePreference.setSummary(data);
        }
    }

    /**
     * set data of a particular EditTextPreference
     */
    private void setEditValue(String value) {
        String name = sharedPreferences.getString(value, "");
        EditTextPreference printerValuePreference = (EditTextPreference) getPreferenceScreen()
                .findPreference(value);
        printerValuePreference.setOnPreferenceChangeListener(this);

        if (!name.equals("")) {
            printerValuePreference.setSummary(name);
        }
    }

    /**
     * Called when [printer] is tapped
     */
    private void setPrinterList(String printModel) {
        String port = sharedPreferences.getString("port", "");

        // call the Activity_NetPrinterList when port is NET
        if (port.equalsIgnoreCase("NET")) {
            Intent printerList = new Intent(this, Activity_NetPrinterList.class);
            String printTempModel = printModel.replaceAll("_", "-");
            printerList.putExtra("modelName", printTempModel);
            startActivityForResult(printerList, Common.PRINTER_SEARCH);
        } else if (port.equalsIgnoreCase("BLE")) {
            Intent printerList = new Intent(this, Activity_BLEPrinterList.class);
            startActivityForResult(printerList, Common.PRINTER_SEARCH);
        } else // call the Activity_BluetoothPrinterList when port is Bluetooth
        {
            Intent printerList = new Intent(this,
                    Activity_BluetoothPrinterList.class);
            startActivityForResult(printerList, Common.PRINTER_SEARCH);
        }
    }


    /**
     * set paper size & port information with changing printer model
     */
    private void printerModelChange(String printerModel) {

        // paper size
        ListPreference paperSizePreference = (ListPreference) getPreferenceScreen()
                .findPreference("paperSize");
        // port
        ListPreference portPreference = (ListPreference) getPreferenceScreen()
                .findPreference("port");
        if (!printerModel.equals("")) {

            String[] entryPort;
            String[] entryPaperSize;
            entryPort = PrinterModelInfo.getPortOrPaperSizeInfo(printerModel, Common.SETTINGS_PORT);
            entryPaperSize = PrinterModelInfo.getPortOrPaperSizeInfo(printerModel, Common.SETTINGS_PAPERSIZE);

            portPreference.setEntryValues(entryPort);
            portPreference.setEntries(entryPort);

            paperSizePreference.setEntryValues(entryPaperSize);
            paperSizePreference.setEntries(entryPaperSize);

        }
    }

    /**
     * initialize the address & macAddress information with changing printer
     * model or port
     */
    private void setChangedData() {
        EditTextPreference addressPreference = (EditTextPreference) getPreferenceScreen()
                .findPreference("address");
        EditTextPreference macAddressPreference = (EditTextPreference) getPreferenceScreen()
                .findPreference("macAddress");
        PreferenceScreen printerPreference = (PreferenceScreen) getPreferenceScreen()
                .findPreference("printer");

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("address", "");
        editor.putString("macAddress", "");
        editor.putString("printer", getString(R.string.printer_text));
        editor.apply();

        addressPreference.setText("");
        macAddressPreference.setText("");
        printerPreference.setSummary(getString(R.string.printer_text));
        macAddressPreference.setSummary(getString(R.string.mac_address_value));
        addressPreference.setSummary(getString(R.string.address_value));
    }

    /**
     * set the BackgroundForPreferenceScreens to light it is black when at OS
     * 2.1/2.2
     */
    private void setBackgroundForPreferenceScreens(String key) {
        PreferenceScreen preferenceScreen = (PreferenceScreen) getPreferenceScreen()
                .findPreference(key);

        preferenceScreen
                .setOnPreferenceClickListener(new OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        PreferenceScreen pref = (PreferenceScreen) preference;
                        pref.getDialog()
                                .getWindow()
                                .setBackgroundDrawableResource(
                                        android.R.drawable.screen_background_light);
                        return false;
                    }
                });
    }
}
