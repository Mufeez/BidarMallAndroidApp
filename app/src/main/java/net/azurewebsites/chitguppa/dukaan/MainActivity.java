package net.azurewebsites.chitguppa.dukaan;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.UrlQuerySanitizer;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends AppCompatActivity {

    private android.webkit.WebView myWebView;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myWebView = (WebView) findViewById(R.id.webView);
        final android.webkit.WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
       // webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        //Log.i(LOG_TAG, "onRefresh called from SwipeRefreshLayout");

        // This method performs the actual data-refresh operation.
        // The method calls setRefreshing(false) when it's finished.
        // myUpdateOperation();
        //myWebView.reload();

        //webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);

        myWebView.setWebViewClient(new WebViewClient() {



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
                    String body =snt.getValue("body");
                    String phone=snt.getValue("phone");

                    String smsUrl="sms:"+phone+"?body"+body;
                    String whatsappUrl="https://api.whatsapp.com/send?phone="+ phone +"&text=" + body;
                    boolean installed = appInstalledOrNot("com.whatsapp");
                    if(installed) {
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

                    Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                    startActivity(browserIntent);

                    return true;

                }*/

               if (url.startsWith("https://api.whatsapp.com/")) {

                   UrlQuerySanitizer snt = new UrlQuerySanitizer();
                   snt.setAllowUnregisteredParamaters(true);
                   snt.parseUrl(url);
                   String phone=snt.getValue("phone");

                   String smsUrl="sms:"+phone+"?body"+body;
                   String whatsappUrl="https://api.whatsapp.com/send?phone="+ phone +"&text=" + body;
                   boolean installed = appInstalledOrNot("com.whatsapp");
                   if(installed) {
                       Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                               Uri.parse(url));
                       startActivity(browserIntent);
                    String smsUrl="sms:"+phone+"?body"+body;
                        startActivity(browserIntent);

                        return true;
                    } else {
                        Intent sendMessages = new Intent(Intent.ACTION_SENDTO, Uri.parse(smsUrl));
                        startActivity(sendMessages);
                    }
                    return true;

                }

                myWebView.loadUrl(url);
                return true;
            }




        });
        myWebView.loadUrl("https://www.bidarmall.com");

    };

    @Override
    public void onBackPressed(){
        if(myWebView.canGoBack()){myWebView.goBack();}
        else {super.onBackPressed();}
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
