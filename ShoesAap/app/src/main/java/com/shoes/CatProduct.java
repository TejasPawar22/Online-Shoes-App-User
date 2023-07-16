package com.shoes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.shoes.adapters.CatProductAdapter;
import com.shoes.models.NavCategoryModel;
import com.shoes.models.ViewAllModel;

import java.util.ArrayList;
import java.util.List;

public class CatProduct extends AppCompatActivity {


    FirebaseFirestore firestore;
    RecyclerView recyclerView;
    CatProductAdapter viewAllAdapter;
    List<ViewAllModel> viewAllModelList;
    NavCategoryModel navCategoryModel = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cat_product);



        firestore = FirebaseFirestore.getInstance();

        //String type= getIntent().getStringExtra("type");
        //Toast.makeText(this, type.toString(), Toast.LENGTH_SHORT).show();

        recyclerView=findViewById(R.id.view_all_rec);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        String type= getIntent().getStringExtra("type");
        Toast.makeText(this,String.valueOf(type), Toast.LENGTH_SHORT).show();
        viewAllModelList = new ArrayList<>();
        viewAllAdapter = new CatProductAdapter(this,viewAllModelList);

        recyclerView.setAdapter(viewAllAdapter);

        if(type != null && type.equalsIgnoreCase(type.toString())){
            firestore.collection("AllProducts").whereEqualTo("type",type.toString()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    for(DocumentSnapshot documentSnapshot:task.getResult().getDocuments()){
                        ViewAllModel viewAllModel =documentSnapshot.toObject(ViewAllModel.class);
                        viewAllModelList.add(viewAllModel);
                        viewAllAdapter.notifyDataSetChanged();
                    }
                }
            });

        }

    }
}