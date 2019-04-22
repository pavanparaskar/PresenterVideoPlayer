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
import android.widget.Toast;

import com.example.abc.presentervideoplayer.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

public class PhoneLoginActivity extends AppCompatActivity {

    private Button sendverificationButton, veryfyButton;
    private EditText inputPhoneNumberLogin, inputVerificatinCode;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;

    private FirebaseAuth mAuth;

    private ProgressDialog mProgressDialog;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);
        mAuth = FirebaseAuth.getInstance();
        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");

        mAdView = findViewById(R.id.adView);

        AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
        mAdView.loadAd(adRequest);
        sendverificationButton = findViewById(R.id.send_ver_code_button);
        veryfyButton = findViewById(R.id.verify_button);

        inputPhoneNumberLogin = findViewById(R.id.phone_number_login);
        inputVerificatinCode = findViewById(R.id.verification_code_input);

        mProgressDialog = new ProgressDialog(this);
        sendverificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String phoneNumber = inputPhoneNumberLogin.getText().toString();
                if (TextUtils.isEmpty(phoneNumber)) {


                } else {

                    mProgressDialog.setTitle("Verification Authe.....");
                    mProgressDialog.setMessage("Please Wait , and watch for code...");
                    mProgressDialog.setCanceledOnTouchOutside(false);
                    mProgressDialog.show();
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            "+91"+phoneNumber,        // Phone number to verify
                            60,                 // Timeout duration
                            TimeUnit.SECONDS,   // Unit of timeout
                            PhoneLoginActivity.this,               // Activity (for callback binding)
                            callbacks);
                }
            }
        });

        veryfyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendverificationButton.setVisibility(View.INVISIBLE);
                inputPhoneNumberLogin.setVisibility(View.INVISIBLE);

                String verificationCode = inputVerificatinCode.getText().toString();
                if (TextUtils.isEmpty(verificationCode)) {
                    Toast.makeText(getApplicationContext(), "Enter code first", Toast.LENGTH_LONG).show();
                } else {
                    mProgressDialog.setTitle("Verifying code....");
                    mProgressDialog.setMessage("Please Wait.....");
                    mProgressDialog.setCanceledOnTouchOutside(false);
                    mProgressDialog.show();

                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verificationCode);
                    signInWithPhoneAuthCredential(credential);
                }

            }
        });

        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Toast.makeText(getApplicationContext(), "Invalid mobile number", Toast.LENGTH_SHORT).show();
                sendverificationButton.setVisibility(View.VISIBLE);
                inputPhoneNumberLogin.setVisibility(View.VISIBLE);

                veryfyButton.setVisibility(View.INVISIBLE);
                inputVerificatinCode.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                mVerificationId = verificationId;
                mResendToken = token;
                mProgressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Code has been send...", Toast.LENGTH_SHORT).show();
                sendverificationButton.setVisibility(View.INVISIBLE);
                inputPhoneNumberLogin.setVisibility(View.INVISIBLE);

                veryfyButton.setVisibility(View.VISIBLE);
                inputVerificatinCode.setVisibility(View.VISIBLE);


            }


        };


    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            mProgressDialog.dismiss();

                            sendUserToMainActiviity();

                        } else {
                            String error = task.getException().toString();
                            Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void sendUserToMainActiviity() {

        Intent mainIntent = new Intent(getApplicationContext(), Main3ActivitySandy.class);
        startActivity(mainIntent);
    }
}
