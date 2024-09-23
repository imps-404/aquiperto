package com.example.firebase1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class RecoverActivity extends AppCompatActivity {

    EditText etRecPw;
    Button btRecPw;
    FirebaseAuth mAuthRecover;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_recover);

        etRecPw = findViewById(R.id.editRecPw);
        btRecPw = findViewById(R.id.buttonRecPw);

        mAuthRecover = FirebaseAuth.getInstance();

        //send reset email
        btRecPw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = etRecPw.getText().toString();

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches() || email.equals("")) {
                    etRecPw.setError("Invalid email");
                    return;
                }

                mAuthRecover.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(RecoverActivity.this, "E-mail sent.", Toast.LENGTH_LONG).show();
                            Intent i = new Intent(RecoverActivity.this, LoginMenuActivity.class);
                            startActivity(i);
                        } else
                            Toast.makeText(RecoverActivity.this, "Ops, something went wrong...", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

    }
}