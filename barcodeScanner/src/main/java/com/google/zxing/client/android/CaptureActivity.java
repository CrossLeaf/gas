/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.zxing.client.android;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.Result;
import com.google.zxing.ResultMetadataType;
import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.camera.CameraManager;
import com.google.zxing.client.android.history.HistoryActivity;
import com.google.zxing.client.android.history.HistoryItem;
import com.google.zxing.client.android.history.HistoryManager;
import com.google.zxing.client.android.result.ResultButtonListener;
import com.google.zxing.client.android.result.ResultHandler;
import com.google.zxing.client.android.result.ResultHandlerFactory;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.nfc.tech.IsoDep;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * This activity opens the camera and does the actual scanning on a background thread. It draws a
 * viewfinder to help the user place the barcode correctly, shows feedback as the image processing
 * is happening, and then overlays the results when a scan is successful.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 * @author Sean Owen
 */
public final class CaptureActivity extends Activity implements SurfaceHolder.Callback {

    private static final String TAG = CaptureActivity.class.getSimpleName();

    private static final long DEFAULT_INTENT_RESULT_DURATION_MS = 1500L;
    private static final long BULK_MODE_SCAN_DELAY_MS = 1000L;

    private static final String[] ZXING_URLS = {"http://zxing.appspot.com/scan", "zxing://scan/"};

    public static final int HISTORY_REQUEST_CODE = 0x0000bacc;

    private static final Collection<ResultMetadataType> DISPLAYABLE_METADATA_TYPES =
            EnumSet.of(ResultMetadataType.ISSUE_NUMBER,
                    ResultMetadataType.SUGGESTED_PRICE,
                    ResultMetadataType.ERROR_CORRECTION_LEVEL,
                    ResultMetadataType.POSSIBLE_COUNTRY);

    private CameraManager cameraManager;
    private CaptureActivityHandler handler;
    private Result savedResultToShow;
    private ViewfinderView viewfinderView;
    private TextView statusView;
    private EditText edt_barcode;
    private Button btn_barcode;
    private View resultView;
    private Result lastResult;
    private boolean hasSurface;
    private boolean copyToClipboard;
    private IntentSource source;
    private String sourceUrl;
    private Collection<BarcodeFormat> decodeFormats;
    private Map<DecodeHintType, ?> decodeHints;
    private String characterSet;
    private InactivityTimer inactivityTimer;
    private BeepManager beepManager;
    private AmbientLightManager ambientLightManager;
    //LISE
    private HistoryManager historyManager;
    private HistoryItem metaHistoryItem;
    //private ListView listView_barcode;
    private ArrayAdapter<String> arrayAdapter_barcode;

    private String cylinders_number;
    private String cylinders_locate;
    private int flag;
    private String customer_id;
    private String car_id;
    private ArrayList<String> cylinders_list;
    private boolean asyncDone = false;
    private Toast showToastMessage;

    ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public Handler getHandler() {
        return handler;
    }

    CameraManager getCameraManager() {
        return cameraManager;
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.capture);
        String url = "http://198.245.55.221:8089/ProjectGAPP/php/show.php?tbname=cylinders";
        new Cylinders_list().execute(url);

        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);
        beepManager = new BeepManager(this);
        ambientLightManager = new AmbientLightManager(this);

        Intent intent = getIntent();
        flag = intent.getIntExtra("flag", 0) - 1;
        Log.e("tag", "flag=" + flag);

        //鋼瓶所在地
        cylinders_locate = intent.getStringExtra("locate");
        //車子ID
        car_id = intent.getStringExtra("car_id");
        //客戶ID
        customer_id = intent.getStringExtra("customer_id");

        //menu的介面
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        //LISE
//    listView_barcode = (ListView)findViewById(R.id.listView_barcode);
        arrayAdapter_barcode = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
