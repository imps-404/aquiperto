package com.example.firebase1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginMenuActivity extends AppCompatActivity {

    EditText edEmail, edPw;
    Button btLogin;
    TextView tv1Create, tv1Recover;
    FirebaseAuth mAuthLogin;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login_menu);

        edEmail = findViewById(R.id.editTextEmail);
        edPw = findViewById(R.id.editTextSenha);
        btLogin = findViewById(R.id.buttonLogar);
        tv1Create = findViewById(R.id.textViewCriarUsuario);
        tv1Recover = findViewById(R.id.textViewEsqueciSenha);
        progressBar = findViewById(R.id.progressBar);

        mAuthLogin = FirebaseAuth.getInstance();

        progressBar.setVisibility(View.GONE);

        //New user
        tv1Create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(
                        LoginMenuActivity.this,
                        CreateUserActivity.class
                );
                startActivity(i);
            }
        });

        //Recover account
        tv1Recover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(
                        LoginMenuActivity.this,
                        RecoverActivity.class
                );
                startActivity(i);
            }
        });

        //Login
        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = edEmail.getText().toString();
                String pw = edPw.getText().toString();

                if (doLogin()) {

                    btLogin.setEnabled(false);
                    mAuthLogin.signInWithEmailAndPassword(email, pw).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Intent i = new Intent(
                                        LoginMenuActivity.this,
                                        LoggedMenuActivity.class
                                );
                                startActivity(i);
                            } else {
                                btLogin.setEnabled(true);
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    });
                }

            }
        });

    }

    private boolean doLogin() {
        if (edEmail.getText().toString().trim().equals("") ||
                !Patterns.EMAIL_ADDRESS.matcher(edEmail.getText().toString()).matches()) {
            edEmail.setError("Invalid email.");
            edEmail.requestFocus();
            return false;
        }
        if (edPw.getText().toString().trim().equals("")) {
            edPw.setError("Invalid password.");
            edPw.requestFocus();
            return false;
        }
        progressBar.setVisibility(View.VISIBLE);

        return true;
    }
}