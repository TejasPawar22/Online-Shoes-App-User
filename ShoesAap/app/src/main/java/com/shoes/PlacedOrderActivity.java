package com.shoes;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.shoes.models.MyCartModel;
import com.shoes.models.userModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class PlacedOrderActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseFirestore firestore;
    FirebaseDatabase database;
    TextView orderidShow;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_placed_order_activity);

        auth= FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        database = FirebaseDatabase.getInstance();



        List<MyCartModel> list = (ArrayList<MyCartModel>) getIntent().getSerializableExtra("ItemList");

        if(list != null && list.size()> 0) {
            for (MyCartModel model : list) {


                DocumentReference docRef = firestore.collection("UserData").document(auth.getCurrentUser().getUid());
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Log.d(TAG, "DocumentSnapshot data: " + document.toObject(userModel.class).getAddress());
                                userModel userModel = document.toObject(userModel.class);





                                Random myRandom = new Random();




                                Random r = new Random();
                                int number = 100000 + (int)(r.nextFloat() * 899900);

                                String orderid= String.valueOf("Your OrderID"+number);

                                orderidShow = findViewById(R.id.orderid);

                                orderidShow.setText(orderid);


                                final HashMap<String, Object> cartMap = new HashMap<>();
                                    cartMap.put("Orderid",orderid);
                                cartMap.put("Userid",auth.getCurrentUser().getUid());

                                    cartMap.put("productName", model.getProductName());
                                    cartMap.put("productPrice", model.getProductPrice());
                                    cartMap.put("currentDate", model.getCurrentDate());
                                    cartMap.put("currentTime", model.getCurrentTime());
                                    cartMap.put("productQty", model.getProductQty());
                                    cartMap.put("totalPrice", model.getTotalPrice());
                                    cartMap.put("productImg",model.getProductImg());

                                    cartMap.put("userName", userModel.getName());

                                    cartMap.put("userAddress", userModel.getAddress());
                                    cartMap.put("userEmail", userModel.getEmail());
                                    cartMap.put("userMobile", userModel.getNumber());

                                    cartMap.put("OrderStatus", "Pending");






               /* firestore.collection("CurrentUser").document(auth.getCurrentUser().getUid())
                        .collection("MyOrder").add(cartMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                new SweetAlertDialog(PlacedOrderActivity.this, SweetAlertDialog.SUCCESS_TYPE).setTitleText(String.valueOf("Your Order Has Been Placed")).show();

                                finish();
                            }
                        });*/



                                firestore.collection("MyOrder").document(orderid)
                                        .set(cartMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                firestore.collection("CurrentUser").document(auth.getCurrentUser().getUid())
                                                        .collection("AddToCart").document(model.getDocumentId()).delete();

                                                    firestore.collection("CurrentUser").document(auth.getCurrentUser().getUid()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                new SweetAlertDialog(PlacedOrderActivity.this, SweetAlertDialog.SUCCESS_TYPE).setTitleText(String.valueOf("Your Order Has Been Placed" )).show();


                                                            }
                                                        }
                                                    });

                                                    //finish();
                                                }
                                            });

                            } else {
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });
                                }


            }
        }



    @Override
    public void onBackPressed() {


        startActivity(new Intent(PlacedOrderActivity.this,HomeActivity.class));
    }
}