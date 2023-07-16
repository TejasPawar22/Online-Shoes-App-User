package com.shoes.adapters;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.shoes.R;
import com.shoes.models.MyorderModel;

import java.util.HashMap;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MyordersAdapter extends RecyclerView.Adapter<MyordersAdapter.ViewHolder> {

    Context context;
    List<MyorderModel> cartModelList;
    int totalPrice=0;
    FirebaseFirestore firestore;
    FirebaseAuth auth;
    public MyordersAdapter(Context context, List<MyorderModel> cartModelList) {
        this.context = context;
        this.cartModelList = cartModelList;

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();


    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.my_order_details,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {




        holder.name.setText(cartModelList.get(position).getProductName());
        holder.price.setText(cartModelList.get(position).getProductPrice());
        holder.date.setText(cartModelList.get(position).getCurrentDate());
        holder.time.setText(cartModelList.get(position).getCurrentTime());
        holder.quantity.setText(cartModelList.get(position).getProductQty());
        holder.totalPrice.setText(String.valueOf(cartModelList.get(position).getTotalPrice()));
        holder.orderStatus.setText(cartModelList.get(position).getOrderStatus());



        String orderStatus="Cancel By Customer";
        String MyOrderMODELStatus = String.valueOf(cartModelList.get(position).getOrderStatus());
        if(MyOrderMODELStatus.equals(orderStatus)){
           holder.MYordersCard.setBackgroundColor(Color.parseColor("#FF5252"));
           holder.removeItem.setVisibility(View.GONE);
        }



        String productName= cartModelList.get(position).getProductName();
        String priceCart = String.valueOf(cartModelList.get(position).getTotalPrice());
        holder.removeItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*firestore.collection("CurrentUser").document(auth.getCurrentUser().getUid())
                        .collection("MyOrder")
                        .document(cartModelList.get(position).getDocumentId()).delete()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    cartModelList.remove(cartModelList.get(position));
                                    notifyDataSetChanged();
                                    Toast.makeText(context, productName+" "+"removed", Toast.LENGTH_SHORT).show();

                                    double totalAmount =0.0;
                                    for(MyCartModel myorderModel : cartModelList){
                                        totalAmount+=myorderModel.getTotalPrice();
                                    }

                                    Toast.makeText(context, String.valueOf(totalAmount), Toast.LENGTH_SHORT).show();
                                   // holder.CartTotalPrice.setText(String.valueOf(totalAmount));


                                        /*Intent intent = new Intent("MyTotalAmount");
                                        intent.putExtra("totalAmount",priceCart);
                                        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                                }else{
                                    Toast.makeText(context, "Error"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });*/


                // Create a query against the collection.
                firestore.collection("MyOrder").whereEqualTo("Userid",auth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            notifyDataSetChanged();
                            final HashMap<String, Object> cartMap = new HashMap<>();
                            cartMap.put("OrderStatus", "Cancel By Customer");
                            firestore.collection("MyOrder").document(cartModelList.get(position).getOrderid())
                                    .set(cartMap, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {



                                            new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE).setTitleText("Your Order Has Been Cancelled").show();
                                            holder.orderStatus.setText("Cancel By Customer");


                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "Error writing document", e);
                                            Toast.makeText(context, "Failed to Cancel!", Toast.LENGTH_LONG).show();

                                        }
                                    });


                        }

                    }
                });

            }
        });

        totalPrice =totalPrice+cartModelList.get(position).getTotalPrice();

        Intent intent = new Intent("MyTotalAmount");
        intent.putExtra("totalAmount",totalPrice);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);


    }



    @Override
    public int getItemCount() {
        return cartModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView name,price,date,time,quantity,totalPrice,CartTotalPrice,orderStatus;
        ImageView removeItem;
        Button buynow;
        LinearLayout MYordersCard;
        ConstraintLayout EmptyConstraint;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.product_name);
            price = itemView.findViewById(R.id.product_price);
            date = itemView.findViewById(R.id.current_date);
            time = itemView.findViewById(R.id.current_time);
            quantity = itemView.findViewById(R.id.product_quantity);
            totalPrice = itemView.findViewById(R.id.total_price);
            CartTotalPrice= itemView.findViewById(R.id.CartTotalPrice);
            removeItem = itemView.findViewById(R.id.removeItem);
            buynow=itemView.findViewById(R.id.buy_now);
            orderStatus=itemView.findViewById(R.id.OrderStatus);
            MYordersCard=itemView.findViewById(R.id.MYordersCard);
            EmptyConstraint=itemView.findViewById(R.id.EmptyConstraint);


        }
    }
}
