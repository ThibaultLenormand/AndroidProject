package com.esgi.freres.androidproject.Fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.esgi.freres.androidproject.Activities.MainActivity;
import com.esgi.freres.androidproject.R;
import com.esgi.freres.androidproject.Webservices.ListService;
import com.esgi.freres.androidproject.Webservices.LoginService;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class CreateListFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_createlist, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        final String TAG = "CreateListFragment";
        Button submit = (Button) getActivity().findViewById(R.id.addList);

        submit.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {

                final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
                String token = sp.getString("token", null);
                EditText name = (EditText) getActivity().findViewById(R.id.editTextNewList);

                RequestParams params = new RequestParams();
                params.put("token", token);
                params.put("name", name.getText().toString());

                ListService.create(new AsyncHttpResponseHandler() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        Log.i(TAG, "Create list success =>"+ new String(responseBody));
                        try {
                            JSONObject jsonResponse = new JSONObject(new String(responseBody));
                            if(jsonResponse.getInt("code") == 0) {
                                Toast.makeText(getActivity(),getContext().getString(R.string.add_list_success),Toast.LENGTH_SHORT).show();
                                Intent MainActivityIntent = new Intent(getContext(), MainActivity.class);
                                startActivity(MainActivityIntent);
                            }
                            else {
                                Toast.makeText(getActivity(),getContext().getString(R.string.add_list_failure),Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Log.i(TAG, "Register failure");
                        // Toast fail
                        Toast.makeText(getActivity(),"Registration failed",Toast.LENGTH_LONG).show();
                    }
                }, params);
            }
        });
    }
}
