package com.example.abc.presentervideoplayer.sandyapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.abc.presentervideoplayer.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText editTextSendEmail;
    private Button buttonSendEmail;
    TextView textViewBackButton;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        editTextSendEmail = findViewById(R.id.edittext_forgot_password);
        buttonSendEmail = findViewById(R.id.btn_send_email);
        textViewBackButton = findViewById(R.id.textview_back);
        mAuth = FirebaseAuth.getInstance();


        buttonSendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String emailAddress = editTextSendEmail.getText().toString();

                if (TextUtils.isEmpty(emailAddress)) {
                    Toast.makeText(getApplicationContext(), "Please Enter Email", Toast.LENGTH_LONG).show();
                } else {
                    mAuth.sendPasswordResetEmail(emailAddress).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

                                Toast.makeText(getApplicationContext(), "Please check email to reset Password", Toast.LENGTH_LONG).show();

                                Intent phonIntent = new Intent(getApplicationContext(), LoginActivity.class);
                                startActivity(phonIntent);
                            } else {
                                String error = task.getException().toString();
                                Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();
                            }

                        }
                    });

                }

            }
        });
    }
}
