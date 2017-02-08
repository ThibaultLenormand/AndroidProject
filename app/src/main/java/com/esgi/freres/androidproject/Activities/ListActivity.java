package com.esgi.freres.androidproject.Activities;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.esgi.freres.androidproject.Classes.Item;
import com.esgi.freres.androidproject.Fragments.CreateItemFragment;
import com.esgi.freres.androidproject.Fragments.ItemFragment;
import com.esgi.freres.androidproject.R;
import com.esgi.freres.androidproject.Webservices.ItemService;
import com.esgi.freres.androidproject.Webservices.ListService;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class ListActivity extends AppCompatActivity{

    private static final String TAG = "List activity";

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final String token = sp.getString("token", null);
        final Integer idList = sp.getInt("idList", 0);
        final String nameList = sp.getString("nameList", null);

        final ListView listV = (ListView) findViewById(R.id.itemList);
        final Activity context = this;
        final RequestParams params = new RequestParams();

        android.support.v7.app.ActionBar ab = getSupportActionBar();
        if(nameList != null && ab != null) {
            ab.setTitle(nameList);
        }

        // Edit list
        /*
        Button editButton = (Button) findViewById(R.id.editListButton);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                params.put("token", token);
                ListService.edit(new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                    }
                }, params);

                Toast.makeText(getApplicationContext(), getResources().getString(R.string.coming_soon),Toast.LENGTH_LONG).show();
            }
        });
        */

        // Add item
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Fragment create item
                FragmentManager fm = getFragmentManager();
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                fragmentTransaction.replace(R.id.container_list, new CreateItemFragment());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                setContentView(R.layout.activity_list);
            }
        });

        // Appel de ItemService.list

        params.put("token", token);
        params.put("shopping_list_id", idList);
        ItemService.list(new AsyncHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.i(TAG, "Get list success => "+ new String(responseBody));
                JSONObject jsonResponse = null;
                try {
                    jsonResponse = new JSONObject(new String(responseBody));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                final ArrayList<String> itemList = new ArrayList<>();
                final ArrayList<Integer> itemIdList = new ArrayList<>();
                final ArrayList<String> itemNameList = new ArrayList<>();
                final ArrayList<Integer> itemQtyList = new ArrayList<>();
                final ArrayList<Double> itemPriceList = new ArrayList<>();

                JSONArray resultJSON = null;
                try {
                    resultJSON = jsonResponse.getJSONArray("result");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                for (int i = 0; i < resultJSON.length(); i++) {
                    Item parsedItemList = new Item();
                    JSONObject itemListJSON = null;
                    try {
                        itemListJSON = resultJSON.getJSONObject(i);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        parsedItemList.setId(itemListJSON.getInt("id"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        parsedItemList.setName(itemListJSON.getString("name"));
                        parsedItemList.setQuantity(itemListJSON.getInt("quantity"));
                        parsedItemList.setPrice(itemListJSON.getDouble("price"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    itemList.add("x"+parsedItemList.getQuantity()+" "+parsedItemList.getName()+"  =>  $"+parsedItemList.getPrice());

                    itemIdList.add(parsedItemList.getId());
                    itemNameList.add(parsedItemList.getName());
                    itemQtyList.add(parsedItemList.getQuantity());
                    itemPriceList.add(parsedItemList.getPrice());
                }

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, itemList);
                listV.setAdapter(arrayAdapter);
                listV.setEmptyView(findViewById(R.id.empty_list_view));
                listV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                        // Set SharedPreferences
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("token", token);
                        editor.putInt("idItem", itemIdList.get(position));
                        editor.putString("nameItem", itemNameList.get(position));
                        editor.putInt("qtyItem", itemQtyList.get(position));
                        editor.putString("priceItem", itemPriceList.get(position).toString());
                        editor.commit();

                        // Fragment edit item
                        FragmentManager fm = getFragmentManager();
                        FragmentTransaction fragmentTransaction = fm.beginTransaction();
                        fragmentTransaction.replace(R.id.container_list, new ItemFragment());
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                        setContentView(R.layout.activity_list);
                    }
                });
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.i(TAG, "Get list fail => "+ new String(responseBody));
            }
        }, params);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final String token = sp.getString("token", null);
        final Integer idList = sp.getInt("idList", 0);
        final RequestParams params = new RequestParams();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_del_list) {
            params.put("token", token);
            params.put("id", idList);
            ListService.remove(new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    JSONObject jsonResponse = null;
                    try {
                        jsonResponse = new JSONObject(new String(responseBody));
                        Log.i(TAG, jsonResponse.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        if(jsonResponse.getInt("code") == 0) {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.del_list_success),Toast.LENGTH_LONG).show();
                            Intent MainActivityIntent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(MainActivityIntent);
                        }
                        else {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.del_list_failure),Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                }
            }, params);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        int count = getFragmentManager().getBackStackEntryCount();
        Log.i(TAG, String.valueOf(count));
        if (count == 0) {
            super.onBackPressed();
            Intent MainActivityIntent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(MainActivityIntent);
        } else {
            Intent ListActivityIntent = new Intent(getApplicationContext(), ListActivity.class);
            startActivity(ListActivityIntent);
        }
    }
}
