package net.azurewebsites.chitguppa.aasaan;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.UrlQuerySanitizer;
import android.os.Bundle;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class MainActivity extends AppCompatActivity {

    private android.webkit.WebView myWebView;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_main);
        myWebView = (WebView) findViewById(R.id.webView);


        final android.webkit.WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webSettings.setDomStorageEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setSupportMultipleWindows(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
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
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                }

               else  if (permissionCheckNP != PackageManager.PERMISSION_GRANTED) {
                    //requesting permission
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_NOTIFICATION_POLICY}, 1);
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
        myWebView.loadUrl("https://retailcenter.io");


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



}
