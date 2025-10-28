package com.example.kuku;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class OrderFragment extends Fragment {

    private OrderAdapter adapter;
    private List<OrderHistory> orderHistories;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = auth.getCurrentUser();

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_order, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.order_recycler_view);
        int spacingInDp = 8;
        int spacingInPx = (int) (spacingInDp * getContext().getResources().getDisplayMetrics().density);
        recyclerView.addItemDecoration(new SpaceItemDecoration(spacingInPx));
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        Button clear = view.findViewById(R.id.clearAll);
        orderHistories = new ArrayList<>();
        adapter = new OrderAdapter(getContext(), orderHistories);
        recyclerView.setAdapter(adapter);
        if (currentUser != null) {
            String userUid = currentUser.getUid();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("orderHistory").child(userUid);

            databaseReference.addValueEventListener(new ValueEventListener() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    orderHistories.clear();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        OrderHistory orderHistory=postSnapshot.getValue(OrderHistory.class);
                        orderHistories.add(orderHistory);
                    }
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getContext(), "Not", Toast.LENGTH_SHORT).show();
                }
            });
        }else {
            Toast.makeText(getContext(), "Please Log in", Toast.LENGTH_SHORT).show();
        }
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmDialog();
            }
        });




        return view;
    }
    @Override
    public void onResume() {
        super.onResume();
        // Set the toolbar title when the fragment is resumed
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Order History");
    }
    private void showConfirmDialog() {

        new AlertDialog.Builder(getContext())
                .setTitle("Confirm Delete")
                .setMessage("Are you sure you want to delete all histories ?")
                .setPositiveButton("Confirm", (dialog, which) -> DELETE())
                .setNegativeButton("Cancel", null)
                .show();

    }
    private void DELETE(){
        if (currentUser != null) {
            String userUid = currentUser.getUid();
            DatabaseReference itemRef = FirebaseDatabase.getInstance().getReference("orderHistory").child(userUid);

            itemRef.removeValue().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Item removed successfully, update UI if needed
                    Toast.makeText(getContext(), "Histories cleared", Toast.LENGTH_SHORT).show();
                } else {
                    // Handle failure
                    Toast.makeText(getContext(), "Failed to remove item", Toast.LENGTH_SHORT).show();
                }
            });
        }else {
            Toast.makeText(getContext(), "Please Log in", Toast.LENGTH_SHORT).show();
        }
    }

}
