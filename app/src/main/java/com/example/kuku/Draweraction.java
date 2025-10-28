package com.example.kuku;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.WindowInsets;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Draweraction extends AppCompatActivity {
    private ItemAdapter itemAdapter;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_draweraction);
        View decoreview= getWindow().getDecorView();
        decoreview.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
            @NonNull
            @Override
            public WindowInsets onApplyWindowInsets(@NonNull View v, @NonNull WindowInsets insets) {
                int left=insets.getSystemWindowInsetLeft();
                int top=insets.getSystemWindowInsetTop();
                int right=insets.getSystemWindowInsetRight();
                int bottom=insets.getSystemWindowInsetBottom();
                v.setPadding(left,top,right,bottom);
                return insets.consumeSystemWindowInsets();
            }
        });

        toolbar=findViewById(R.id.toolbar_drawer);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getColor(R.color.white));


        int id=getIntent().getIntExtra("drawer",0);

        if(R.id.wallet==id){
            loadFragment(new WalletFragment());
        }else if (R.id.order_history==id) {
            loadFragment(new OrderFragment());
        }
        String update=getIntent().getStringExtra("update");
        if("update".equals(update)){
            loadFragment(new UpdateFragment());
        }
        String address=getIntent().getStringExtra("address");
        if("address".equals(address)){
            loadFragment(new AddressFragment());
        }
        String cart=getIntent().getStringExtra("cart");
        if("cart".equals(cart)){
            loadFragment(new CartFragment());
        }
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.ActionFramlayout, fragment);
        fragmentTransaction.commit();
    }

}