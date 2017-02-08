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
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.esgi.freres.androidproject.Classes.ShoppingList;
import com.esgi.freres.androidproject.Fragments.CreateListFragment;
import com.esgi.freres.androidproject.R;
import com.esgi.freres.androidproject.Webservices.ListService;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Main activity";

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String token = sp.getString("token", null);

        final ListView listV = (ListView) findViewById(R.id.shoppingList);
        final Activity context = this;

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Fragment create list
                FragmentManager fm = getFragmentManager();
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                fragmentTransaction.replace(R.id.container_main, new CreateListFragment());
                fragmentTransaction.commit();
                setContentView(R.layout.activity_main);
            }
        });

        // Appel de ListService.list
        RequestParams params = new RequestParams();
        params.put("token", token);

        ListService.list(new AsyncHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.i(TAG, "Get list success => "+ new String(responseBody));
                JSONObject jsonResponse = null;
                try {
                    jsonResponse = new JSONObject(new String(responseBody));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                final ArrayList<String> shoppingList = new ArrayList<>();
                final ArrayList<Integer> shoppingListIds = new ArrayList<Integer>();
                JSONArray resultJSON = null;
                try {
                    resultJSON = jsonResponse.getJSONArray("result");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                for (int i = 0; i < resultJSON.length(); i++) {
                    ShoppingList parsedShoppingList = new ShoppingList();
                    JSONObject shoppingListJSON = null;
                    try {
                        shoppingListJSON = resultJSON.getJSONObject(i);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        parsedShoppingList.setId(shoppingListJSON.getInt("id"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        parsedShoppingList.setName(shoppingListJSON.getString("name"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Boolean completed = false;
                    try {
                        if(shoppingListJSON.getInt("completed") == 0){
                            completed = false;
                        } else {
                            completed = true;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    parsedShoppingList.setCompleted(completed);
                    shoppingList.add(parsedShoppingList.getName());
                    shoppingListIds.add(parsedShoppingList.getId());
                }

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, shoppingList);
                listV.setAdapter(arrayAdapter);
                listV.setEmptyView(findViewById(R.id.empty_lists_list));
                listV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                        // List Activity
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putInt("idList", shoppingListIds.get(position));
                        editor.putString("nameList", shoppingList.get(position));
                        editor.commit();

                        Intent ListActivityIntent = new Intent(getApplicationContext(), ListActivity.class);
                        startActivity(ListActivityIntent);
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
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("token", null);
            editor.commit();

            Intent LoginActivityIntent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(LoginActivityIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        int count = getFragmentManager().getBackStackEntryCount();

        if (count == 0) {
            super.onBackPressed();
        } else {
            getFragmentManager().popBackStack();
        }
    }
}