//    listView_barcode.setAdapter(arrayAdapter_barcode);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // CameraManager must be initialized here, not in onCreate(). This is necessary because we don't
        // want to open the camera driver and measure the screen size if we're going to show the help on
        // first launch. That led to bugs where the scanning rectangle was the wrong size and partially
        // off screen.
        cameraManager = new CameraManager(getApplication());

        viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
        viewfinderView.setCameraManager(cameraManager);

        resultView = findViewById(R.id.result_view);
        statusView = (TextView) findViewById(R.id.status_view);
        edt_barcode = (EditText) findViewById(R.id.edt_barcode);
        btn_barcode = (Button) findViewById(R.id.btn_barcode);

        handler = null;
        lastResult = null;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //<LISE>
    /*if (prefs.getBoolean(PreferencesActivity.KEY_DISABLE_AUTO_ORIENTATION, true)) {
      setRequestedOrientation(getCurrentOrientation());
    } else {
      setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
    }*/
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        //</LISE>

        resetStatusView();

        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            // The activity was paused but not stopped, so the surface still exists. Therefore
            // surfaceCreated() won't be called, so init the camera here.
            initCamera(surfaceHolder);
        } else {
            // Install the callback and wait for surfaceCreated() to init the camera.
            surfaceHolder.addCallback(this);
        }

        beepManager.updatePrefs();
        ambientLightManager.start(cameraManager);

        inactivityTimer.onResume();

        Intent intent = getIntent();

        copyToClipboard = prefs.getBoolean(PreferencesActivity.KEY_COPY_TO_CLIPBOARD, true)
                && (intent == null || intent.getBooleanExtra(Intents.Scan.SAVE_HISTORY, true));

        source = IntentSource.NONE;
        sourceUrl = null;
        decodeFormats = null;
        characterSet = null;

        if (intent != null) {

            String action = intent.getAction();
            String dataString = intent.getDataString();

            if (Intents.Scan.ACTION.equals(action)) {

                // Scan the formats the intent requested, and return the result to the calling activity.
                source = IntentSource.NATIVE_APP_INTENT;
                decodeFormats = DecodeFormatManager.parseDecodeFormats(intent);
                decodeHints = DecodeHintManager.parseDecodeHints(intent);

                if (intent.hasExtra(Intents.Scan.WIDTH) && intent.hasExtra(Intents.Scan.HEIGHT)) {
                    int width = intent.getIntExtra(Intents.Scan.WIDTH, 0);
                    int height = intent.getIntExtra(Intents.Scan.HEIGHT, 0);
                    if (width > 0 && height > 0) {
                        cameraManager.setManualFramingRect(width, height);
                    }
                }

                if (intent.hasExtra(Intents.Scan.CAMERA_ID)) {
                    int cameraId = intent.getIntExtra(Intents.Scan.CAMERA_ID, -1);
                    if (cameraId >= 0) {
                        cameraManager.setManualCameraId(cameraId);
                    }
                }

                String customPromptMessage = intent.getStringExtra(Intents.Scan.PROMPT_MESSAGE);
                if (customPromptMessage != null) {
                    statusView.setText(customPromptMessage);
                }

            } else if (dataString != null &&
                    dataString.contains("http://www.google") &&
                    dataString.contains("/m/products/scan")) {

                // Scan only products and send the result to mobile Product Search.
                source = IntentSource.PRODUCT_SEARCH_LINK;
                sourceUrl = dataString;
                decodeFormats = DecodeFormatManager.PRODUCT_FORMATS;

            } else if (isZXingURL(dataString)) {

                // Scan formats requested in query string (all formats if none specified).
                // If a return URL is specified, send the results there. Otherwise, handle it ourselves.
                source = IntentSource.ZXING_LINK;
                sourceUrl = dataString;
                Uri inputUri = Uri.parse(dataString);
                decodeFormats = DecodeFormatManager.parseDecodeFormats(inputUri);
                // Allow a sub-set of the hints to be specified by the caller.
                decodeHints = DecodeHintManager.parseDecodeHints(inputUri);

            }

            characterSet = intent.getStringExtra(Intents.Scan.CHARACTER_SET);

        }

        historyManager = new HistoryManager(this); //LISE
