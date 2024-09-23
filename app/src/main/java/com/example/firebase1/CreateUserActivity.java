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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class CreateUserActivity extends AppCompatActivity {

    EditText etName, etEmail, etPw;
    Button btCreate;

    FirebaseAuth mAuthNewUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_create_user);

        etName = findViewById(R.id.editCreateName);
        etEmail = findViewById(R.id.editCreateEmail);
        etPw = findViewById(R.id.editCreatePassword);
        btCreate = findViewById(R.id.buttonCreate);

        mAuthNewUser = FirebaseAuth.getInstance();

        btCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createUser(etEmail.getText().toString(), etPw.getText().toString());
            }
        });

    }

    private void createUser(String email, String pw) {

        if (etName.getText().toString().equals("")) {
            etName.setError("Please try again..");
            etName.requestFocus();
            return;
        }

        if (email.equals("") || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Please try again..");
            etEmail.requestFocus();
            return;
        }

        if (pw.equals("")) {
            etPw.setError("Please try again..");
            etPw.requestFocus();
            return;
        }


        //
        mAuthNewUser.createUserWithEmailAndPassword(email, pw).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "User " +
                            "successfully created!", Toast.LENGTH_LONG).show();

                    Intent i = new Intent(CreateUserActivity.this,
                            LoginMenuActivity.class);
                    startActivity(i);
                } else
                    Toast.makeText(getApplicationContext(), "Ops! Something went wrong..",
                            Toast.LENGTH_LONG).show();
            }
        });
    }
}