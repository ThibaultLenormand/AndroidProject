package com.esgi.freres.androidproject.Fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
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

import com.esgi.freres.androidproject.R;
import com.esgi.freres.androidproject.Webservices.LoginService;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class RegisterFragment extends Fragment {

    public RegisterFragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        final String TAG = "RegisterFragment";
        Button submit = (Button) getActivity().findViewById(R.id.submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText fname = (EditText) getActivity().findViewById(R.id.editTextFirstName);
                EditText lname = (EditText) getActivity().findViewById(R.id.editTextLastName);
                EditText email = (EditText) getActivity().findViewById(R.id.editTextEmail);
                EditText password = (EditText) getActivity().findViewById(R.id.editTextPassword);

                RequestParams params = new RequestParams();
                params.put("firstname", fname.getText().toString());
                params.put("lastname", lname.getText().toString());
                params.put("email", email.getText().toString());
                params.put("password", password.getText().toString());

                LoginService.register(new AsyncHttpResponseHandler() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        Log.i(TAG, "Register success =>"+ new String(responseBody));
                        // Vérif, toast et login fragment
                        try {
                            JSONObject jsonResponse = new JSONObject(new String(responseBody));
                            if(jsonResponse.getInt("code") == 0) {
                                Toast.makeText(getActivity(),getContext().getString(R.string.register_success),Toast.LENGTH_LONG).show();
                                LoginFragment loginFrag = new LoginFragment();
                                FragmentManager fm = getFragmentManager();
                                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                                fragmentTransaction.replace(R.id.container, loginFrag);
                                fragmentTransaction.commit();
                            }
                            else {
                                Toast.makeText(getActivity(),getContext().getString(R.string.register_fail),Toast.LENGTH_LONG).show();
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
