package com.shoes.ui.gallery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.shoes.R;
import com.shoes.adapters.MyordersAdapter;
import com.shoes.models.MyorderModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class GalleryFragment extends Fragment {

    FirebaseFirestore db;
    FirebaseAuth auth;
    RecyclerView recyclerView;
    MyordersAdapter cartAdapter;
    List<MyorderModel> cartModelList;
    TextView overTotalAmount;
    ConstraintLayout EmptyConstraint;

    Button buyNow;
    int totalBill;
    public GalleryFragment() {
        // Required empty public constructor
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment





        View root = inflater.inflate(R.layout.fragment_gallery, container, false);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        recyclerView = root.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        overTotalAmount=root.findViewById(R.id.CartTotalPrice);
        EmptyConstraint=root.findViewById(R.id.EmptyConstraint);


       /* LocalBroadcastManager.getInstance(getActivity())
                .registerReceiver(mMessageReceiver,new IntentFilter("MyTotalAmount"));*/
        cartModelList = new ArrayList<>();
        cartAdapter = new MyordersAdapter(getActivity(),cartModelList);
        recyclerView.setAdapter(cartAdapter);


       /*db.collection("CurrentUser").document(auth.getCurrentUser().getUid())
                .collection("MyOrder").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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
                });*/

        // Create a query against the collection.
        db.collection("MyOrder").whereEqualTo("Userid", auth.getCurrentUser().getUid())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){

                            for(DocumentSnapshot documentSnapshot: task.getResult().getDocuments()){


                                String documentId = documentSnapshot.getId();



                                MyorderModel cartModel = documentSnapshot.toObject(MyorderModel.class);
                                cartModel.setDocumentId(documentId);



                                cartModelList.add(cartModel);
                                cartAdapter.notifyDataSetChanged();
                            }



                            calculateTotalAmount(cartModelList);
                        }else{

                        }

                    }
                });

// Execute the query











        return root;
    }

    private void calculateTotalAmount(List<MyorderModel> cartModelList) {

        double totalAmount =0.0;
        for(MyorderModel myCartModel : cartModelList){
            totalAmount+=myCartModel.getTotalPrice();
        }

        if(totalAmount==0.0){
            EmptyConstraint.setVisibility(View.VISIBLE);
        }else{
            EmptyConstraint.setVisibility(View.GONE);
        }

        //Toast.makeText(getContext(), String.valueOf(totalAmount), Toast.LENGTH_SHORT).show();
        overTotalAmount.setText("Total Amount :"+totalAmount);


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