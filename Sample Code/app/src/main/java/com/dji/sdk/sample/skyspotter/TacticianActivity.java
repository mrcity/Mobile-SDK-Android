package com.dji.sdk.sample.skyspotter;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.brother.ptouch.sdk.Printer;
import com.brother.ptouch.sdk.PrinterInfo;
import com.dji.sdk.sample.R;

// From https://github.com/kenglxn/QRGen
import net.glxn.qrgen.android.QRCode;

public class TacticianActivity extends AppCompatActivity {

    Button printQrCodes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tactician);

        printQrCodes = (Button) findViewById(R.id.btn_print_qr_codes);

        printQrCodes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                printBitmap(generateQrCode());
            }
        });
    }

    private Bitmap generateQrCode() {
        Bitmap myBitmap = QRCode.from("www.example.org").bitmap();
        return myBitmap;
    }

    private void printBitmap(Bitmap b) {
        Printer myPrinter = new Printer();
        PrinterInfo ptInfo = myPrinter.getPrinterInfo();
        myPrinter.setPrinterInfo(ptInfo);
        myPrinter.startCommunication();
        myPrinter.printImage(b);
        myPrinter.endCommunication();
    }
}
