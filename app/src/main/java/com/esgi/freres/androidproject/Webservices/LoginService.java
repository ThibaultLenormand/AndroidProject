package com.esgi.freres.androidproject.Webservices;

import android.util.Log;

import com.esgi.freres.androidproject.Utils.Config;
import com.loopj.android.http.*;

public class LoginService {

    private static final String TAG = "LoginService";

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static  void  login(AsyncHttpResponseHandler responseH, RequestParams params) {
        client.get(Config.BASE_URL+ Config.URL_LOGIN, params, responseH);
        Log.i(TAG,"Request login => "+Config.BASE_URL+Config.URL_LOGIN+" "+responseH);
    }


    public static  void  register(AsyncHttpResponseHandler responseH, RequestParams params) {
        client.get(Config.BASE_URL+Config.URL_REGISTER, params, responseH);
        Log.i(TAG,"Request register received => "+Config.BASE_URL+Config.URL_REGISTER+" "+responseH);
    }
}
