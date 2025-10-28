package com.example.kuku;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.AuthCredential;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


public class LoginFragment extends Fragment {

    private TextView countryCodeText;
    private LoginFragment.Country[] countries;
    EditText number;
    Button rv_otp;
    ProgressBar progressBar;
    // Declare FirebaseAuth and GoogleSignInClient
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Context context=getContext();
        View view= inflater.inflate(R.layout.fragment_login, container, false);
        Spinner countrySpinner = view.findViewById(R.id.country_spinner);
        countryCodeText = view.findViewById(R.id.country_code_text);
        progressBar=view.findViewById(R.id.send_progress);
        number=view.findViewById(R.id.et_phone_number);
        rv_otp=view.findViewById(R.id.btn_receive_otp);
        SignInButton signInButton = view.findViewById(R.id.signInButton);

        // Initialize country data
        countries = new LoginFragment.Country[]{
                new LoginFragment.Country(R.drawable.flag_of_india, "Bharat", "+91"),
                new LoginFragment.Country(R.drawable.flag_of_albania, "Albania", "+355"),
                new LoginFragment.Country(R.drawable.flag_of_algeria, "Algeria", "+213"),
                // Add other countries here
        };

        if (getArguments() != null) {
            String num = getArguments().getString("num");
            String code = getArguments().getString("code");
            number.setText(num);
            countryCodeText.setText(code);
        }
        // Set up the adapter
        ArrayAdapter<LoginFragment.Country> adapter = new ArrayAdapter<LoginFragment.Country>(context, R.layout.spinner_item, countries) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                if (convertView == null) {
                    convertView = getLayoutInflater().inflate(R.layout.spinner_item, parent, false);
                }
                ImageView flag = convertView.findViewById(R.id.country_flag);
                flag.setImageResource(countries[position].getFlagResource());
                return convertView;
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                if (convertView == null) {
                    convertView = getLayoutInflater().inflate(R.layout.spinner_item, parent, false);
                }
                ImageView flag = convertView.findViewById(R.id.country_flag);
                TextView name = convertView.findViewById(R.id.country_name);
                flag.setImageResource(countries[position].getFlagResource());
                name.setText(countries[position].getName());
                return convertView;
            }
        };

        countrySpinner.setAdapter(adapter);

        countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                countryCodeText.setText(countries[position].getCode());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                countryCodeText.setText("");
            }
        });

        rv_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!number.getText().toString().trim().isEmpty()){
                    if(number.getText().toString().trim().length()==10){

                        progressBar.setVisibility(View.VISIBLE);
                        rv_otp.setVisibility(View.INVISIBLE);
                        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                                countryCodeText.getText().toString() + number.getText().toString(),
                                30L,
                                TimeUnit.SECONDS,
                                requireActivity(),
                                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                    @Override
                                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                        progressBar.setVisibility(View.GONE);
                                        rv_otp.setVisibility(View.VISIBLE);
                                    }

                                    @Override
                                    public void onVerificationFailed(@NonNull FirebaseException e) {
                                        progressBar.setVisibility(View.GONE);
                                        rv_otp.setVisibility(View.VISIBLE);
                                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onCodeSent(@NonNull String otp_code, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                        progressBar.setVisibility(View.GONE);
                                        rv_otp.setVisibility(View.VISIBLE);
                                        Fragment fragment=new OtpFragment();
                                        FragmentManager fragmentManager= (getActivity().getSupportFragmentManager());
                                        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
                                        fragmentTransaction.replace(R.id.loginFram,fragment).commit();
                                        Bundle bundle=new Bundle();
                                        bundle.putString("code",countryCodeText.getText().toString());
                                        bundle.putString("num",number.getText().toString());
                                        bundle.putString("otp",otp_code);
                                        fragment.setArguments(bundle);
                                    }
                                }
                        );

                    }
                    else {
                        Toast.makeText(context, "Please enter correct number", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(context, "Please enter mobile number", Toast.LENGTH_SHORT).show();
                };
            }
        });

        mAuth = FirebaseAuth.getInstance();

        // Configure Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)) // Replace with your client ID
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(getContext(), gso);

        // Set up the sign-in button and click listener
        signInButton.setOnClickListener(v -> signIn());

        return view;
    }
    // Method to initiate sign-in
    private void signIn() {
        // Start the Google sign-in popup
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 101); // Request code can be any integer value
    }

    // Handle sign-in result
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check request code
        if (requestCode == 101) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    // Handle the sign-in result
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // Sign in success, authenticate with Firebase
            firebaseAuthWithGoogle(account);
        } catch (ApiException e) {
            // Sign in failed, handle the error
            Toast.makeText(getContext(), "Sign-in failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // Authenticate with Firebase using Google credentials
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        assert user != null;
                        String userUid=user.getUid();
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
                        Query checkuserDatabse=reference.orderByChild("userUid").equalTo(userUid);
                        checkuserDatabse.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    Intent intent = new Intent(getContext(), Mainscreen.class);
                                    startActivity(intent);
                                }
                                else
                                {
                                    Toast.makeText(getContext(), "Sign-in successful", Toast.LENGTH_SHORT).show();
                                    Fragment fragment=new InfoFragment();
                                    FragmentManager fragmentManager= (getActivity().getSupportFragmentManager());
                                    FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
                                    fragmentTransaction.replace(R.id.loginFram,fragment).commit();
                                    Bundle bundle=new Bundle();
                                    fragment.setArguments(bundle);
                                }

                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    } else {
                        // Sign-in failed
                        Toast.makeText(getContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    // Country class
    class Country {
        private int flagResource;
        private String name;
        private String code;

        public Country(int flagResource, String name, String code) {
            this.flagResource = flagResource;
            this.name = name;
            this.code = code;
        }

        public int getFlagResource() {
            return flagResource;
        }

        public String getName() {
            return name;
        }

        public String getCode() {
            return code;
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        // Set the toolbar title when the fragment is resumed
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Login");
    }
}