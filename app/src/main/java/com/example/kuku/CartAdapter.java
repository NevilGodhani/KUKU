package com.example.kuku;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private final Context context;
    private final List<Cart> cart;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = auth.getCurrentUser();

    public CartAdapter(Context context1, List<Cart> cart) {
        this.context = context1;
        this.cart = cart;
    }
    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.layout_cart, parent, false);
        return new CartAdapter.CartViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CartAdapter.CartViewHolder holder, int position) {
        Cart cart1 = cart.get(position);

        holder.textViewTitle.setText(cart1.getTitle());
        String title=cart1.getTitle();
        holder.textViewPrice.setText("₹" + cart1.getPrice());
        Picasso.get().load(cart1.getImageUrl()).into(holder.imageView);
        holder.textViewQuantity.setText("Quantity : " + cart1.getQty().toString());

        holder.remove.setOnClickListener(v -> {
            if (currentUser != null) {
                String userUid = currentUser.getUid();
                // Get the reference to the specific item in Firebase
                DatabaseReference itemRef = FirebaseDatabase.getInstance().getReference("cart").child(userUid).child(title);

                // Remove the item from Firebase
                itemRef.removeValue().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Item removed successfully, update UI if needed
                        Toast.makeText(v.getContext(), "Item removed", Toast.LENGTH_SHORT).show();
                    } else {
                        // Handle failure
                        Toast.makeText(v.getContext(), "Failed to remove item", Toast.LENGTH_SHORT).show();
                    }
                });
            }else {
                Toast.makeText(v.getContext(), "Please Log in", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return cart.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder{
        public TextView textViewTitle,textViewPrice,textViewQuantity;
        public ImageView imageView;
        public Button remove;
        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.cart_product_title);
            imageView = itemView.findViewById(R.id.cart_product_image);
            textViewPrice=itemView.findViewById(R.id.cart_product_price);
            textViewQuantity=itemView.findViewById(R.id.cart_product_quantity);
            remove=itemView.findViewById(R.id.button_remove);
        }
    }

}

