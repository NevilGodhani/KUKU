package com.example.kuku;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class OtpFragment extends Fragment {

    String otpbackend;
    private TextView tvResendOtp;
    EditText i1,i2,i3,i4,i5,i6;
    TextView t1,txt1,txt2,edit;
    Button submit;
    ProgressBar progressBar;
    Login login;
    Toolbar toolbar;
public static String num;
public static String code;


    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_otp, container, false);
        submit = view.findViewById(R.id.btn_submit);
        progressBar = view.findViewById(R.id.submit_progress);
        t1 = view.findViewById(R.id.num);
        txt1 = view.findViewById(R.id.tv1_resend_otp);
        txt2 = view.findViewById(R.id.tv2_resend_otp);
        edit=view.findViewById(R.id.tv_edit_phone);
        i1 = view.findViewById(R.id.input1);
        i2 = view.findViewById(R.id.input2);
        i3 = view.findViewById(R.id.input3);
        i4 = view.findViewById(R.id.input4);
        i5 = view.findViewById(R.id.input5);
        i6 = view.findViewById(R.id.input6);
        login = (Login)getActivity();
        Context context=getContext();
        txt1.setVisibility(View.VISIBLE);
        txt2.setVisibility(View.INVISIBLE);
        String number = null;
        String otp = null;

        if (getArguments() != null) {
            code = getArguments().getString("code");
            num = getArguments().getString("num");
            otp = getArguments().getString("otp");
            number = code + num;
        }


        new  CountDownTimer(20000, 1000) {
            public void onTick(long millisUntilFinished) {
                txt1.setVisibility(View.VISIBLE);
                txt1.setText("Resend OTP in " + millisUntilFinished / 1000 + "s");
            }

            public void onFinish() {
                txt1.setVisibility(View.INVISIBLE);
                txt2.setVisibility(View.VISIBLE);
                txt2.setText("Didn't Receive OTP? Resend");
            }
        }.start();

        t1.setText(number);
        otpbackend = otp;

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!i1.getText().toString().trim().isEmpty() && !i2.getText().toString().trim().isEmpty() && !i3.getText().toString().trim().isEmpty() && !i4.getText().toString().trim().isEmpty() && !i5.getText().toString().trim().isEmpty() && !i6.getText().toString().trim().isEmpty()) {
                    String otpstring = i1.getText().toString() +
                            i2.getText().toString() +
                            i3.getText().toString() +
                            i4.getText().toString() +
                            i5.getText().toString() +
                            i6.getText().toString();
                    if (otpbackend != null) {
                        progressBar.setVisibility(View.VISIBLE);
                        submit.setVisibility(View.INVISIBLE);

                        PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(
                                otpbackend, otpstring
                        );
                        FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential).
                                addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        progressBar.setVisibility(View.GONE);
                                        submit.setVisibility(View.VISIBLE);
                                        if (task.isSuccessful()) {
                                            checkuser();
                                        } else {
                                            Toast.makeText(context, "Please enter correct OTP", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    } else {
                        Toast.makeText(context, "Please check internet connection", Toast.LENGTH_SHORT).show();
                    }
                    //Toast.makeText(otp.this, "OTP Verify", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Please enter all numbers", Toast.LENGTH_SHORT).show();
                }
            }
        });

        String finalNumber = number;
        txt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "OTP sent successfully", Toast.LENGTH_SHORT).show();
                txt2.setVisibility(View.INVISIBLE);
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        finalNumber,
                        60,
                        TimeUnit.SECONDS,
                        requireActivity(),
                        new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCodeSent(@NonNull String new_otp_code, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                otpbackend = new_otp_code;
                                Toast.makeText(context, "OTP sent successfully", Toast.LENGTH_SHORT).show();
                            }
                        }
                );
            }
        });
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment=new LoginFragment();
                FragmentManager fragmentManager= (getActivity().getSupportFragmentManager());
                FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.loginFram,fragment).commit();
                Bundle bundle=new Bundle();
                bundle.putString("num",num);
                bundle.putString("code",code);
                fragment.setArguments(bundle);
            }
        });
        movenumber();
        return view;
    }

    private void movenumber() {
        i1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    i2.requestFocus();
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }

        });
        i2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().trim().isEmpty()){
                    i3.requestFocus();
                }
                if (s.toString().trim().isEmpty()) {
                    i1.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        i3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().trim().isEmpty()){
                    i4.requestFocus();
                }
                if (s.toString().trim().isEmpty()) {
                    i2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        i4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().trim().isEmpty()){
                    i5.requestFocus();
                }
                if (s.toString().trim().isEmpty()) {
                    i3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        i5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().trim().isEmpty()){
                    i6.requestFocus();
                }
                if (s.toString().trim().isEmpty()) {
                    i4.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        i6.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().isEmpty()) {
                    i5.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
    public void checkuser(){
        String num=t1.getText().toString().trim();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("users");
        Query checkuserDatabse=reference.orderByChild("phoneNumber").equalTo(num);
        checkuserDatabse.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    Intent intent = new Intent(getContext(), Mainscreen.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
                else {
                    Fragment fragment=new InfoFragment();
                    FragmentManager fragmentManager= (getActivity().getSupportFragmentManager());
                    FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.loginFram,fragment).commit();
                    Bundle bundle=new Bundle();
                    bundle.putString("num",t1.getText().toString());
                    fragment.setArguments(bundle);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    @Override
    public void onResume() {
        super.onResume();
        // Set the toolbar title when the fragment is resumed
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("OTP");
    }
}