package com.example.kuku;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.RadioGroup;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AccessoryFragment extends Fragment {

    private GridView productGridView;
    private List<Food> productList = new ArrayList<>();
    private FoodAdapter productAdapter;
    private DatabaseReference databaseReference;
    private SearchView searchView;
    private Button filterButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_accessory, container, false);

        productGridView = view.findViewById(R.id.product_grid_view);
        searchView = view.findViewById(R.id.search_view);
        filterButton = view.findViewById(R.id.button_filter);

        productAdapter = new FoodAdapter(getContext(), productList);
        productGridView.setAdapter(productAdapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("accessory");

        // Load products from Firebase
        loadProductsFromFirebase();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                productAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                productAdapter.getFilter().filter(newText);
                return false;
            }
        });

        // Set up filter button click listener
        filterButton.setOnClickListener(v -> showFilterBottomSheet());

        return view;
    }
    private void loadProductsFromFirebase() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                productList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Food product = snapshot.getValue(Food.class);
                    productList.add(product);
                }
                productAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to load products.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @SuppressLint("NonConstantResourceId")
    private void showFilterBottomSheet() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        View sheetView = getLayoutInflater().inflate(R.layout.filter_bottom_sheet, null);
        bottomSheetDialog.setContentView(sheetView);

        RadioGroup priceFilterGroup = sheetView.findViewById(R.id.price_filter_group);
        Button applyFilterButton = sheetView.findViewById(R.id.button_apply_filter);

        applyFilterButton.setOnClickListener(v -> {
            int selectedId = priceFilterGroup.getCheckedRadioButtonId();
            double minPrice = 0;
            double maxPrice = Double.MAX_VALUE;

            if (selectedId == R.id.price_under_500) {
                maxPrice = 500;
            } else if (selectedId == R.id.price_500_to_1000) {
                minPrice = 500;
                maxPrice = 1000;
            } else if (selectedId == R.id.price_1000_to_2000) {
                minPrice = 1000;
                maxPrice = 2000;
            } else if (selectedId == R.id.price_2000_to_3000) {
                minPrice = 2000;
                maxPrice = 3000;
            } else if (selectedId == R.id.price_3000_and_up) {
                minPrice = 3000;
            }

            filterProductsByPrice(minPrice, maxPrice);
            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.show();
    }

    private void filterProductsByPrice(double minPrice, double maxPrice) {
        List<Food> filteredList = new ArrayList<>();
        for (Food product : productList) {
            if (product.getPrice() >= minPrice && product.getPrice() <= maxPrice) {
                filteredList.add(product);
            }
        }

        // Use updateProductList to update the adapter with the filtered products
        productAdapter.updateProductList(filteredList);
    }
}