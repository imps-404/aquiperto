package com.example.firebase1;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicMarkableReference;

public class ShopEditActivity extends AppCompatActivity {
    Button btnBack, btnEdit, btnUpload;
    EditText etName, etAddress, etNumber, etPhone,etType;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    ArrayList<Shop> shopArrayList;
    Shop shop = new Shop();
    private final int GALLERY_REQ_CODE = 1000;
    private String linkName ="";
    String linkTemp;
    ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_shop_edit);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        new DownloadJSonAsyncTask().execute("https://fireb-708d9-default-rtdb.firebaseio.com/DB.json");
        AsyncTask<String, Void, Shop> execute = new DownloadJSonAsyncTask().execute("https://fireb-708d9-default-rtdb.firebaseio.com/DB.json");

    }
    public class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {

        private String url;
        private ImageView imageView;

        public ImageLoadTask(String url, ImageView imageView) {
            this.url = url;
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            try {
                URL urlConnection = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) urlConnection
                        .openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            imageView.setImageBitmap(result);
        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode==RESULT_OK)
        {
            if(requestCode==GALLERY_REQ_CODE)
            {
                img.setImageURI(data.getData());
                try {
                    uploadImage();
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }

    }

    public void fillFields()
    {

        FirebaseApp.initializeApp(ShopEditActivity.this);
        firebaseDatabase = firebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        String user = FirebaseAuth.getInstance().getUid();

        databaseReference.child("DB").child("Shop").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task)
            {
                if (!task.isSuccessful())
                {
                    Log.wtf("firebase", "Error getting data", task.getException());

                }
                else
                {
                    Log.wtf("firebase", String.valueOf(task.getResult().child(user).getValue()));


                    etName.setText( String.valueOf(task.getResult().child(user).child("name").getValue()));
                    etAddress.setText( String.valueOf(task.getResult().child(user).child("address").getValue()));
                    etNumber.setText(String.valueOf(task.getResult().child(user).child("number").getValue()));
                    etPhone.setText( String.valueOf(task.getResult().child(user).child("phone").getValue()));
                    etType.setText( String.valueOf(task.getResult().child(user).child("type").getValue()));
                    linkTemp= String.valueOf(task.getResult().child(user).child("image").getValue());
                }
            }
        });

    }
    public Bitmap getBitmapfromUrl(String imageUrl)
    {
        try
        {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            return bitmap;

        } catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;

        }
    }
    public void setShop() throws FileNotFoundException {
        String user = FirebaseAuth.getInstance().getUid();


        shop.setId(user);
        shop.setName(etName.getText().toString());
        shop.setType(etType.getText().toString());
        shop.setAddress(etAddress.getText().toString());
        shop.setNumber(etNumber.getText().toString());
        shop.setPhone(etPhone.getText().toString());
        //if(linkName!="")
            shop.setImage(linkName);


        Log.wtf("set shop", shop.toString());
    }
    public void uploadImage() throws FileNotFoundException
    {

        FirebaseStorage storage = FirebaseStorage.getInstance();
        // Create a storage reference from our app

        StorageReference storageRef = storage.getReference();
        String user = FirebaseAuth.getInstance().getUid();
        // Create a reference to "mountains.jpg"
        StorageReference mountainsRef = storageRef.child("mountains.jpg");

        // Create a reference to 'images/mountains.jpg'
        StorageReference mountainImagesRef = storageRef.child("images/mountains.jpg");

        // While the file names are the same, the references point to different files
        mountainsRef.getName().equals(mountainImagesRef.getName());    // true
        mountainsRef.getPath().equals(mountainImagesRef.getPath());    // false

        // Get the data from an ImageView as bytes
        img.setDrawingCacheEnabled(true);
        img.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) img.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = mountainsRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Log.i("urlimage", "fake and gay");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
                Task<Uri> result = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String photoStringLink = uri.toString();
                        Log.wtf("image shop2", photoStringLink);
                        linkName = photoStringLink;
                    }
                });
            }
        });


    }
    public void updateFields()
    {
        try
        {
            setShop();

        }
        catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        FirebaseApp.initializeApp(ShopEditActivity.this);
        firebaseDatabase = firebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        String user = FirebaseAuth.getInstance().getUid();
        DatabaseReference dr = databaseReference.child("DB").child("Shop").child(user);

        dr.child("id").setValue(shop.getId());
        dr.child("name").setValue(shop.getName());
        dr.child("type").setValue(shop.getType());
        dr.child("address").setValue(shop.getAddress());
        dr.child("number").setValue(shop.getNumber());
        dr.child("phone").setValue(shop.getPhone());

        if(shop.getImage()!="") {
            dr.child("image").setValue(shop.getImage());
        }

    }
    private class DownloadImageFromInternet extends AsyncTask<String, Void, Bitmap> {
        ImageView imageView;
        public DownloadImageFromInternet(ImageView imageView) {
            this.imageView=imageView;
            Toast.makeText(getApplicationContext(), "Please wait, it may take a few minute...",Toast.LENGTH_SHORT).show();
        }
        protected Bitmap doInBackground(String... urls) {
            String imageURL=urls[0];
            Bitmap bimage=null;
            try {
                InputStream in=new java.net.URL(imageURL).openStream();
                bimage= BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error Message", e.getMessage());
                e.printStackTrace();
            }
            return bimage;
        }
        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
        }
    }
    public class DownloadJSonAsyncTask extends AsyncTask<String, Void, Shop> {
        ProgressDialog dialog;
        Shop shoperoni;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(ShopEditActivity.this, "Loading",
                    "Downloading...");
        }

        @Override
        protected Shop doInBackground(String... params)
        {
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
                    Shop shop = getData(text);
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
        protected void onPostExecute(Shop shops) {
            super.onPostExecute(shops);
            dialog.dismiss();

            etName = findViewById(R.id.editTextName);
            etAddress = findViewById(R.id.editTextAddress);
            etNumber = findViewById(R.id.editTextCEP);
            etPhone = findViewById(R.id.editTextPhone);
            etType = findViewById(R.id.editTextType);

            img = findViewById(R.id.imageViewUp);

            btnBack = findViewById(R.id.btnBack);
            btnBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(
                            ShopEditActivity.this,
                            LoggedMenuActivity.class
                    );
                    startActivity(i);
                }
            });

            try
            {


                fillFields();
                setShop();
                try {
                    Picasso.get()
                            .load(shop.getImage())
                            .resize(60, 60)
                            .into(img);
                } catch (Exception e) {

                    Picasso.get().load(R.drawable.poro).into(img);
                }

            }
            catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }

            btnEdit = findViewById(R.id.buttonInsert);
            btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    updateFields();

                    Intent i = new Intent(
                            ShopEditActivity.this,
                            LoggedMenuActivity.class
                    );
                    startActivity(i);
                }
            });

            btnUpload = findViewById(R.id.buttonInsertImage);
            btnUpload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    Intent i = new Intent(Intent.ACTION_PICK);
                    i.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i,GALLERY_REQ_CODE);
                }
            });



        }
        private Shop getData(String text) {
            Shop shops = new Shop();

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

            return shops;
        }
    }
}