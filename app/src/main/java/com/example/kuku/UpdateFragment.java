package com.example.kuku;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UpdateFragment extends Fragment {

    EditText first,second,Email,phone;
    Button update;
    //TextView phone;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
    View view=inflater.inflate(R.layout.fragment_update, container, false);
        first=view.findViewById(R.id.first_name_u);
        second=view.findViewById(R.id.last_name_u);
        Email=view.findViewById(R.id.email_u);
        update=view.findViewById(R.id.update_u);
        phone=view.findViewById(R.id.phone_number_u);
        Context context=getContext();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            String userUid = currentUser.getUid();
            DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("users").child(userUid);
            userReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (task.isSuccessful()) {
                        DataSnapshot snapshot = task.getResult();
                        if(snapshot.exists()) {
                            User user = snapshot.getValue(User.class);
                            if (user != null) {
                                String firstName = user.getFirstName();
                                String lastName = user.getLastName();
                                String email = user.getEmail();
                                String phoneNumber = user.getPhoneNumber();
                                first.setText(firstName);
                                second.setText(lastName);
                                Email.setText(email);
                                phone.setText(phoneNumber);
                            }
                        }else {
                            Toast.makeText(context, "No data available", Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(context, "Failed to read value", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentUser != null) {

                    String firsts=first.getText().toString().trim();
                    String seconds=second.getText().toString().trim();
                    String emails=Email.getText().toString().trim();
                    String phones=phone.getText().toString().trim();
                    DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("users");
                    String userUid = currentUser.getUid();
                    User user = new User(firsts, seconds, emails, phones,userUid);
                    userReference.child(userUid).setValue(user).addOnCompleteListener(task -> {
                        if (task.isSuccessful()){
                            Toast.makeText(context, "Data updated successfully", Toast.LENGTH_SHORT).show();
                            Intent i=new Intent(context,Mainscreen.class);
                            startActivity(i);
                        }else {
                            Toast.makeText(context, "Failed to save data", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
        return  view;
    }
    @Override
    public void onResume() {
        super.onResume();
        // Set the toolbar title when the fragment is resumed
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Update Details");
    }
}