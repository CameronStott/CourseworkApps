package com.example.locations_defib;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginScreen extends AppCompatActivity {

    FirebaseAuth mAuth;

    EditText editEmailLog, editPasswordLog;

    TextView btnRegister;
    Button btnLogin;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        editEmailLog = (EditText) findViewById(R.id.editEmailLog);
        editPasswordLog = (EditText) findViewById(R.id.editPasswordLog);
        mAuth = FirebaseAuth.getInstance();

        btnRegister = (TextView)findViewById(R.id.textViewRegisterBtn);
        btnLogin = (Button)findViewById(R.id.btnLogin);

        progressBar = (ProgressBar)findViewById(R.id.progressBarLogin);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent logIntent = new Intent(LoginScreen.this,RegisterScreen.class);
                startActivity(logIntent);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editEmailLog.getText().toString();
                String password = editPasswordLog.getText().toString();

                if (email.isEmpty()){
                    editEmailLog.setError("Please enter an email address");
                }
                if(password.isEmpty()) {
                    editPasswordLog.setError("Please enter a password");
                }

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                String TAG = "LoginScreen";
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "signInWithEmail:success");
                                    Toast.makeText(LoginScreen.this,"Login Successfull", Toast.LENGTH_SHORT).show();
                                    Intent regIntent = new Intent(LoginScreen.this,BBQOrdering.class);
                                    startActivity(regIntent);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                                    Toast.makeText(LoginScreen.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                }

                                // ...
                            }
                        });
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginScreen.this, RegisterScreen.class);
                startActivity(intent);
            }
        });


    }
}
