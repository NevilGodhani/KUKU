package com.example.kuku;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddressFragment extends Fragment {
    EditText editTextName, editTextStreet, editTextCity, editTextState, editTextZip;
    Button buttonSaveAddress;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_address, container, false);
        editTextName = view.findViewById(R.id.editTextName);
        editTextStreet = view.findViewById(R.id.editTextStreet);
        editTextCity = view.findViewById(R.id.editTextCity);
        editTextState = view.findViewById(R.id.editTextState);
        editTextZip = view.findViewById(R.id.editTextZip);
        buttonSaveAddress = view.findViewById(R.id.buttonSaveAddress);
        Context context=getContext();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            String userUid = currentUser.getUid();
            DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("address").child(userUid);
            userReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (task.isSuccessful()) {
                        DataSnapshot snapshot = task.getResult();
                        if(snapshot.exists()) {
                            Address address=snapshot.getValue(Address.class);
                            if (address != null) {
                                String name=address.getName();
                                String street=address.getStreet();
                                String city=address.getCity();
                                String state=address.getState();
                                String zip=address.getZip();
                                editTextName.setText(name);
                                editTextStreet.setText(street);
                                editTextCity.setText(city);
                                editTextState.setText(state);
                                editTextZip.setText(zip);

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
        buttonSaveAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentUser != null) {
                String name = editTextName.getText().toString().trim();
                String street = editTextStreet.getText().toString().trim();
                String city = editTextCity.getText().toString().trim();
                String state = editTextState.getText().toString().trim();
                String zip = editTextZip.getText().toString().trim();
                if (name.isEmpty() || street.isEmpty() || city.isEmpty() || state.isEmpty() || zip.isEmpty()) {
                    Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }
                DatabaseReference addressReference = FirebaseDatabase.getInstance().getReference("address");
                String userUid = currentUser.getUid();
                Address address = new Address(name, street, city, state, zip);
                addressReference.child(userUid).setValue(address).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "Address saved successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Please Try Again", Toast.LENGTH_SHORT).show();
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
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Address");
    }
}