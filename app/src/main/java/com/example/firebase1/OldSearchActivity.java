package com.example.firebase1;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.SearchView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.Scanner;

public class OldSearchActivity extends AppCompatActivity implements RecycleViewInterface{

    Button bt1;
    SearchView searchView;
    RecyclerView recyclerView;

    //logout
    /*
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        FirebaseAuth.getInstance().signOut();
        Intent i = new Intent(
                SearchActivity.this,
                LoginMenuActivity.class);
        startActivity(i);
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_main_search);

        recyclerView = findViewById(R.id.recyclerView01);
        bt1 = findViewById(R.id.button);
        searchView = findViewById(R.id.searchView);
        searchView.clearFocus();
        searchView.setIconified(false);

        //json async class
       // new DownloadJSonAsyncTask().execute("http://ddragon.leagueoflegends.com/cdn/12.22.1/data/en_US/champion.json");
        AsyncTask<String, Void, ArrayList<Shop>> execute = new DownloadJSonAsyncTask().execute("https://fireb-708d9-default-rtdb.firebaseio.com/Shop.json");

        //Fav button
        /*
        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(
                        SearchActivity.this,
                        FavoritesActivity.class);
                startActivity(i);
            }
        });
*/

    }
    //Recycleview adapter
    private void setAdapter(ArrayList<Shop> shopArrayList_) {
        RecyclerAdapter recyclerAdapter = new RecyclerAdapter(shopArrayList_,this);

        recyclerAdapter.getActivity(OldSearchActivity.this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(recyclerAdapter);
        recyclerAdapter.startFirebase();
        searchView.clearFocus();
    }

    @Override
    public void onClick(int position) {

    }

    //Async class
    public class DownloadJSonAsyncTask extends AsyncTask<String, Void, ArrayList<Shop>> {

        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(OldSearchActivity.this, "Loading",
                    "Downloading...");
        }

        @Override
        protected ArrayList<Shop> doInBackground(String... params) {

            String urlString = params[0];
            URL url;

            try {
                url = new URL(urlString);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setReadTimeout(15000000);
                httpURLConnection.connect();

                InputStream response = httpURLConnection.getInputStream();

                String text = new Scanner(response).useDelimiter("\\A").next();

                if (text != null) {
                    ArrayList<Shop> shop = getData(text);
                    return shop;
                } else
                {
                    System.out.println("vazio");
                    return null;
                }


            }
            catch (MalformedURLException e) {
                e.printStackTrace();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Shop> shopArrayList) {
            super.onPostExecute(shopArrayList);

            dialog.dismiss();

            setAdapter(shopArrayList);

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    ArrayList<Shop> filteredList = new ArrayList<>();
                    for (Shop shop : shopArrayList) {
                        if (shop.getName().toLowerCase(Locale.ROOT).contains(s.toLowerCase())) {
                            filteredList.add(shop);
                        }
                    }
                    if (filteredList.isEmpty()) {
                        Toast.makeText(OldSearchActivity.this, "not found", Toast.LENGTH_LONG).show();
                    } else {
                        setAdapter(filteredList);
                    }
                    return false;
                }
            });

        }

        private ArrayList<Shop> getData(String text) {
            ArrayList shopArrayList = new ArrayList<>();

            try {
                JSONObject jItem = new JSONObject(text.trim());
                JSONObject jsonObjectItem = new JSONObject((jItem.getJSONObject("data").toString()));
                Iterator<?> keys = jsonObjectItem.keys();
                int i = 0;
                while (keys.hasNext()) {
                    String key = (String) keys.next();

                    if (jsonObjectItem.get(key) instanceof JSONObject) {
                        Shop shop = new Shop();

                      // champion.setVersion(jsonObjectItem.getJSONObject(key).getString("version"));
                      //  champion.setId(jsonObjectItem.getJSONObject(key).getString("id"));
                        shop.setAddress(jsonObjectItem.getJSONObject(key).getString("key"));
                        shop.setName(jsonObjectItem.getJSONObject(key).getString("name"));
                      //  champion.setTitle(jsonObjectItem.getJSONObject(key).getString("title"));
                      //  champion.setFull(jsonObjectItem.getJSONObject(key).getJSONObject("image").getString("full"));

                        shopArrayList.add(shop);
                        System.out.println(shopArrayList.get(i).toString());
                        i++;

                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return shopArrayList;
        }
    }


}