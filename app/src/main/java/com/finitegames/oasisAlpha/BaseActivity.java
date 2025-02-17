package com.finitegames.oasisAlpha;

import static android.view.View.VISIBLE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.messaging.FirebaseMessaging;

@SuppressWarnings("Convert2Lambda")
public class BaseActivity extends AppCompatActivity {

    public ProgressBar spinner;
    public WebView webView;
    public TextView textView;
    public String title;
    public Context context; //Application context
    public String token; //device token string.
    public BottomNavigationView bottomNavigationView; //Hook to the bottom navigation
    public NavigationView navigationView; //hook the left navigation

    public String urlLoad = "https://finitegames.co.za?ax=thesimpson";


    public void loading(WebView view,ProgressBar spinner){
        if(spinner.getVisibility() == View.GONE){
            view.setVisibility(View.INVISIBLE);
            spinner.setVisibility(VISIBLE);
        }
    }
    public void endloading(WebView view,ProgressBar spinner){
        if(spinner.getVisibility() == VISIBLE){
            spinner.setVisibility(View.GONE);
            view.setVisibility(VISIBLE);
        }
    }


    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        context = this.getApplicationContext();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        //functions.updateColor();



        //Local variables
        spinner = findViewById(R.id.loadingcircle);
        spinner.setVisibility(VISIBLE);
        textView = findViewById(R.id.textView);
        navigationView = findViewById(R.id.NavigationLeft);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setVisibility(View.GONE);
        webView = findViewById(R.id.webView);

        Intent currentIntent = this.getIntent();
        Bundle extras = currentIntent.getExtras();

        if(extras != null){
            if(extras.containsKey("URL")){
                urlLoad = extras.getString("URL");
                System.out.println("Still getting here");
                System.out.println(urlLoad);

                extras.remove("URL");
            }else{
                urlLoad = "https://finitegames.co.za?ax=" + token;
            }

        }else{
            System.out.println("Extras is null");
            System.out.println(urlLoad);
        }


        //firebase messaging.
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        System.out.println("Fetching FCM registration token failed");
                    }
                    // Get new FCM registration token
                    token = task.getResult();

//                        try {
//                            String Url_string = "https://finitegames.co.za/api?d=" + token;
//                            //Boolean avail = verify_User("Url_string");
//                            if(verify_User(Url_string)){
//                                String avail = "exists";
//                            }else{
//
//                            }
//                        } catch (IOException | JSONException e) {
//                            e.printStackTrace();
//                        }
                    //System.out.println("Token :" + token);
                    //Toast.makeText(this, "it works" + token, Toast.LENGTH_SHORT).show();
                });


        //Create a webview.
        webView.setWebViewClient(new WebViewClient(){

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                textView.setVisibility(View.VISIBLE);
                bottomNavigationView.getMenu().clear();
                bottomNavigationView.inflateMenu(R.menu.bottom_nav_1);
                endloading(view, spinner);
            }


            @SuppressLint({"SetTextI18n", "ObsoleteSdkInt"})
            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                super.onReceivedHttpError(view, request, errorResponse);

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    int statusCode = errorResponse.getStatusCode();
                    String requestedPage = request.getUrl().toString();
                    //System.out.println("Resposnse error :" + errorResponse.getStatusCode());

                    if(statusCode >= 400 && statusCode <= 599){
                        if(!requestedPage.endsWith("ico")){
                            textView.setVisibility(View.VISIBLE);
                            textView.setText("Server respsonse error :" + statusCode + " \n Requested url :");
                            bottomNavigationView.getMenu().clear();
                            bottomNavigationView.inflateMenu(R.menu.bottom_nav_1);
                            endloading(view, spinner);
                        }
                    }
                }
            }

            @SuppressLint("QueryPermissionsNeeded")
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                //noinspection
                if(url.contains("finitegames") || url.contains("payfast") ){
                    if(url.contains("login")) bottomNavigationView.setVisibility(View.GONE);
                }else{

                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    if(browserIntent.resolveActivity(getPackageManager()) != null){
                        startActivity(browserIntent);
                    }
                    view.loadUrl("https://finitegames.co.za");
                }
            }

            @SuppressLint("QueryPermissionsNeeded")
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                super.shouldOverrideUrlLoading(view, url);
                //bottomNavigationView.setVisibility(View.GONE);
                if(url.contains("finitegames")){
                    loading(view,spinner);
                }else if(url.contains("payfast")){
                    if(url.contains("documentation") || url.contains("legal") || url.contains("contact") || url.contains("support")){
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        if(browserIntent.resolveActivity(getPackageManager()) != null){
                            startActivity(browserIntent);
                        }
                        view.loadUrl("finitegames.co.za/money.php");
                    }else{
                        loading(view,spinner);
                    }
                }else{
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    if(browserIntent.resolveActivity(getPackageManager()) != null){
                        startActivity(browserIntent);
                    }
                }
                return false;
            }

            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                //noinspection
                if(!url.contains("login")) {
                    check_orient();
                    //bottomNavigationView.setVisibility(VISIBLE);
                }

                if(!url.contains("android_asset/error")){
                    endloading(view, spinner);
                }


            }
        });

        //noinspection deprecation
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);
        navigationView.setNavigationItemSelectedListener(leftNavListener);

        webView.loadUrl(urlLoad);
        webView.setVisibility(View.INVISIBLE);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setGeolocationEnabled(true);
        webSettings.setSupportZoom(false);
        webSettings.setTextZoom(85);

    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        //System.out.println("Orion");
        super.onConfigurationChanged(newConfig);

        //Check the screen orientation.
        check_orient();

