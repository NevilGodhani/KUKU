package com.example.kuku;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;


public class AccountFragment extends Fragment {

    TextView first_a,last_a,email_a,phone_a;
    CardView edit_a,address_a;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = auth.getCurrentUser();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_account, container, false);

        first_a=view.findViewById(R.id.first_a);
        last_a=view.findViewById(R.id.last_a);
        email_a=view.findViewById(R.id.email_a);
        phone_a=view.findViewById(R.id.phone_a);
        edit_a=view.findViewById(R.id.edit_a);
        address_a=view.findViewById(R.id.address);
        Context context=getContext();

        if (currentUser != null) {
            String userUid = currentUser.getUid();
            DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("users").child(userUid);

            userReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    try {
                        if (task.isSuccessful()) {
                            DataSnapshot snapshot = task.getResult();
                            if (snapshot.exists()) {
                                // Assuming User class has the correct structure to map the data
                                User user = snapshot.getValue(User.class);

                                if (user != null) {
                                    String firstName = user.getFirstName() != null ? user.getFirstName() : "N/A";
                                    String lastName = user.getLastName() != null ? user.getLastName() : "N/A";
                                    String email = user.getEmail() != null ? user.getEmail() : "N/A";
                                    String phoneNumber = user.getPhoneNumber() != null ? user.getPhoneNumber() : "N/A";

                                    // Ensure TextViews are properly initialized before setting text
                                    if (first_a != null) first_a.setText(firstName);
                                    if (last_a != null) last_a.setText(lastName);
                                    if (email_a != null) email_a.setText(email);
                                    if (phone_a != null) phone_a.setText(phoneNumber);
                                } else {
                                    Toast.makeText(context, "User data is null", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(context, "No data available", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(context, "Failed to read value", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        // Catch any unexpected errors and print to Logcat
                        Log.e("FirebaseError", "Error fetching user data", e);
                        Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
        else {
            Toast.makeText(getContext(), "Please Log in", Toast.LENGTH_SHORT).show();
        }
        edit_a.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, Draweraction.class);
                intent.putExtra("update","update");
                startActivity(intent);
            }
        });
        address_a.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(context, Draweraction.class);
                i.putExtra("address","address");
                startActivity(i);
            }
        });
        return view;
    }

}