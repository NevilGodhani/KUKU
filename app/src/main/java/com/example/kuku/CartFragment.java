package com.example.kuku;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CartFragment extends Fragment {

    private CartAdapter adapter;
    private List<Cart> cart;
    double total;
    private DatabaseReference walletRef;
    private int userBalance;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = auth.getCurrentUser();

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.cart_recycler_view);
        int spacingInDp = 8;
        int spacingInPx = (int) (spacingInDp * getContext().getResources().getDisplayMetrics().density);
        recyclerView.addItemDecoration(new SpaceItemDecoration(spacingInPx));
        Button buyNow = view.findViewById(R.id.buyNowButton);
        TextView totalPrice=view.findViewById(R.id.totalPriceTextView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        walletRef = FirebaseDatabase.getInstance().getReference("wallet");
        fetchWalletBalance();

        cart = new ArrayList<>();
        adapter = new CartAdapter(getContext(), cart);
        recyclerView.setAdapter(adapter);

        if (currentUser != null) {
            String userUid = currentUser.getUid();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("cart").child(userUid);

            databaseReference.addValueEventListener(new ValueEventListener() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    cart.clear();
                    total = 0.0;
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        Cart cart1 = postSnapshot.getValue(Cart.class);
                        if (cart1.getPrice() != null) {
                            cart.add(cart1);
                            Integer qty = cart1.getQty();
                            total = total + (cart1.getPrice() * qty);
                        }
                    }
                    adapter.notifyDataSetChanged();
                    totalPrice.setText("Total Price :- ₹" + total);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getContext(), "Not", Toast.LENGTH_SHORT).show();
                }
            });
        }else {
            Toast.makeText(getContext(), "Please Log in", Toast.LENGTH_SHORT).show();
        }

        buyNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmDialog();
            }
        });
        return view;
    }
    private void showConfirmDialog() {
        if (total==0.0){
            Toast.makeText(getContext(), "Please add products", Toast.LENGTH_SHORT).show();
        }else {
            new AlertDialog.Builder(getContext())
                    .setTitle("Confirm Purchase")
                    .setMessage("Are you sure you want to buy this item for ₹" + total + "?")
                    .setPositiveButton("Confirm", (dialog, which) -> reduceWalletBalance())
                    .setNegativeButton("Cancel", null)
                    .show();
        }
    }
    private void reduceWalletBalance() {

        if (userBalance >= total) {
            Integer newBalance = (int) (userBalance - total);
            Toast.makeText(getContext(), "Purchase Successful!", Toast.LENGTH_SHORT).show();
            updateWalletBalance(newBalance);
            clearCart();

        } else {
            Toast.makeText(getContext(), "Insufficient Balance!", Toast.LENGTH_SHORT).show();
        }
    }
    private void updateWalletBalance(int newBalance) {
        if(currentUser!=null){
            String userUid=currentUser.getUid();
            DatabaseReference walletRef = FirebaseDatabase.getInstance().getReference("wallet").child(userUid);

            // Update the balance in the wallet node
            walletRef.child("balance").setValue(newBalance)
                    .addOnSuccessListener(aVoid -> {
                        // Successfully updated the balance
                        Log.d("Wallet Update", "Balance updated successfully: " + newBalance);
                    })
                    .addOnFailureListener(e -> {
                        // Failed to update the balance
                        Log.e("Wallet Update", "Failed to update balance: " + e.getMessage());
                        Toast.makeText(getContext(), "Failed to update balance", Toast.LENGTH_SHORT).show();
                    });
        }

    }

    public void clearCart() {
        String userUid = currentUser.getUid();
        DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference("cart").child(userUid);
        DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference("orderHistory").child(userUid);
        cartRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
                        Cart cartItem = itemSnapshot.getValue(Cart.class);
                        String cartTitle = cartItem.getTitle();
                        Integer cartQty = cartItem.getQty();

                        Query checkItemQuery = orderRef.orderByChild("title").equalTo(cartTitle);
                        checkItemQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                        OrderHistory existingOrder = dataSnapshot.getValue(OrderHistory.class);
                                        if (existingOrder != null) {
                                            int newQuantity = existingOrder.getQty() + cartQty;
                                            dataSnapshot.getRef().child("qty").setValue(newQuantity)
                                                    .addOnCompleteListener(updateTask -> {
                                                        if (updateTask.isSuccessful()) {
                                                            Toast.makeText(getContext(), cartTitle + " quantity updated to " + newQuantity, Toast.LENGTH_SHORT).show();
                                                            Log.d(TAG, "Quantity updated successfully: " + newQuantity);
                                                        } else {
                                                            Log.e(TAG, "Failed to update quantity: ", updateTask.getException());
                                                            Toast.makeText(getContext(), "Failed to update quantity", Toast.LENGTH_SHORT).show();
                                                        }
                                                    })
                                                    .addOnFailureListener(e -> {
                                                        Log.e(TAG, "Error updating quantity: ", e);
                                                        Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    });
                                        } else {
                                            Log.e(TAG, "Failed to retrieve cart item: Cart object is null");
                                            Toast.makeText(getContext(), "Failed to retrieve cart item", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }else {
                                    orderRef.child(cartTitle).setValue(cartItem);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(getContext(), "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();

                            }
                        });
                    }
                    cartRef.removeValue()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    //Toast.makeText(getContext(), "Cart saved to order history and cleared successfully", Toast.LENGTH_SHORT).show();
                                } else {
                                    //Toast.makeText(getContext(), "Failed to clear cart after saving", Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    Toast.makeText(getContext(), "Cart is already empty", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to retrieve cart", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        // Set the toolbar title when the fragment is resumed
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Cart");
    }

    private void fetchWalletBalance() {
        if(currentUser!=null) {
            String userUid=currentUser.getUid();
            walletRef.child(userUid).addValueEventListener(new ValueEventListener() {
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