//        if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
//            Toast.makeText(this, "landscape mode", Toast.LENGTH_SHORT).show();
//            bottomNavigationView.setVisibility(View.GONE);
//            navigationView.setVisibility(VISIBLE);
//            //System.out.println("Orion");
//
//        }else if(newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
//            navigationView.setVisibility(View.GONE);
//            bottomNavigationView.setVisibility(VISIBLE);
//            Toast.makeText(this, "Potrait mode", Toast.LENGTH_SHORT).show();
//        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Bundle newExtra = intent.getExtras();
        WebView webView = BaseActivity.this.webView;

        if(newExtra != null){
            //System.out.println("Got here 22 vnvnbvghv");
            if(newExtra.containsKey("URL")){
                String newUrl = newExtra.getString("URL");
                //System.out.println(newUrl);
                webView.loadUrl(newUrl);
            }
        }
    }

    public void check_orient(){
        int orient = getResources().getConfiguration().orientation;
        if(orient == 1){
            navigationView.setVisibility(View.GONE);
            bottomNavigationView.setVisibility(View.VISIBLE);
        }else if(orient == 2){
            bottomNavigationView.setVisibility(View.GONE);
            navigationView.setVisibility(View.VISIBLE);
        }

    }
    private final NavigationView.OnNavigationItemSelectedListener leftNavListener =
            new NavigationView.OnNavigationItemSelectedListener() {
                @SuppressLint("NonConstantResourceId")
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    return handle_menu(item);
                }
            };


    @SuppressWarnings("deprecation")
    private final BottomNavigationView.OnNavigationItemSelectedListener navListener =
            this::handle_menu;

    @SuppressLint("NonConstantResourceId")
    private boolean handle_menu(MenuItem item){
        //Fragment selectedFragment = null;
        WebView webView;
        webView = BaseActivity.this.webView;
        String url = webView.getUrl();
        title = webView.getTitle();
        final String urlDevide = "/";
        final String refreshDevide = "?";

        int divPosition = url.lastIndexOf(urlDevide);
        if(url.endsWith(urlDevide)){
            url = url.substring(0,url.length() - 1);
            divPosition = url.lastIndexOf(urlDevide);
        }
        int refreshDivPosition;

        if(url.contains("?")){
            refreshDivPosition = url.lastIndexOf(refreshDevide);
        }else{
            refreshDivPosition = -1;
        }

        String navUrl;

        if(spinner.getVisibility() == VISIBLE){
            return false;
        }else{
            loading(webView, spinner);

        }

        switch (item.getItemId()){
            case R.id.nav_refresh:

                if(url.contains("payfast")){
                    endloading(webView, spinner);
                }else if(url.contains("finitegames")){
                    if (refreshDivPosition > 1){
                        navUrl = url.substring(0,refreshDivPosition);
                        webView.loadUrl(navUrl);
                    }else{
                        webView.reload();
                    }
                }
                break;

            case R.id.nav_home:
                webView.loadUrl("https://finitegames.co.za?ax=thesimpson&abel=" + token);
                break;

            case R.id.nav_back:
                navUrl = url.substring(0,divPosition);
                if(url.contains("payfast")){
                    webView.loadUrl("finitegames.co.za/money.php");
                }else if(divPosition <= 10 || url.endsWith("co.za/") || url.endsWith("co.za") || url.endsWith("za?ax=thesimpson&abel=" + token)){
                    endloading(webView, spinner);
                }else{
                    webView.loadUrl(navUrl);

                }

                break;
            case R.id.nav_chat:
                webView.loadUrl("https://finitegames.co.za/chat.php");
                break;

            case R.id.nav_profile:
                webView.loadUrl("https://finitegames.co.za/settings.php");
                break;

            case R.id.reset:
                recreate();
                break;

            case R.id.exit:
                System.exit(0);
                break;

            default:
                throw new IllegalStateException("Unexpected value: " + item.getItemId());
        }
        return true;
    }
}
