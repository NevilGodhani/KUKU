package com.example.kuku;

import static android.content.ContentValues.TAG;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

class FoodAdapter extends BaseAdapter {

    private List<Food> originalProductList;
    private List<Food> filteredProductList;
    private final LayoutInflater inflater;
    private ProductFilter productFilter;
    private DatabaseReference databaseReference;
    Integer q;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = auth.getCurrentUser();
    public FoodAdapter(Context context, List<Food> productList) {
        this.originalProductList = productList;
        this.filteredProductList = productList;
        this.inflater = LayoutInflater.from(context);
        getFilter();
    }

    public Filter getFilter() {
        if (productFilter == null) {
            productFilter = new ProductFilter();
        }
        return productFilter;
    }
    private class ProductFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            List<Food> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(originalProductList);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Food product : originalProductList) {
                    if (product.getTitle().toLowerCase().contains(filterPattern)) {
                        filteredList.add(product);
                    }
                }
            }

            results.values = filteredList;
            results.count = filteredList.size();

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredProductList = (List<Food>) results.values;
            notifyDataSetChanged();
        }


    }
    public void updateProductList(List<Food> newProductList) {
        this.originalProductList = newProductList;
        this.filteredProductList = newProductList; // Update both lists
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return filteredProductList.size();
    }
    @Override
    public Object getItem(int position) {
        return filteredProductList.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.layout_food, parent, false);
            holder = new ViewHolder();
            holder.productImage = convertView.findViewById(R.id.product_image);
            holder.productTitle = convertView.findViewById(R.id.product_title);
            holder.productPrice = convertView.findViewById(R.id.product_price);
            holder.buttonAddToCart = convertView.findViewById(R.id.button_add_to_cart);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Food food=filteredProductList.get(position);

        holder.productTitle.setText(food.getTitle());
        holder.productPrice.setText(String.format("₹%s", food.getPrice()));
        Picasso.get().load(food.getImageUrl()).into(holder.productImage);

        // Handle add to cart button click
        View finalConvertView = convertView;

        holder.buttonAddToCart.setOnClickListener(v -> {

            if (currentUser != null) {
                // Extract details of the food item
                String title = food.getTitle();
                Double price = food.getPrice();
                String imageUrl = food.getImageUrl();

                // Ensure the context is correctly set
                Context context = v.getContext();
                String userUid = currentUser.getUid();
                // Get reference to the 'cart' node
                DatabaseReference cartReference = FirebaseDatabase.getInstance().getReference("cart").child(userUid);

                // Check if the item already exists in the cart using title
                Query checkItemQuery = cartReference.orderByChild("title").equalTo(title);
                checkItemQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            // The item already exists, increment the quantity
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                Cart existingCart = dataSnapshot.getValue(Cart.class);

                                if (existingCart != null) {
                                    int newQuantity = existingCart.getQty() + 1;

                                    // Update the quantity in the existing cart item
                                    dataSnapshot.getRef().child("qty").setValue(newQuantity)
                                            .addOnCompleteListener(updateTask -> {
                                                if (updateTask.isSuccessful()) {
                                                    Toast.makeText(context, title + " quantity updated to " + newQuantity, Toast.LENGTH_SHORT).show();
                                                    Log.d(TAG, "Quantity updated successfully: " + newQuantity);
                                                } else {
                                                    Log.e(TAG, "Failed to update quantity: ", updateTask.getException());
                                                    Toast.makeText(context, "Failed to update quantity", Toast.LENGTH_SHORT).show();
                                                }
                                            })
                                            .addOnFailureListener(e -> {
                                                Log.e(TAG, "Error updating quantity: ", e);
                                                Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            });
                                } else {
                                    Log.e(TAG, "Failed to retrieve cart item: Cart object is null");
                                    Toast.makeText(context, "Failed to retrieve cart item", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            // The item does not exist, so add it as a new item

                            Cart newCart = new Cart(title, price, imageUrl, 1);
                            cartReference.child(title).setValue(newCart)
                                    .addOnCompleteListener(addTask -> {
                                        if (addTask.isSuccessful()) {
                                            Toast.makeText(context, title + " added to cart", Toast.LENGTH_SHORT).show();
                                            Log.d(TAG, title + " added to cart successfully.");
                                        } else {
                                            Log.e(TAG, "Failed to add item to cart: ", addTask.getException());
                                            Toast.makeText(context, "Failed to add item to cart", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e(TAG, "Error adding item to cart: ", e);
                                        Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "Database error: " + error.getMessage());
                        Toast.makeText(context, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(finalConvertView.getContext(), "Please Log in", Toast.LENGTH_SHORT).show();
            }
        });




        return convertView;
    }
    private static class ViewHolder {
        ImageView productImage;
        TextView productTitle;
        TextView productPrice;
        Button buttonAddToCart;
    }

}

