package com.shoes.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.shoes.R;
import com.shoes.models.MyCartModel;

import java.util.List;

public class MyCartAdapter extends RecyclerView.Adapter<MyCartAdapter.ViewHolder> {

    Context context;
    List<MyCartModel> cartModelList;
    int totalPrice=0;
    FirebaseFirestore firestore;
    FirebaseAuth auth;
    public MyCartAdapter(Context context, List<MyCartModel> cartModelList) {
        this.context = context;
        this.cartModelList = cartModelList;

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();


    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.my_cart_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Glide.with(context).load(cartModelList.get(position).getProductImg()).into(holder.productImg);



            holder.name.setText(cartModelList.get(position).getProductName());
            holder.price.setText(cartModelList.get(position).getProductPrice());
            holder.date.setText(cartModelList.get(position).getCurrentDate());
            holder.time.setText(cartModelList.get(position).getCurrentTime());
            holder.quantity.setText(cartModelList.get(position).getProductQty());
            holder.totalPrice.setText(String.valueOf(cartModelList.get(position).getTotalPrice()));

            String productName= cartModelList.get(position).getProductName();
            String priceCart = String.valueOf(cartModelList.get(position).getTotalPrice());
            holder.removeItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    firestore.collection("CurrentUser").document(auth.getCurrentUser().getUid())
                            .collection("AddToCart")
                            .document(cartModelList.get(position).getDocumentId()).delete()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        cartModelList.remove(cartModelList.get(position));
                                        notifyDataSetChanged();
                                        Toast.makeText(context, productName+" "+"removed", Toast.LENGTH_SHORT).show();

                                            double totalAmount =0.0;
                                            for(MyCartModel myCartModel : cartModelList){
                                                totalAmount+=myCartModel.getTotalPrice();
                                            }

                                            Toast.makeText(context, String.valueOf(totalAmount), Toast.LENGTH_SHORT).show();
                                           // holder.CartTotalPrice.setText(String.valueOf(totalAmount));


                                        /*Intent intent = new Intent("MyTotalAmount");
                                        intent.putExtra("totalAmount",priceCart);
                                        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);*/
                                    }else{
                                        Toast.makeText(context, "Error"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
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

        TextView name,price,date,time,quantity,totalPrice,CartTotalPrice;
        ImageView removeItem,productImg;
        Button buynow;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.product_name);
            price = itemView.findViewById(R.id.product_price);
            date = itemView.findViewById(R.id.current_date);
            time = itemView.findViewById(R.id.current_time);
            quantity = itemView.findViewById(R.id.product_quantity);
            totalPrice = itemView.findViewById(R.id.total_price);
            //CartTotalPrice= itemView.findViewById(R.id.CartTotalPrice);
            removeItem = itemView.findViewById(R.id.removeItem);
          //  buynow=itemView.findViewById(R.id.buy_now);
            productImg=itemView.findViewById(R.id.productImg);


        }
    }
}
