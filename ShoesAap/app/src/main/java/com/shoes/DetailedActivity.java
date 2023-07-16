package com.shoes;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.shoes.models.ViewAllModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class DetailedActivity extends AppCompatActivity {
    int totalQty=1;
    int totalPrice=0;
    ImageView detailedImg,addItem,RemoveItem;
    TextView   price,rating,description,quantity;
    Button addTocart;
    Toolbar toolbar;

    FirebaseFirestore firestore;
    FirebaseAuth auth;
    ViewAllModel viewAllModel = null;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();



        final Object object = getIntent().getSerializableExtra("detail");
        if (object instanceof ViewAllModel){
            viewAllModel = (ViewAllModel) object;
        }
        detailedImg = findViewById(R.id.detailed_img);
        addItem = findViewById(R.id.additem);
        RemoveItem = findViewById(R.id.removeItem);
        price = findViewById(R.id.detailed_price);
        rating = findViewById(R.id.detailed_rating);
        description = findViewById(R.id.detailed_desc);
        quantity = findViewById(R.id.quantity);



        if (viewAllModel != null){
            Glide.with(getApplicationContext()).load(viewAllModel.getImg_url()).into(detailedImg);
            rating.setText(viewAllModel.getRating());
            price.setText(viewAllModel.getPrice());
            description.setText(viewAllModel.getDescription());
            toolbar.setTitle(viewAllModel.getName());

            totalPrice = Integer.parseInt(viewAllModel.getPrice()) * totalQty;

        }
        addTocart= findViewById(R.id.add_to_cart);

        addTocart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addedToCart();
            }
        });
        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (totalQty < 5){
                    totalQty++;
                    quantity.setText(String.valueOf(totalQty));
                    totalPrice = Integer.parseInt(viewAllModel.getPrice()) * totalQty;

                }
            }
        });

        RemoveItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (totalQty > 1){
                    totalQty--;
                    quantity.setText(String.valueOf(totalQty));
                    totalPrice = Integer.parseInt(viewAllModel.getPrice()) * totalQty;

                }
            }
        });

    }

    private void addedToCart() {
        String saveCurrentDate,saveCurrentTime;
        Calendar calForDate = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MM dd, yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calForDate.getTime());

            final HashMap<String,Object> cartMap = new HashMap<>();

            cartMap.put("productName",viewAllModel.getName());
            cartMap.put("productImg",viewAllModel.getImg_url());
        cartMap.put("productPrice",price.getText().toString());
        cartMap.put("currentDate",saveCurrentDate);
        cartMap.put("currentTime",saveCurrentTime);
        cartMap.put("productQty",quantity.getText().toString());
        cartMap.put("totalPrice",totalPrice);

        firestore.collection("CurrentUser").document(auth.getCurrentUser().getUid())
                .collection("AddToCart").add(cartMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        Toast.makeText(DetailedActivity.this, viewAllModel.getName()+"Added To Cart", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });

    }
}