//        renewListview(); //LISE
        if (asyncDone){
            if (showToastMessage != null) {
                showToastMessage.cancel();
                showToastMessage = Toast.makeText(this, "等待資料載入", Toast.LENGTH_LONG);
                showToastMessage.show();
            }else {
                showToastMessage = Toast.makeText(this, "等待資料載入", Toast.LENGTH_LONG);
                showToastMessage.show();
            }
        }else {
            btn_barcode.setOnClickListener(new View.OnClickListener() {
                boolean hasCylinder = false;
                @Override
                public void onClick(View v) {
                    if (!(edt_barcode.getText().toString().trim().equals(""))) {
                        cylinders_number = edt_barcode.getText().toString().trim();
                        for (int i = 0; i < cylinders_list.size(); i++) {
                            if (cylinders_number.equals(cylinders_list.get(i))) {
                                hasCylinder = true;
                                break;
                            }
                        }

                        if (hasCylinder){
                            metaHistoryItem = new HistoryItem(cylinders_number);
                            new Update().start();
                            if (showToastMessage != null) {
                                showToastMessage.cancel();
                                showToastMessage = Toast.makeText(CaptureActivity.this, "更新資料", Toast.LENGTH_SHORT);
                                showToastMessage.show();
                            }else {
                                showToastMessage = Toast.makeText(CaptureActivity.this, "更新資料", Toast.LENGTH_SHORT);
                                showToastMessage.show();
                            }
                        }else {
                            if (showToastMessage != null) {
                                showToastMessage.cancel();
                                showToastMessage = Toast.makeText(CaptureActivity.this, "無此資料", Toast.LENGTH_SHORT);
                                showToastMessage.show();
                            }else {
                                showToastMessage = Toast.makeText(CaptureActivity.this, "無此資料", Toast.LENGTH_SHORT);
                                showToastMessage.show();
                            }
                        }

                    }
                    edt_barcode.setText("");
                }
            });
        }
    }

    private int getCurrentOrientation() {
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        switch (rotation) {
            case Surface.ROTATION_0:
            case Surface.ROTATION_90:
                return ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
            default:
                return ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
        }
    }

    private static boolean isZXingURL(String dataString) {
        if (dataString == null) {
            return false;
        }
        for (String url : ZXING_URLS) {
            if (dataString.startsWith(url)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onPause() {
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        inactivityTimer.onPause();
        ambientLightManager.stop();
        beepManager.close();
        cameraManager.closeDriver();
        //historyManager = null; // Keep for onActivityResult
        if (!hasSurface) {
            SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
            SurfaceHolder surfaceHolder = surfaceView.getHolder();
            surfaceHolder.removeCallback(this);
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if (source == IntentSource.NATIVE_APP_INTENT) {
                    setResult(RESULT_CANCELED);
                    finish();
                    return true;
                }
                if ((source == IntentSource.NONE || source == IntentSource.ZXING_LINK) && lastResult != null) {
                    restartPreviewAfterDelay(0L);
                    return true;
                }
                break;
            case KeyEvent.KEYCODE_FOCUS:
            case KeyEvent.KEYCODE_CAMERA:
                // Handle these events so they don't launch the Camera app
                return true;
            // Use volume up/down to turn on light
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                cameraManager.setTorch(false);
                return true;
            case KeyEvent.KEYCODE_VOLUME_UP:
                cameraManager.setTorch(true);
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.capture, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        int i = item.getItemId();

        if (i == R.id.menu_history) {
            intent.setClassName(this, HistoryActivity.class.getName());
            startActivity(intent);
            //startActivityForResult(intent, HISTORY_REQUEST_CODE);

        } else if (i == R.id.menu_settings) {
            intent.setClassName(this, PreferencesActivity.class.getName());
            startActivity(intent);
        } else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == RESULT_OK) {
            if (requestCode == HISTORY_REQUEST_CODE) {
                int itemNumber = intent.getIntExtra(Intents.History.ITEM_NUMBER, -1);
                if (itemNumber >= 0) {
                    //HistoryItem historyItem = historyManager.buildHistoryItem(itemNumber);
                    //decodeOrStoreSavedBitmap(null, historyItem.getResult());
                }
            }
        }
    }

    private void decodeOrStoreSavedBitmap(Bitmap bitmap, Result result) {
        // Bitmap isn't used yet -- will be used soon
        if (handler == null) {
            savedResultToShow = result;
        } else {
            if (result != null) {
                savedResultToShow = result;
            }
            if (savedResultToShow != null) {
                Message message = Message.obtain(handler, R.id.decode_succeeded, savedResultToShow);
                handler.sendMessage(message);
            }
            savedResultToShow = null;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (holder == null) {
            Log.e(TAG, "*** WARNING *** surfaceCreated() gave us a null surface!");
        }
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    /**
     * A valid barcode has been found, so give an indication of success and show the results.
     *
     * @param rawResult   The contents of the barcode.
     * @param scaleFactor amount by which thumbnail was scaled
     * @param barcode     A greyscale bitmap of the camera data which was decoded.
     */
    public void handleDecode(Result rawResult, Bitmap barcode, float scaleFactor) {
        inactivityTimer.onActivity();
        lastResult = rawResult;
        ResultHandler resultHandler = ResultHandlerFactory.makeResultHandler(this, rawResult);

        boolean fromLiveScan = barcode != null;
        if (fromLiveScan) {
            // Then not from history, so beep/vibrate and we have an image to draw on
            beepManager.playBeepSoundAndVibrate();
            drawResultPoints(barcode, scaleFactor, rawResult);
        }

        switch (source) {
            case NATIVE_APP_INTENT:
            case PRODUCT_SEARCH_LINK:
                handleDecodeExternally(rawResult, resultHandler, barcode);
                break;
            /*case ZXING_LINK:
                if (scanFromWebPageManager == null || !scanFromWebPageManager.isScanFromWebPage()) {
                    handleDecodeInternally(rawResult, resultHandler, barcode);
                } else {
                    handleDecodeExternally(rawResult, resultHandler, barcode);
                }
                break;*/
            case NONE:
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                if (fromLiveScan && prefs.getBoolean(PreferencesActivity.KEY_BULK_MODE, false)) {
                    Toast.makeText(getApplicationContext(),
                            getResources().getString(R.string.msg_bulk_mode_scanned) + " (" + rawResult.getText() + ')',
                            Toast.LENGTH_SHORT).show();
                    // Wait a moment or else it will scan the same barcode continuously about 3 times
                    restartPreviewAfterDelay(BULK_MODE_SCAN_DELAY_MS);
                } else {
                    handleDecodeInternally(rawResult, resultHandler, barcode);
                }
                break;
        }
    }

    /**
     * Superimpose a line for 1D or dots for 2D to highlight the key features of the barcode.
     *
     * @param barcode     A bitmap of the captured image.
     * @param scaleFactor amount by which thumbnail was scaled
     * @param rawResult   The decoded results which contains the points to draw.
     */
    private void drawResultPoints(Bitmap barcode, float scaleFactor, Result rawResult) {
        ResultPoint[] points = rawResult.getResultPoints();
        if (points != null && points.length > 0) {
            Canvas canvas = new Canvas(barcode);
            Paint paint = new Paint();
            paint.setColor(getResources().getColor(R.color.result_points));
            if (points.length == 2) {
                paint.setStrokeWidth(4.0f);
                drawLine(canvas, paint, points[0], points[1], scaleFactor);
            } else if (points.length == 4 &&
                    (rawResult.getBarcodeFormat() == BarcodeFormat.UPC_A ||
                            rawResult.getBarcodeFormat() == BarcodeFormat.EAN_13)) {
                // Hacky special case -- draw two lines, for the barcode and metadata
                drawLine(canvas, paint, points[0], points[1], scaleFactor);
                drawLine(canvas, paint, points[2], points[3], scaleFactor);
            } else {
                paint.setStrokeWidth(10.0f);
                for (ResultPoint point : points) {
                    if (point != null) {
                        canvas.drawPoint(scaleFactor * point.getX(), scaleFactor * point.getY(), paint);
                    }
                }
            }
        }
    }

    private static void drawLine(Canvas canvas, Paint paint, ResultPoint a, ResultPoint b, float scaleFactor) {
        if (a != null && b != null) {
            canvas.drawLine(scaleFactor * a.getX(),
                    scaleFactor * a.getY(),
                    scaleFactor * b.getX(),
                    scaleFactor * b.getY(),
                    paint);
        }
    }

    // Put up our own UI for how to handle the decoded contents.
    private void handleDecodeInternally(Result rawResult, ResultHandler resultHandler, Bitmap barcode) {

        CharSequence displayContents = resultHandler.getDisplayContents();


        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        if (resultHandler.getDefaultButtonID() != null && prefs.getBoolean(PreferencesActivity.KEY_AUTO_OPEN_WEB, false)) {
            resultHandler.handleButtonPress(resultHandler.getDefaultButtonID());
            return;
        }

        //TODO 掃描後更新資料

        cylinders_number = rawResult.getText();
        for (int i = 0; i < cylinders_list.size(); i++) {
            if (cylinders_number.equals(cylinders_list.get(i))) {
                metaHistoryItem = new HistoryItem(cylinders_number);
                new Update().start();
                if (showToastMessage !=null) {
                    showToastMessage.cancel();
                    showToastMessage = Toast.makeText(CaptureActivity.this, "更新資料", Toast.LENGTH_SHORT);
                    showToastMessage.show();
                }else {
                    showToastMessage = Toast.makeText(CaptureActivity.this, "更新資料", Toast.LENGTH_SHORT);
                    showToastMessage.show();
                }
                break;
            } else {
                if (showToastMessage !=null) {
                    showToastMessage.cancel();
                    showToastMessage = Toast.makeText(CaptureActivity.this, "無此資料", Toast.LENGTH_SHORT);
                    showToastMessage.show();
                }else {
                    showToastMessage = Toast.makeText(CaptureActivity.this, "無此資料", Toast.LENGTH_SHORT);
                    showToastMessage.show();
                }
            }
        }

        btn_barcode.setVisibility(View.GONE);
        edt_barcode.setVisibility(View.GONE);
        statusView.setVisibility(View.GONE);
        viewfinderView.setVisibility(View.GONE);
        resultView.setVisibility(View.VISIBLE);

        ImageView barcodeImageView = (ImageView) findViewById(R.id.barcode_image_view);

        if (barcode == null) {
            barcodeImageView.setImageBitmap(BitmapFactory.decodeResource(getResources(),
                    R.drawable.launcher_icon));
        } else {
            barcodeImageView.setImageBitmap(barcode);
        }

        //掃描後所呈現的資訊
        TextView formatTextView = (TextView) findViewById(R.id.format_text_view);
        formatTextView.setText(rawResult.getBarcodeFormat().toString());

        TextView typeTextView = (TextView) findViewById(R.id.type_text_view);
        typeTextView.setText(resultHandler.getType().toString());

        DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
        TextView timeTextView = (TextView) findViewById(R.id.time_text_view);
        timeTextView.setText(formatter.format(new Date(rawResult.getTimestamp())));

        TextView metaTextView = (TextView) findViewById(R.id.meta_text_view);
        View metaTextViewLabel = findViewById(R.id.meta_text_view_label);
        metaTextView.setVisibility(View.GONE);
        metaTextViewLabel.setVisibility(View.GONE);
        Map<ResultMetadataType, Object> metadata = rawResult.getResultMetadata();
        if (metadata != null) {
            //最大格所呈現的文字-----目前為掃到條碼底下的文字
            StringBuilder metadataText = new StringBuilder(20);
            for (Map.Entry<ResultMetadataType, Object> entry : metadata.entrySet()) {
                if (DISPLAYABLE_METADATA_TYPES.contains(entry.getKey())) {
                    metadataText.append(entry.getValue()).append('\n');
                }
            }

            Log.e("tag", "what is metadataText:" + metadataText);

            if (metadataText.length() > 0) {
                metadataText.setLength(metadataText.length() - 1);
                metaTextView.setText(metadataText);
                metaTextView.setVisibility(View.VISIBLE);
                metaTextViewLabel.setVisibility(View.VISIBLE);
            }
        }
        /*掃描後右邊呈現資訊（掃描後得到的訊息）*/
        TextView contentsTextView = (TextView) findViewById(R.id.contents_text_view);
        Log.e("tag", "what is displayContents:" + displayContents);
        contentsTextView.setText(displayContents);
        int scaledSize = Math.max(22, 32 - displayContents.length() / 4);
        contentsTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, scaledSize);

        TextView supplementTextView = (TextView) findViewById(R.id.contents_supplement_text_view);
        supplementTextView.setText("");
        supplementTextView.setOnClickListener(null);
        /*end*/

        Button btn_cap_cancel = (Button) findViewById(R.id.btn_cap_cancel);
        Button btn_cap_ok = (Button) findViewById(R.id.btn_cap_ok);

        btn_cap_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_cap_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                restartPreviewAfterDelay(0L);
            }
        });

        /*掃描後詢問是否要存入資料庫或修改
        metaHistoryItem = new HistoryItem(rawResult.getText());
        if (historyManager.isBarcodeExist(metaHistoryItem.barcode)) {
            //這裡看一下掃到什麼

            Log.e("tag", "掃描的內容：" + rawResult.getText());
            AlertDialog.Builder altBlgBuilder = AltDlgBuilder_OldItemFound();
            altBlgBuilder.show();
        } else {
            Log.e("tag", "Insert new barcode?");
            AlertDialog.Builder altBlgBuilder = AltDlgBuilder_NewItemFound();
            altBlgBuilder.show();
        }
        Toast.makeText(this, metaHistoryItem.capacity, Toast.LENGTH_SHORT).show();
        historyManager.addItem(metaHistoryItem);*/
        //</LISE>

    }

    // Briefly show the contents of the barcode, then handle the result outside Barcode Scanner.
    private void handleDecodeExternally(Result rawResult, ResultHandler resultHandler, Bitmap barcode) {

        if (barcode != null) {
            Log.e("tag", "掃到barcode");
            viewfinderView.drawResultBitmap(barcode);
        }

        long resultDurationMS;
        if (getIntent() == null) {
            resultDurationMS = DEFAULT_INTENT_RESULT_DURATION_MS;
        } else {
            resultDurationMS = getIntent().getLongExtra(Intents.Scan.RESULT_DISPLAY_DURATION_MS,
                    DEFAULT_INTENT_RESULT_DURATION_MS);
        }

        if (resultDurationMS > 0) {
            String rawResultString = String.valueOf(rawResult);
            if (rawResultString.length() > 32) {
                rawResultString = rawResultString.substring(0, 32) + " ...";
            }
            statusView.setText(getString(resultHandler.getDisplayTitle()) + " : " + rawResultString);
        }

        if (copyToClipboard && !resultHandler.areContentsSecure()) {
            CharSequence text = resultHandler.getDisplayContents();
        }

        if (source == IntentSource.NATIVE_APP_INTENT) {

            // Hand back whatever action they requested - this can be changed to Intents.Scan.ACTION when
            // the deprecated intent is retired.
            Intent intent = new Intent(getIntent().getAction());
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            intent.putExtra(Intents.Scan.RESULT, rawResult.toString());
            intent.putExtra(Intents.Scan.RESULT_FORMAT, rawResult.getBarcodeFormat().toString());
            byte[] rawBytes = rawResult.getRawBytes();
            Log.e("tag", "解析資料");
            if (rawBytes != null && rawBytes.length > 0) {
                intent.putExtra(Intents.Scan.RESULT_BYTES, rawBytes);
            }
            Map<ResultMetadataType, ?> metadata = rawResult.getResultMetadata();
            if (metadata != null) {
                if (metadata.containsKey(ResultMetadataType.UPC_EAN_EXTENSION)) {
                    intent.putExtra(Intents.Scan.RESULT_UPC_EAN_EXTENSION,
                            metadata.get(ResultMetadataType.UPC_EAN_EXTENSION).toString());
                }
                Number orientation = (Number) metadata.get(ResultMetadataType.ORIENTATION);
                if (orientation != null) {
                    intent.putExtra(Intents.Scan.RESULT_ORIENTATION, orientation.intValue());
                }
                String ecLevel = (String) metadata.get(ResultMetadataType.ERROR_CORRECTION_LEVEL);
                if (ecLevel != null) {
                    intent.putExtra(Intents.Scan.RESULT_ERROR_CORRECTION_LEVEL, ecLevel);
                }
                @SuppressWarnings("unchecked")
                Iterable<byte[]> byteSegments = (Iterable<byte[]>) metadata.get(ResultMetadataType.BYTE_SEGMENTS);
                if (byteSegments != null) {
                    int i = 0;
                    for (byte[] byteSegment : byteSegments) {
                        intent.putExtra(Intents.Scan.RESULT_BYTE_SEGMENTS_PREFIX + i, byteSegment);
                        i++;
                    }
                }
            }
            sendReplyMessage(R.id.return_scan_result, intent, resultDurationMS);

        } else if (source == IntentSource.PRODUCT_SEARCH_LINK) {

            // Reformulate the URL which triggered us into a query, so that the request goes to the same
            // TLD as the scan URL.
            int end = sourceUrl.lastIndexOf("/scan");
            String replyURL = sourceUrl.substring(0, end) + "?q=" + resultHandler.getDisplayContents() + "&source=zxing";
            sendReplyMessage(R.id.launch_product_query, replyURL, resultDurationMS);

        } else if (source == IntentSource.ZXING_LINK) {


        }
    }

    private void sendReplyMessage(int id, Object arg, long delayMS) {
        if (handler != null) {
            Message message = Message.obtain(handler, id, arg);
            if (delayMS > 0L) {
                handler.sendMessageDelayed(message, delayMS);
            } else {
                handler.sendMessage(message);
            }
        }
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        if (surfaceHolder == null) {
            throw new IllegalStateException("No SurfaceHolder provided");
        }
        if (cameraManager.isOpen()) {
            Log.w(TAG, "initCamera() while already open -- late SurfaceView callback?");
            return;
        }
        try {
            cameraManager.openDriver(surfaceHolder);
            // Creating the handler starts the preview, which can also throw a RuntimeException.
            if (handler == null) {
                handler = new CaptureActivityHandler(this, decodeFormats, decodeHints, characterSet, cameraManager);
            }
            decodeOrStoreSavedBitmap(null, null);
        } catch (IOException ioe) {
            Log.w(TAG, ioe);
            displayFrameworkBugMessageAndExit();
        } catch (RuntimeException e) {
            // Barcode Scanner has seen crashes in the wild of this variety:
            // java.?lang.?RuntimeException: Fail to connect to camera service
            Log.w(TAG, "Unexpected error initializing camera", e);
            displayFrameworkBugMessageAndExit();
        }
    }

    private void displayFrameworkBugMessageAndExit() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.app_name_barcode_scanner));
        builder.setMessage(getString(R.string.msg_camera_framework_bug));
        builder.setPositiveButton(R.string.button_ok, new FinishListener(this));
        builder.setOnCancelListener(new FinishListener(this));
        builder.show();
    }

    public void restartPreviewAfterDelay(long delayMS) {
        if (handler != null) {
            handler.sendEmptyMessageDelayed(R.id.restart_preview, delayMS);
        }
        resetStatusView();
    }

    private void resetStatusView() {
        resultView.setVisibility(View.GONE);
        statusView.setText(R.string.msg_default_status);
        statusView.setVisibility(View.VISIBLE);
        edt_barcode.setVisibility(View.VISIBLE);
        btn_barcode.setVisibility(View.VISIBLE);
        viewfinderView.setVisibility(View.VISIBLE);
        lastResult = null;
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();
    }


    //LISE
    public AlertDialog.Builder AltDlgBuilder_NewItemFound() {
        AlertDialog.Builder altBlgBuilder = new AlertDialog.Builder(CaptureActivity.this);
        altBlgBuilder.setTitle("新建項目");
        altBlgBuilder.setMessage("資料庫無這筆資料，你要新增嗎？");
        altBlgBuilder.setCancelable(false);

        altBlgBuilder.setPositiveButton("是",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //回傳鋼瓶號碼  cylinders_number
                        new Update().start();

                        AlertDialog.Builder dlg_choice = AltDlgBuilder_choice();
                        dlg_choice.show();
                    }
                });
        altBlgBuilder.setNegativeButton("否",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });

        return altBlgBuilder;
    }


    //LISE
    public AlertDialog.Builder AltDlgBuilder_OldItemFound() {
        Log.e("tag", "do AltDlgBuilder_OldItemFound()");
        AlertDialog.Builder altBlgBuilder = new AlertDialog.Builder(CaptureActivity.this);
        altBlgBuilder.setTitle("修改項目");
        altBlgBuilder.setMessage("資料庫已有這筆資料，你要修改嗎？");
        altBlgBuilder.setCancelable(false);

        altBlgBuilder.setPositiveButton("是",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //回傳鋼瓶號碼  cylinders_number
                        new Update().start();

//                        AlertDialog.Builder dlg_choice = AltDlgBuilder_choice();
//                        dlg_choice.show();
                    }
                });
        altBlgBuilder.setNegativeButton("否",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });

        return altBlgBuilder;
    }


    //LISE 有LISE是寶輝寫的
    public AlertDialog.Builder AltDlgBuilder_choice() {

        String[] options = {"公司", "檢驗廠", "客戶"};
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        metaHistoryItem.location = "公司";
                        break;
                    case 1:
                        metaHistoryItem.location = "檢驗廠";
                        break;
                    case 2:
                        metaHistoryItem.location = "客戶";
                        break;
                }
                historyManager.addItem(metaHistoryItem);
                renewListview();
            }
        };

        AlertDialog.Builder altBlgBuilder = new AlertDialog.Builder(CaptureActivity.this);
        altBlgBuilder.setTitle("請選擇位置");
        altBlgBuilder.setCancelable(false);
        altBlgBuilder.setItems(options, listener);

        return altBlgBuilder;
    }


    private void renewListview() {
        Log.e("tag", "do renewListview");
        try {
            Iterator<HistoryItem> items = historyManager.getAllItem();
            arrayAdapter_barcode.clear();
            while (items.hasNext()) {
                arrayAdapter_barcode.add(items.next().barcode);
                Log.e("tag", "arrayAdapter_barcode:" + items.next().barcode);
            }
        } catch (NoSuchElementException e) {
            Log.e("scanner", "錯誤問題：" + e.toString());
        }
    }

    public class Update extends Thread {

        @Override
        public void run() {
            String retSrc;
            String url = "";
            String url1 = "";
            int urlLen = 1;
            metaHistoryItem.barcode = cylinders_number;
            switch (flag) {

                case 0: //檢驗廠掃入
                    Log.e("thread", "操作項目：掃入");
                    url = "http://198.245.55.221:8089/ProjectGAPP/php/IO_inspect.php?IO_val=1" +
                            "&cylinders_number=" + cylinders_number + "&suppliers_id=6";
                    metaHistoryItem.location = "檢驗廠";
                    historyManager.addItem(metaHistoryItem);
                    break;
                case 1: //檢驗廠掃出
                    Log.e("thread", "操作項目：掃出");
                    url = "http://198.245.55.221:8089/ProjectGAPP/php/IO_inspect.php?IO_val=0&cylinders_number=" + cylinders_number;
                    url1 = "http://198.245.55.221:8089/ProjectGAPP/php/getData_from_xml.php?card_no=" + cylinders_number;
                    urlLen = 2;
                    metaHistoryItem.location = "公司";
                    historyManager.addItem(metaHistoryItem);
                    break;
                case 2: //更新狀態
                    Log.e("thread", "操作項目：更新項目");
                    url = "http://198.245.55.221:8089/ProjectGAPP/php/getData_from_xml.php?card_no=" + cylinders_number;
                    break;
                case 3: //任務掃入
                    url = "http://198.245.55.221:8089/ProjectGAPP/php/upd_other.php?tb_name=cylinders" +
                            "&tb_where_name=cylinders_number&tb_where_val=" + cylinders_number +
                            "&tb_td=cylinders_type&tb_val=1_" + car_id;
                    metaHistoryItem.location = "公司";
                    historyManager.addItem(metaHistoryItem);
                    break;
                case 4: //任務掃出
                    url = "http://198.245.55.221:8089/ProjectGAPP/php/upd_other.php?tb_name=cylinders" +
                            "&tb_where_name=cylinders_number&tb_where_val=" + cylinders_number +
                            "&tb_td=cylinders_type&tb_val=2_" + customer_id;
                    metaHistoryItem.location = "客戶";
                    historyManager.addItem(metaHistoryItem);
                    break;

                default:
                    break;
            }

            for (int i = 0; i < urlLen; i++) {
                HttpGet httpget = new HttpGet(url);
                Log.e("capture", "url:" + url);
                HttpClient httpclient = new DefaultHttpClient();
                try {
                    HttpResponse response = httpclient.execute(httpget);
                    url = url1; //將url換成下一個
                    HttpEntity resEntity = response.getEntity();
                    if (resEntity != null) {
                        retSrc = EntityUtils.toString(resEntity);
                        Log.e("retSrc", "完整資料：" + retSrc);
                    } else {
                        retSrc = "Did not work!";
                        Log.e("retSrc", "完整資料：" + retSrc);
                    }

                } catch (Exception e) {
                    Log.e("retSrc", "讀取JSON Error...");
                } finally {

                    httpclient.getConnectionManager().shutdown();
                }
            }
        }
    }

    /*載入cylinders列表*/
    private class Cylinders_list extends AsyncTask<String, Void, ArrayList<String>> {

        @Override
        protected ArrayList<String> doInBackground(String... urls) {
            try {
                cylinders_list = new ArrayList<>();
                String cylinder;
                JSONArray jsonArrayCylinder = new JSONArray(getJSONData(urls[0]));
                for (int i = 0; i<jsonArrayCylinder.length(); i++) {
                    cylinder = jsonArrayCylinder.getJSONArray(i).getString(2);
                    cylinders_list.add(cylinder);
                    Log.e("capture cylinder", "cylinder number:"+cylinder);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return cylinders_list;
        }

        @Override
        protected void onPostExecute(ArrayList<String> strings) {
            super.onPostExecute(strings);
            asyncDone = true;
        }

        private String getJSONData(String url) {
            String retSrc = "";
            HttpGet httpget = new HttpGet(url);
            HttpClient httpclient = new DefaultHttpClient();
            try {
                HttpResponse response = httpclient.execute(httpget);
                HttpEntity resEntity = response.getEntity();
                if (resEntity != null) {
                    retSrc = EntityUtils.toString(resEntity);
                    Log.e("retSrc", "完整資料：" + retSrc);
                } else {
                    throw new IOException("Did not work!");
                }

            } catch (Exception e) {
                Log.e("retSrc", String.valueOf(e));
                return null;
            } finally {
                httpclient.getConnectionManager().shutdown();
            }
            return retSrc;

        }


    }
}
