package com.example.kuku;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class InfoFragment extends Fragment {
    Button signup;
    EditText fn,ln,em,pnum;
    public static String num;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_info, container, false);
        signup=view.findViewById(R.id.sign_up_button);
        fn=view.findViewById(R.id.first_name);
        ln=view.findViewById(R.id.last_name);
        em=view.findViewById(R.id.email);
        pnum=view.findViewById(R.id.phone_number);
        Context context=getContext();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        if (getArguments() != null) {
            num=getArguments().getString("num");
            pnum.setText(num);
        }
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String fname=fn.getText().toString();
                String lname=ln.getText().toString();
                String email=em.getText().toString();
                String phoneNumber=pnum.getText().toString();

                if (currentUser != null) {
                    DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("users");
                    String userUid = currentUser.getUid();
                    User user = new User(fname, lname, email, phoneNumber,userUid);
                    userReference.child(userUid).setValue(user).addOnCompleteListener(task -> {
                        if (task.isSuccessful()){
                            Toast.makeText(context, "You have signup successfully", Toast.LENGTH_SHORT).show();
                            Intent i=new Intent(context,Mainscreen.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                        }else {
                            Toast.makeText(context, "Failed to save data", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
        return view;
    }
    @Override
    public void onResume() {
        super.onResume();
        // Set the toolbar title when the fragment is resumed
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Information");
    }
}