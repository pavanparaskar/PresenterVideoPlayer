package com.example.abc.presentervideoplayer.sandyapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.abc.presentervideoplayer.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class RegisterActivity extends AppCompatActivity {

    private Button createAccountButton;
    private EditText userEmail, userPassword;
    private TextView alreadyHaveAccount;
    FirebaseAuth mAuth;
    private ProgressDialog mProgressBar;

    private DatabaseReference rootRef; //firebase database

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();   //firebase email authentication
        rootRef = FirebaseDatabase.getInstance().getReference();  // to store data on firebase database

        initialfields();
        alreadyHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToLoginUserActivity();
            }
        });
        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewAccount();// user will click on create account ..........................
            }
        });
    }


    private void initialfields() {
        createAccountButton = findViewById(R.id.register_button);

        userEmail = findViewById(R.id.register_email);
        userPassword = findViewById(R.id.register_password);
        alreadyHaveAccount = findViewById(R.id.already_have_account_link);
        mProgressBar = new ProgressDialog(this);

    }

    private void sendToLoginUserActivity() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void createNewAccount() {
        String email = userEmail.getText().toString();
        String password = userPassword.getText().toString();
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Please Enter email...", Toast.LENGTH_SHORT).show();

        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Please Enter password...", Toast.LENGTH_SHORT).show();

        } else {


            mProgressBar.setTitle("Creating New Account");
            mProgressBar.setMessage("Please wait......");
            mProgressBar.setCanceledOnTouchOutside(true);
            mProgressBar.show();
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                String deviceToken = FirebaseInstanceId.getInstance().getToken();

                                String currentUserID = mAuth.getCurrentUser().getUid();         // get current user id for database
                                rootRef.child("Users").child(currentUserID).setValue("");       //store data to databse
                                rootRef.child(currentUserID).child("device_token")
                                        .setValue(deviceToken);
                                sendToMainActivity();//After successfull registertration
                                Toast.makeText(getApplicationContext(), "Account created successfull...", Toast.LENGTH_SHORT).show();
                                mProgressBar.dismiss();

                            } else {
                                String msg = task.getException().toString();
                                Toast.makeText(getApplicationContext(), "Error" + msg, Toast.LENGTH_SHORT).show();
                                mProgressBar.dismiss();
                            }

                        }
                    });
        }


    }

    private void sendToMainActivity() {
        Intent intent = new Intent(getApplicationContext(), Main3ActivitySandy.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
