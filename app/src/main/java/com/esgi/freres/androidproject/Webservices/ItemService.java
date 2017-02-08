package com.esgi.freres.androidproject.Webservices;

import android.util.Log;

import com.esgi.freres.androidproject.Utils.Config;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class ItemService {

    private static final String TAG = "ItemService";

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void create(AsyncHttpResponseHandler responseH, RequestParams params) {
        client.get(Config.BASE_URL+ Config.URL_CREATE_PRODUCT, params, responseH);
        Log.i(TAG, "Request create list => "+Config.BASE_URL+Config.URL_CREATE_PRODUCT+" "+responseH);
    }

    public static void list(AsyncHttpResponseHandler responseH, RequestParams params) {
        client.get(Config.BASE_URL + Config.URL_PRODUCT, params, responseH);
        Log.i(TAG, "Request list lists => "+ Config.BASE_URL+Config.URL_PRODUCT+ " "+responseH);
    }

    public static void edit(AsyncHttpResponseHandler responseH, RequestParams params) {
        client.get(Config.BASE_URL+ Config.URL_EDIT_PRODUCT, params, responseH);
        Log.i(TAG, "Request edit list => "+Config.BASE_URL+Config.URL_EDIT_PRODUCT+" "+responseH);
    }

    public static void remove(AsyncHttpResponseHandler responseH, RequestParams params) {
        client.get(Config.BASE_URL+ Config.URL_REMOVE_PRODUCT, params, responseH);
        Log.i(TAG, "Request remove list => "+Config.BASE_URL+Config.URL_REMOVE_PRODUCT+" "+responseH);
    }
}
