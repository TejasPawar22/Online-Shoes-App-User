package com.shoes;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.shoes.adapters.MyCartAdapter;
import com.shoes.models.MyCartModel;
import com.shoes.models.ViewAllModel;
import com.shoes.models.userModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class MyCart extends Fragment {

    FirebaseFirestore db;
    FirebaseAuth auth;
    RecyclerView recyclerView;
    MyCartAdapter cartAdapter;
    List<MyCartModel> cartModelList;
    List<userModel> userModelsList;
    TextView overTotalAmount;
    ConstraintLayout EmptyConstraint;
    FirebaseDatabase database;
    Button buyNow;
    int totalBill;
    public MyCart() {
        // Required empty public constructor
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment





        View root = inflater.inflate(R.layout.fragment_my_cart, container, false);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        recyclerView = root.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        overTotalAmount=root.findViewById(R.id.CartTotalPrice);
        EmptyConstraint=root.findViewById(R.id.EmptyConstraint);


       /* LocalBroadcastManager.getInstance(getActivity())
                .registerReceiver(mMessageReceiver,new IntentFilter("MyTotalAmount"));*/
        cartModelList = new ArrayList<>();
        cartAdapter = new MyCartAdapter(getActivity(),cartModelList);
        recyclerView.setAdapter(cartAdapter);

        db.collection("CurrentUser").document(auth.getCurrentUser().getUid())
                .collection("AddToCart").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){

                            for(DocumentSnapshot documentSnapshot: task.getResult().getDocuments()){

                                String documentId = documentSnapshot.getId();

                                MyCartModel cartModel = documentSnapshot.toObject(MyCartModel.class);
                                cartModel.setDocumentId(documentId);

                                cartModelList.add(cartModel);
                                cartAdapter.notifyDataSetChanged();
                            }


                            calculateTotalAmount(cartModelList);
                        }

                    }
                });


        buyNow = root.findViewById(R.id.buy_now);







        buyNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DocumentReference docRef = db.collection("UserData").document(auth.getCurrentUser().getUid());
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Log.d(TAG, "DocumentSnapshot data: " + document.toObject(userModel.class).getAddress());

                                if( document.toObject(userModel.class).getAddress()==null) {
                                    Toast.makeText(getContext(), "Please Complete Your Profile First.", Toast.LENGTH_SHORT).show();
                                    new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE).setTitleText("Please complete Your Profile").show();


                                }else {
                                    Intent intent = new Intent(getContext(), PlacedOrderActivity.class);
                                    intent.putExtra("ItemList", (Serializable) cartModelList);
                                    startActivity(intent);
                                }
                            } else {
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });


                                }


        });
        return root;

    }

    private void calculateTotalAmount(List<MyCartModel> cartModelList) {

        double totalAmount =0.0;
        for(MyCartModel myCartModel : cartModelList){
            totalAmount+=myCartModel.getTotalPrice();
        }

        //Toast.makeText(getContext(), String.valueOf(totalAmount), Toast.LENGTH_SHORT).show();
        overTotalAmount.setText("Total Amount :"+totalAmount);

        if(totalAmount==0.0){
            buyNow.setVisibility(View.GONE);

        }

    }


   /* public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {


        int totalBill = 0;

                    totalBill= intent.getIntExtra("totalAmount",0);


        overTotalAmount.setText("Total Cart Amount: "+totalBill);

            if(totalBill==0){
                EmptyConstraint.setVisibility(View.VISIBLE);
            }

        }
    };*/
}