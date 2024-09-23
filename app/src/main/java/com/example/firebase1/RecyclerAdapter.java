package com.example.firebase1;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {

    private final RecycleViewInterface recycleViewInterface;
   // ArrayList<Champion> championArrayList;
    ArrayList<Shop> shopArrayList;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    Activity refActivity;

    public RecyclerAdapter(ArrayList<Shop> shopArrayList, RecycleViewInterface recycleViewInterface) {
        this.shopArrayList = shopArrayList;
        this.recycleViewInterface = recycleViewInterface;
    }
    public void filteredList(ArrayList<Shop> shopArrayList) {
        this.shopArrayList = shopArrayList;
    }

    void getActivity(Activity m) {
        refActivity = m;
    }

    void startFirebase() {
        FirebaseApp.initializeApp(refActivity);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView mTextViewNome;
        TextView mTextViewTitle;
        ImageView mImageView;
        ImageView mImageView2;

        //static not recommencded
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            mTextViewNome = itemView.findViewById(R.id.textView);
            mTextViewTitle = itemView.findViewById(R.id.textView1);
            mImageView = itemView.findViewById(R.id.imageView1);
            mImageView2 = itemView.findViewById(R.id.imageView2);
            mImageView2.setVisibility(View.INVISIBLE);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(recycleViewInterface != null)
                    {
                        int pos= getAdapterPosition();
                        if(pos!= RecyclerView.NO_POSITION)
                            recycleViewInterface.onClick(pos);
                    }
                }
            });
        }


        @Override
        public void onClick(View view) {
/*
            if (refActivity instanceof SearchActivity && mImageView2.getVisibility() == View.INVISIBLE) {

                AlertDialog alertDialog = new AlertDialog.Builder(refActivity).create();
                alertDialog.setTitle("Favorite");
                alertDialog.setMessage("Do you want to add " + championArrayList.get(getLayoutPosition()).getName() + " to your favorites?");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "YES", (dialog, which) -> {
                    Toast.makeText(view.getContext(), "Added to favorites..", Toast.LENGTH_SHORT).show();
                    save(getLayoutPosition());
                    Objects.requireNonNull(alertDialog.getWindow()).setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);//.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                    dialog.dismiss();
                });
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "NO", (dialog, which) -> {
                    Objects.requireNonNull(alertDialog.getWindow()).setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);//.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                    dialog.dismiss();
                });

                alertDialog.show();


            } else if (refActivity instanceof FavoritesActivity) {
                AlertDialog alertDialog = new AlertDialog.Builder(refActivity).create();
                alertDialog.setTitle("Unfavorite");
                alertDialog.setMessage("Do you want to remove " + championArrayList.get(getLayoutPosition()).getName() + " from your favorites?");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "YES", (dialog, which) -> {
                    Toast.makeText(view.getContext(), "Removed from favorites..", Toast.LENGTH_SHORT).show();
                    removeAt(getLayoutPosition());
                    dialog.dismiss();
                });
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "NO", (dialog, which) -> dialog.dismiss());
                Objects.requireNonNull(alertDialog.getWindow()).setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                alertDialog.show();
            }
*/

        }

        private void save(int layoutPosition) {
            Shop c = shopArrayList.get(layoutPosition);
            String user = FirebaseAuth.getInstance().getUid();
            databaseReference.child("DB").child(c.getName()).setValue(c);
            mImageView2.setVisibility(View.VISIBLE);
        }

        private void removeAt(int layoutPosition) {

            databaseReference = FirebaseDatabase.getInstance().getReference();
            String user = FirebaseAuth.getInstance().getUid();
            databaseReference.child("Shop").child(shopArrayList.get(layoutPosition).getName()).setValue(null);
            shopArrayList.remove(layoutPosition);
            notifyItemRemoved(layoutPosition);
           // notifyItemRangeChanged(layoutPosition, championArrayList.size());


        }
    }

    @NonNull
    @Override
    public RecyclerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType ) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_design, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapter.MyViewHolder holder, int position) {
        System.out.println("in the holder");

        String name = shopArrayList.get(position).getName();
        String title = shopArrayList.get(position).getType();
        String imageURL = shopArrayList.get(position).getImage();//"https://fireb-708d9-default-rtdb.firebaseio.com/" + shopArrayList.get(position).getName();



        String user = FirebaseAuth.getInstance().getUid();
        DatabaseReference root = FirebaseDatabase.getInstance().getReference();
        DatabaseReference users = root.child("Shop");
        databaseReference.child("Shop").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.child(name).exists()) {
                    holder.mImageView2.setVisibility(View.VISIBLE);
                } else {
                    holder.mImageView2.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Picasso.get().load(R.drawable.star).into(holder.mImageView2);

        holder.mTextViewNome.setText(name);
        holder.mTextViewTitle.setText(title);

        try {
            Picasso.get()
                    .load(imageURL)
                    .resize(60, 60)
                    .into(holder.mImageView);
        } catch (Exception e) {
            holder.mTextViewNome.setText("Not found");
            holder.mTextViewTitle.setText("");
            Picasso.get().load(R.drawable.poro).into(holder.mImageView);
        }

    }
    @Override
    public int getItemCount() {

        return shopArrayList.size();

    }


}