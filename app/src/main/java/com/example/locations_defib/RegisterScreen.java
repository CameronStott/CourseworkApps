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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterScreen extends AppCompatActivity {

    EditText editEmail, editPassword;
    Button btnRegister;
    ProgressBar progressBar;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_screen);

        mAuth = FirebaseAuth.getInstance();
        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        btnRegister = findViewById(R.id.btnRegister);
        progressBar = findViewById(R.id.progressBarRegister);


        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editEmail.getText().toString();
                String password = editPassword.getText().toString();


                if (email.isEmpty()){
                    editEmail.setError("Please enter an email address");
                }
                if(password.isEmpty()) {
                    editPassword.setError("Please enter a password");
                }

                progressBar.setVisibility(View.VISIBLE);

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                String TAG = "Register Screen";
                                if (task.isSuccessful()) {
                                    Toast.makeText(RegisterScreen.this, "Registration Successfull", Toast.LENGTH_SHORT).show();
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "Register Success");
                                    Intent loginIntent = new Intent(RegisterScreen.this, LoginScreen.class);
                                    startActivity(loginIntent);

                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "Register Failed", task.getException());
                                    Toast.makeText(RegisterScreen.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.INVISIBLE);

                                }

                                // ...
                            }
                        });
            }
        });

    }
}
