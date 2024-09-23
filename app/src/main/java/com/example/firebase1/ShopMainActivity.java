package com.example.firebase1;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class ShopMainActivity extends AppCompatActivity {

    TextView textView, textViewRating;

    String fullAddress="";
    ImageView imgView, imgMap;
    Button btn, btnComment;
    AlertDialog.Builder builder;
    FirebaseAuth mAuthLogin;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //FirebaseAuth.getInstance().signOut();
        Intent i = new Intent(
                ShopMainActivity.this,
                SearchShopActivity.class);
        startActivity(i);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_shop_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        String name = getIntent().getStringExtra("NAME");
        String address = getIntent().getStringExtra("ADDRESS");
        String type = getIntent().getStringExtra("TYPE");
        String cep = getIntent().getStringExtra("CEP");
        String phone = getIntent().getStringExtra("PHONE");
        String image = getIntent().getStringExtra("IMAGE");
        String id = getIntent().getStringExtra("ID");
        String comm = getIntent().getStringExtra("COMMENTS");
        String rat = getIntent().getStringExtra("RATING");

        fullAddress+=address+", "+cep;

        textView = findViewById(R.id.textView2);
        textView.setSingleLine(false);
       // textViewRating = findViewById(R.id.textViewRating);
        textView.setText("Nome: " +name+"\n"+
                "Profissao: " +type+"\n"+
                "Endereco: " +address+"\n"+
                "CEP: " +cep+"\n"+
                "Contato: " +phone+"\n"
        );
        imgMap =  findViewById(R.id.imageView5);
       // textViewRating.setText(rat);
        loadImage(image);
        btn = findViewById(R.id.button4);
        btnComment = findViewById(R.id.btnComments);


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(
                        ShopMainActivity.this,
                        SearchShopActivity.class
                );
                startActivity(i);
            }
        });
        imgMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(
                        ShopMainActivity.this,
                        MapActivity.class
                );
                i.putExtra( "ADDRESS", fullAddress);
                startActivity(i);
            }
        });

       dialogSet(comm, rat, id);

    }
    public void loadImage(String imageURL)
    {
        imgView = findViewById(R.id.imageViewPhoto);
        try {
            Picasso.get()
                    .load(imageURL)
                    .resize(60, 60)
                    .into(imgView);
        } catch (Exception e) {
            Picasso.get().load(R.drawable.poro).into(imgView);
        }
    }
    public void dialogSet(String comm, String rat, String id)
    {
        //Button Comments
        builder = new AlertDialog.Builder(this);
        btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                // 1. Instantiate an AlertDialog.Builder with its constructor.


                // 2. Chain together various setter methods to set the dialog characteristics.
                builder.setMessage(SpaceComments(comm))
                        .setTitle("Comentarios");



                builder.setPositiveButton("Feedback", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
                            Intent i = new Intent(
                                    ShopMainActivity.this,
                                    FeedbackActivity.class
                            );

                            i.putExtra("RAT", rat);
                            i.putExtra("ID", id);
                            i.putExtra("COMM", comm);


                            startActivity(i);
                        }
                        else
                        {
                            Intent i = new Intent(
                                    ShopMainActivity.this,
                                    LoginMenuActivity.class
                            );
                            startActivity(i);
                        }
                    }
                });
                builder.setNegativeButton("Cancelar", null);
                // 3. Get the AlertDialog.
                AlertDialog dialog = builder.create();

                dialog.show();
            }
        });
    }
    public String SpaceComments(String c)
    {
        String s ="";
        try
        {
            String myStr = c;
            String regex = "###;";
            String[] myArray = myStr.split(regex);
            for (int i = 0; i < myArray.length ; i++)
            {
                //we only want the last 3
                if(myArray.length-i<=3)
                s+= "\n"+" Comentario: "+myArray[i]+"\n\n";
            }
        }
        catch (Exception e)
        {
            Log.wtf("Feedback", "Feedback Failed to space comments!");
        }

        return s;
    }


}