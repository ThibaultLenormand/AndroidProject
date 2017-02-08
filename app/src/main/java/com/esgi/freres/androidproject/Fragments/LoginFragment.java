package com.esgi.freres.androidproject.Fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.preference.PreferenceManager;

import com.esgi.freres.androidproject.Activities.MainActivity;
import com.esgi.freres.androidproject.R;
import com.esgi.freres.androidproject.Webservices.LoginService;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

import static android.content.Context.MODE_PRIVATE;

public class LoginFragment extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onResume() {
        super.onResume();
        final String TAG = "LoginFragment";
        Button submit = (Button) getActivity().findViewById(R.id.submit);
        final TextView register = (TextView) getActivity().findViewById(R.id.register);

        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText login = (EditText) getActivity().findViewById(R.id.editTextLogin);
                EditText password = (EditText) getActivity().findViewById(R.id.editTextPassword);

                RequestParams params = new RequestParams();
                params.put("email", login.getText().toString());
                params.put("password", password.getText().toString());

                LoginService.login(new AsyncHttpResponseHandler() {

                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        Log.i(TAG, "Login success => "+ new String(responseBody));
                        // Vérif, toast et main activity
                        try {
                            JSONObject jsonResponse = new JSONObject(new String(responseBody));
                            if(jsonResponse.getInt("code") == 0){
                                SharedPreferences.Editor editor = sp.edit();
                                editor.putString("token", jsonResponse.getJSONObject("result").getString("token"));
                                editor.putString("fname", jsonResponse.getJSONObject("result").getString("firstname"));
                                editor.commit();

                                Toast.makeText(getActivity(),getContext().getString(R.string.login_success),Toast.LENGTH_LONG).show();
                                Intent MainActivityIntent = new Intent(getContext(), MainActivity.class);
                                startActivity(MainActivityIntent);
                            }
                            else {
                                Toast.makeText(getActivity(),getContext().getString(R.string.login_fail),Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Log.i(TAG, "Login failure");
                        // Toast fail
                        Toast.makeText(getActivity(),getContext().getString(R.string.login_failure),Toast.LENGTH_LONG).show();
                    }
                }, params);
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                RegisterFragment registerFrag = new RegisterFragment();
                FragmentManager fm = getFragmentManager();
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                fragmentTransaction.replace(R.id.container, registerFrag);
                fragmentTransaction.commit();
            }
        });
        super.onResume();
    }
}
