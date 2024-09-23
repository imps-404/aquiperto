package com.example.firebase1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;

public class SearchShopActivity extends AppCompatActivity implements RecycleViewInterface{

    SearchView searchView;
    RecyclerView recyclerView;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    ArrayList<Shop> shopArrayList;

    ImageView imageView;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //FirebaseAuth.getInstance().signOut();
        Intent i = new Intent(
                SearchShopActivity.this,
                MainMenuActivity.class);
        startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchshop);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        recyclerView = findViewById(R.id.recyclerView02);

        searchView = findViewById(R.id.searchView2);
        searchView.clearFocus();
        searchView.setIconified(false);

        manageList();

    }
    //Recycleview adapter
    private void setAdapter(ArrayList<Shop> shopArrayList_) {
        this.shopArrayList = shopArrayList_;
        RecyclerAdapter recyclerAdapter = new RecyclerAdapter(shopArrayList_, this);

        recyclerAdapter.getActivity(SearchShopActivity.this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(recyclerAdapter);
        recyclerAdapter.startFirebase();
        searchView.clearFocus();

    }

    private void manageList() {
                FirebaseApp.initializeApp(SearchShopActivity.this);
                firebaseDatabase = firebaseDatabase.getInstance();
                databaseReference = firebaseDatabase.getReference();
                //String user = FirebaseAuth.getInstance().getUid();
                final ArrayList<Shop> shopArrayList = new ArrayList<>();
                setAdapter(shopArrayList);
                databaseReference.child("DB").child("Shop").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        shopArrayList.clear();//dupe
                        Iterable<DataSnapshot> children = snapshot.getChildren();
                        for (DataSnapshot dns : children) {
                            Shop shop = dns.getValue(Shop.class);
                            UserExp ue = new UserExp();
                            ue.setRating ((ArrayList) dns.child("userexp").child("rating").getValue());
                            ue.setComments((ArrayList) dns.child("userexp").child("comments").getValue());

                            shop.setUserExp(ue);

                            shopArrayList.add(shop);
                            System.out.println("gg"+dns.child("userexp").getValue());
                            System.out.println("gg"+shop.toString());
                        }
                        setAdapter(shopArrayList);
                    }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {


            }

        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                ArrayList<Shop> filteredList = new ArrayList<>();
                for (Shop shop : shopArrayList) {
                    if (shop.getType().toLowerCase(Locale.ROOT).contains(s.toLowerCase())) {
                        filteredList.add(shop);

                    }
                }
                if (filteredList.isEmpty()) {
                    Toast.makeText(SearchShopActivity.this, "not found", Toast.LENGTH_SHORT).show();
                } else {
                    setAdapter(filteredList);
                }
                return false;
            }
        });


    }
    @Override
    public void onClick(int position) {
        Intent i = new Intent(
                SearchShopActivity.this,
                ShopMainActivity.class
        );

        i.putExtra( "NAME", shopArrayList.get(position).getName());
        i.putExtra( "ADDRESS", shopArrayList.get(position).getAddress());
        i.putExtra( "TYPE", shopArrayList.get(position).getType());
        i.putExtra( "CEP", shopArrayList.get(position).getNumber());
        i.putExtra( "PHONE", shopArrayList.get(position).getPhone());
        i.putExtra( "IMAGE", shopArrayList.get(position).getImage());
        i.putExtra( "ID", shopArrayList.get(position).getId());
        String rat="";

        String comments="";
        if(shopArrayList.get(position).getUserExp() != null) {
            int c=0;
            for (c = 0; c < shopArrayList.get(position).getUserExp().getComments().size() ; c++) {


                comments += shopArrayList.get(position).getUserExp().getComments().get(c)+"###;";


                rat+=shopArrayList.get(position).getUserExp().getRating().get(c)+",";
                Log.wtf("zzzz", "zzzz" + c + comments);
            }
            Log.wtf("rat", "rat" +rat);
            i.putExtra("COMMENTS", comments);
            i.putExtra( "RATING", rat);
        }
        else
        {
            i.putExtra("COMMENTS", "");
            i.putExtra( "RATING", 0);
        }

        startActivity(i);
    }
}