package com.example.firebase1;

import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class FeedbackActivity extends AppCompatActivity {
    RatingBar ratingbar;
    Button buttonSend, buttonBack, button;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    TextView tvRating;
    UserExp ue = new UserExp();

    double rate =-1;

    String newComm= "";
    AlertDialog.Builder builder;
    EditText et;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_feedback);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        tvRating = findViewById((R.id.textViewRating));
        String rat = getIntent().getStringExtra("RAT");
        String id = getIntent().getStringExtra("ID");
        String comm = getIntent().getStringExtra("COMM");
        double rating= parseRating(rat);
        Log.wtf("rating", "rating"+ String.valueOf(rating));
        tvRating.setText(String.valueOf(rating));
      //  addListenerOnButtonClick();
        ratingbar = findViewById(R.id.ratingBar);

        buttonSend = findViewById(R.id.button5);
        buttonBack = findViewById(R.id.button3);
        et = findViewById(R.id.editTextTextMultiLine);
        buttonSend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //if(rate < 0 || newComm!= "")
                //{
                    fillUE(ue,rat,comm);
                    sendInfo(ue, id);
                    Intent i = new Intent(
                            FeedbackActivity.this,
                            SearchShopActivity.class
                    );
                    startActivity(i);
               // }
               // else
               // {
               //     builder = new AlertDialog.Builder(FeedbackActivity.this);
                //    builder.setMessage("Porfavor, insira as informacoes")
                //            .setTitle("Comentarios");
                //    AlertDialog dialog = builder.create();

                  //  dialog.show();
                //}
            }
        });


    }
    public void sendInfo(UserExp ue, String shopID)
    {

        FirebaseApp.initializeApp(FeedbackActivity.this);
        firebaseDatabase = firebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        //String user = FirebaseAuth.getInstance().getUid();
        DatabaseReference dr = databaseReference.child("DB").child("Shop").child(shopID);

       // dr.child("id").setValue(shop.getId());
        //ue.setRating ((ArrayList) dns.child("userexp").child("rating").getValue());
      //  ue.setComments((ArrayList) dns.child("userexp").child("comments").getValue());
        try {


            dr.child("userexp").child("rating").setValue(ue.getRating());
            dr.child("userexp").child("comments").setValue(ue.getComments());
        }
        catch (Exception e)
        {
            Log.wtf("Feedback", "Feedback Failed to save to Firebase!");
        }


    }
    public void addListenerOnButtonClick() {
        ratingbar = findViewById(R.id.ratingBar);
        button =  findViewById(R.id.button);
        //Performing action on Button Click
        button.setOnClickListener(arg0 -> {
            //Getting the rating and displaying it on the toast
            String rating = String.valueOf(ratingbar.getRating());
            Toast.makeText(getApplicationContext(), rating, Toast.LENGTH_LONG).show();
        });

    }
    public void fillUE(UserExp ue, String rat, String comments)
    {
        newComm=et.getText().toString();

        //parse and save new comments
        try {
            String myStr = rat;
            String regex = ",";
            String[] myArray = myStr.split(regex);
            List<Double> iList = new ArrayList<Double>();
            for (String s : myArray) {
                iList.add(Double.parseDouble(s));
            }
            rate = ratingbar.getRating();
            iList.add(rate);
            ue.setRating(iList);
        }
        catch (Exception e)
        {
            Log.wtf("Feedback", "Feedback Failed to parse rating!");

        }
        try
        {
            String myStr = comments;
            String regex = "###;";
            String[] myArray = myStr.split(regex);
            List<String> sList = new ArrayList<String>();
            for (String s : myArray) {
                sList.add(s);
            }

            sList.add(newComm);
            ue.setComments(sList);
        }
        catch (Exception e)
        {
            Log.wtf("Feedback", "Feedback Failed to parse comments!");

        }
        Log.wtf("Feedback", "Feedback "+ue.getRating().toString());
        Log.wtf("Feedback", "Feedback "+ue.getComments().toString());


    }
    public double parseRating(String rat)
    {

        double ret=0;
        String myStr = rat;
        String regex = ",";
        String[] myArray = myStr.split(regex);
        for (String s : myArray) {
            System.out.println(s);
        }
        int i=0;
        for(i=0; i<myArray.length; i++)
        {
            try
            {
                ret+=Double.parseDouble(myArray[i]);
            }
            catch (Exception e)
            {

            }
        }

            return ret/=i;
    }

}