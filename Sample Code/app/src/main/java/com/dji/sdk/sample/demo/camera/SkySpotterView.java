package com.dji.sdk.sample.demo.camera;

import android.app.Service;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.Image;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.dji.sdk.sample.R;
import com.dji.sdk.sample.internal.controller.DJISampleApplication;
import com.dji.sdk.sample.internal.utils.Helper;
import com.dji.sdk.sample.internal.utils.ToastUtils;
import com.dji.sdk.sample.internal.utils.VideoFeedView;
import com.dji.sdk.sample.internal.view.PresentableView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetector;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import dji.sdk.base.BaseProduct;
import dji.sdk.camera.VideoFeeder;
import dji.sdk.sdkmanager.DJISDKManager;
import dji.sdk.sdkmanager.LiveStreamManager;

/**
 * Class for live stream demo.
 *
 * @author Hoker
 * @date 2019/1/28
 * <p>
 * Copyright (c) 2019, DJI All Rights Reserved.
 */
public class SkySpotterView extends LinearLayout implements PresentableView {

    private VideoFeedView primaryVideoFeedView;
    private Paint boxPaint, linePaint, textPaint;
    private LiveStreamManager.LiveStreamVideoSource currentVideoSource = LiveStreamManager.LiveStreamVideoSource.Primary;
    private boolean isProcessingFrame = false;

    public SkySpotterView(Context context) {
        super(context);
        initUI(context);
    }

    private void initUI(Context context) {
        setClickable(true);
        setOrientation(VERTICAL);
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
        layoutInflater.inflate(R.layout.view_sky_spotter, this, true);

        // My stuff
        final ImageView frameView = (ImageView) findViewById(R.id.frame_view);

        FirebaseVisionBarcodeDetectorOptions options =
                new FirebaseVisionBarcodeDetectorOptions.Builder()
                        .setBarcodeFormats(
                                FirebaseVisionBarcode.FORMAT_QR_CODE)
                        .build();

        final FirebaseVisionBarcodeDetector detector = FirebaseVision.getInstance()
                .getVisionBarcodeDetector();

        boxPaint = new Paint();
        boxPaint.setStyle(Paint.Style.STROKE);
        boxPaint.setColor(Color.YELLOW);
        boxPaint.setStrokeWidth(24.0f);
        linePaint = new Paint();
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setColor(Color.RED);
        linePaint.setStrokeWidth(24.0f);
        textPaint = new Paint();
        textPaint.setTextSize(160.0f);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setColor(Color.CYAN);
        // end my stuff mostly except for the listener stuff

        primaryVideoFeedView = (VideoFeedView) findViewById(R.id.video_view_primary_video_feed);
        primaryVideoFeedView.registerLiveVideo(VideoFeeder.getInstance().getPrimaryVideoFeed(), true);
        primaryVideoFeedView.setCustomObjectListener(new VideoFeedView.MyCustomObjectListener() {
            @Override
            public void onBitmapReady(Bitmap bitmap) {
                if (!isProcessingFrame) {
                    isProcessingFrame = true;
                    Log.d(DJISampleApplication.TAG, "Looking for QR Code in image " + bitmap.getWidth() + " x " + bitmap.getHeight());
                    final FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
                    final Bitmap boxes = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
                    final Canvas boxCanvas = new Canvas(boxes);
                    Task<List<FirebaseVisionBarcode>> result = detector.detectInImage(image)
                            .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionBarcode>>() {
                                @Override
                                public void onSuccess(List<FirebaseVisionBarcode> barcodes) {
                                    // Task completed successfully
//                                    Log.d(DJISampleApplication.TAG, "Success! Found " + barcodes.size() + " barcodes");
                                    for (FirebaseVisionBarcode barcode : barcodes) {
                                        String qrValue = barcode.getRawValue();
                                        Rect bounds = barcode.getBoundingBox();
                                        Point[] corners = barcode.getCornerPoints();
//                                        Log.d(DJISampleApplication.TAG, "Barcode found! [" + qrValue + "] at [" + bounds.toShortString()
//                                                + "], or CW from top left [" + corners[0] + ", "  + corners[1] + ", "  + corners[2] + ", "  + corners[3] + "]");
                                        boxCanvas.drawRect(bounds, boxPaint);
                                        Path p = new Path();
                                        p.moveTo(corners[corners.length - 1].x, corners[corners.length - 1].y);
                                        for (int i = 0; i < corners.length; i++){
                                            p.lineTo(corners[i].x, corners[i].y);
                                        }
                                        boxCanvas.drawPath(p, linePaint);
                                        boxCanvas.drawText(qrValue, (bounds.left + bounds.right) / 2.0f, (bounds.top + bounds.bottom) / 2.0f, textPaint);
                                    }
                                    isProcessingFrame = false;
                                    frameView.setImageBitmap(boxes);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Task failed with an exception
                                    e.printStackTrace();
                                    isProcessingFrame = false;
                                }
                            });
                }
            }
        });
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        BaseProduct product = DJISampleApplication.getProductInstance();
        if (product == null || !product.isConnected()) {
            ToastUtils.setResultToToast("Disconnect");
            return;
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    @Override
    public int getDescription() {
        return R.string.component_listview_live_stream;
    }

    @NonNull
    @Override
    public String getHint() {
        return this.getClass().getSimpleName() + ".java";
    }
}
