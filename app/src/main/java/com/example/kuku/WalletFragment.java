package com.example.kuku;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class WalletFragment extends Fragment {
    private static TextView tvWalletBalance;
    private DatabaseReference walletRef;
    public static int userBalance;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = auth.getCurrentUser();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_wallet, container, false);
        tvWalletBalance = view.findViewById(R.id.tvWalletBalance);
        fetchWalletBalance();
        updateWalletBalanceDisplay();
        return view;
    }


    private static void updateWalletBalanceDisplay() {

        tvWalletBalance.setText("Wallet Balance: ₹" + userBalance);
    }
    @Override
    public void onResume() {
        super.onResume();
        // Set the toolbar title when the fragment is resumed
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Wallet");
    }

    private void fetchWalletBalance() {
        if(currentUser!=null) {
            String userUid=currentUser.getUid();
            walletRef = FirebaseDatabase.getInstance().getReference("wallet").child(userUid);
            walletRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        Wallet wallet = snapshot.getValue(Wallet.class);
                        if (wallet != null) {
                            userBalance = (int) wallet.getBalance(); // Retrieve balance as integer
                        } else {

                            Log.e("Balance", "Wallet data is null");
                        }
                    } else {
                        Log.e("Balance", "Snapshot does not exist");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("Balance", "Failed to retrieve balance: " + error.getMessage());
                }
            });
        }
    }
}