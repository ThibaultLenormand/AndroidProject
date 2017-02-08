package com.esgi.freres.androidproject.Fragments;

import android.app.Fragment;
import android.app.FragmentManager;
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
import android.widget.TextView;
import android.widget.Toast;

import com.esgi.freres.androidproject.Activities.ListActivity;
import com.esgi.freres.androidproject.Activities.MainActivity;
import com.esgi.freres.androidproject.R;
import com.esgi.freres.androidproject.Webservices.ItemService;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class ItemFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_item, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onResume() {
        super.onResume();
        final String TAG = "ItemFragment";
        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
        final String token = sp.getString("token", null);
        final Integer idItem = sp.getInt("idItem", 0);
        final String nameItem = sp.getString("nameItem", null);
        final Integer qtyItem = sp.getInt("qtyItem", 0);
        final String priceItem = sp.getString("priceItem", null);

        Button edit = (Button) getActivity().findViewById(R.id.buttonEditItem);
        Button delete = (Button) getActivity().findViewById(R.id.buttonDeleteItem);

        // Get item
        final TextView nameTextView = (TextView) getActivity().findViewById(R.id.editTextEditItemName);
        final TextView qtyTextView = (TextView) getActivity().findViewById(R.id.editTextEditItemQty);
        final TextView priceTextView = (TextView) getActivity().findViewById(R.id.editTextEditItemPrice);

        nameTextView.setText(nameItem);
        qtyTextView.setText(qtyItem.toString());
        priceTextView.setText(priceItem.toString());

        // Edit item
        edit.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {

                RequestParams params = new RequestParams();
                params.put("token", token);
                params.put("id", idItem);
                params.put("name", nameTextView.getText().toString());
                params.put("quantity", qtyTextView.getText().toString());
                params.put("price", priceTextView.getText().toString());

                ItemService.edit(new AsyncHttpResponseHandler() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        Log.i(TAG, "Edit item success =>"+ new String(responseBody));
                        try {
                            JSONObject jsonResponse = new JSONObject(new String(responseBody));
                            if(jsonResponse.getInt("code") == 0) {
                                Toast.makeText(getActivity(),getContext().getString(R.string.edit_item_success),Toast.LENGTH_SHORT).show();
                                Intent ListActivityIntent = new Intent(getContext(), ListActivity.class);
                                startActivity(ListActivityIntent);
                            }
                            else {
                                Toast.makeText(getActivity(),getContext().getString(R.string.edit_item_failure),Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Log.i(TAG, "Edit item failure");
                        // Toast fail
                        Toast.makeText(getActivity(),getContext().getString(R.string.edit_item_failure),Toast.LENGTH_LONG).show();
                    }
                }, params);
            }
        });

        // Delete item
        delete.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {

                RequestParams params = new RequestParams();
                params.put("token", token);
                params.put("id", idItem);

                ItemService.remove(new AsyncHttpResponseHandler() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        Log.i(TAG, "Delete item success =>"+ new String(responseBody));
                        try {
                            JSONObject jsonResponse = new JSONObject(new String(responseBody));
                            if(jsonResponse.getInt("code") == 0) {
                                Toast.makeText(getActivity(),getContext().getString(R.string.del_item_success),Toast.LENGTH_SHORT).show();
                                Intent ListActivityIntent = new Intent(getContext(), ListActivity.class);
                                startActivity(ListActivityIntent);
                            }
                            else {
                                Toast.makeText(getActivity(),getContext().getString(R.string.del_item_failure),Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Log.i(TAG, "Delete item failure");
                        // Toast fail
                        Toast.makeText(getActivity(),getContext().getString(R.string.del_item_failure),Toast.LENGTH_LONG).show();
                    }
                }, params);
            }
        });
    }

}
