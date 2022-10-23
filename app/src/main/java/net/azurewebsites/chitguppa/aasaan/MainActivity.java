package net.azurewebsites.chitguppa.aasaan;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.UrlQuerySanitizer;
import android.os.Bundle;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

public class MainActivity extends AppCompatActivity {

    private android.webkit.WebView myWebView;
    String mGeoLocationRequestOrigin = null;
    GeolocationPermissions.Callback  mGeoLocationCallback = null;
    String URL=null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_main);



        myWebView = (WebView) findViewById(R.id.webView);


        final android.webkit.WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSettings.setDomStorageEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setGeolocationEnabled(true);
        webSettings.setGeolocationDatabasePath(getFilesDir().getPath());
        webSettings.setPluginState(WebSettings.PluginState.ON);
        myWebView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {
                int permissionCheckES = ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
                int permissionCheckIS = ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE);
                int permissionCheckNP = ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_NOTIFICATION_POLICY);
                if (permissionCheckES != PackageManager.PERMISSION_GRANTED) {
                    //requesting permission
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                }

                else if (permissionCheckIS != PackageManager.PERMISSION_GRANTED) {
                    //requesting permission
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
                }

               else  if (permissionCheckNP != PackageManager.PERMISSION_GRANTED) {
                    //requesting permission
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_NOTIFICATION_POLICY}, 3);
                }



                    myWebView.loadUrl(JavaScriptInterface.getBase64StringFromBlobUrl(url));




            }
        });
        webSettings.setAppCachePath(MainActivity.this.getApplicationContext().getCacheDir().getAbsolutePath());
        webSettings.setDatabaseEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        myWebView.addJavascriptInterface(new JavaScriptInterface(MainActivity.this.getApplicationContext()), "Android");
        webSettings.setPluginState(WebSettings.PluginState.ON);
        myWebView.loadUrl("https://www.retailcenter.io");



        if (android.os.Build.VERSION.SDK_INT >= 21) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(myWebView, true);
        } else {
            CookieManager.getInstance().setAcceptCookie(true);
        }


       myWebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView myWebView, String url) {


                if (url.startsWith("tel:")) {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
                    startActivity(intent);
                    myWebView.reload();
                    return true;
                }

                if (url.startsWith("sms:")) {
                    Intent sendMessages = new Intent(Intent.ACTION_SENDTO, Uri.parse(url));
                    startActivity(sendMessages);
                    return true;
                }
                if (url.startsWith("smartos:")) {
                    UrlQuerySanitizer snt = new UrlQuerySanitizer();
                    snt.setAllowUnregisteredParamaters(true);
                    snt.parseUrl(url);
                    String body = snt.getValue("body");
                    String phone = snt.getValue("phone");

                    String smsUrl = "sms:" + phone + "?body" + body;
                    String whatsappUrl = "https://api.whatsapp.com/send?phone=" + phone + "&text=" + body;
                    boolean installed = appInstalledOrNot("com.whatsapp");
                    if (installed) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse(url));
                        startActivity(browserIntent);

                        return true;
                    } else {
                        Intent sendMessages = new Intent(Intent.ACTION_SENDTO, Uri.parse(smsUrl));
                        startActivity(sendMessages);
                    }
                    return true;
                }


                if (url.startsWith("https://api.whatsapp.com/")) {

                    UrlQuerySanitizer snt = new UrlQuerySanitizer();
                    snt.setAllowUnregisteredParamaters(true);
                    snt.parseUrl(url);
                    String body = snt.getValue("text");
                    String phone = snt.getValue("phone");

                    String smsUrl = "sms:" + phone + "?body" + body;
                    String whatsappUrl = "https://api.whatsapp.com/send?phone=" + phone + "&text=" + body;
                    boolean installed = appInstalledOrNot("com.whatsapp");
                    if (installed) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse(url));
                        startActivity(browserIntent);

                        return true;
                    } else {
                        Intent sendMessages = new Intent(Intent.ACTION_SENDTO, Uri.parse(smsUrl));
                        startActivity(sendMessages);
                    }
                    return true;

                }


                return false;
            }




        });




        myWebView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onGeolocationPermissionsShowPrompt(final String origin,
                                                           final GeolocationPermissions.Callback callback) {

                int permissionCheckFineLocation = ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
                if (permissionCheckFineLocation!= PackageManager.PERMISSION_GRANTED) {
                    mGeoLocationCallback=callback;
                    mGeoLocationRequestOrigin=origin;
                    //requesting permission
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 123);
                }

                else {// permission and the user has therefore already granted it
                    displayLocationSettingsRequest( MainActivity.this);
                    callback.invoke(origin, true, false);
                }

            }
        });






    };

    @Override
    public void onBackPressed(){
        if(myWebView.canGoBack()){myWebView.goBack();}
        else {super.onBackPressed();}
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
            //you have the permission now.
            if(requestCode==123) {
                displayLocationSettingsRequest( MainActivity.this);
                mGeoLocationCallback.invoke(mGeoLocationRequestOrigin, true, false);
            }

        }


    }

    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = getPackageManager();
        boolean app_installed;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        }
        catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }

    private void displayLocationSettingsRequest(Context context) {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API).build();
        googleApiClient.connect();
        final String TAG = "YOUR-TAG-NAME";
        final int REQUEST_CHECK_SETTINGS = 0x1;

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.i(TAG, "All location settings are satisfied.");
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.i(TAG, "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");

                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result
                            // in onActivityResult().
                            status.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            Log.i(TAG, "PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.i(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                        break;
                }
            }
        });
    }



}
