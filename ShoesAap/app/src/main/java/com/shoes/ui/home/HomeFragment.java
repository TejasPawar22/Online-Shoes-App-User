package com.shoes.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.shoes.R;
import com.shoes.adapters.ViewAllAdapter;
import com.shoes.databinding.FragmentHomeBinding;
import com.shoes.models.NavCategoryModel;
import com.shoes.models.ViewAllModel;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    FirebaseFirestore firestore;
    RecyclerView recyclerView;
    ViewAllAdapter viewAllAdapter;
    List<NavCategoryModel> viewAllModelList;

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);


        firestore = FirebaseFirestore.getInstance();

        //String type= getIntent().getStringExtra("type");
        //Toast.makeText(this, type.toString(), Toast.LENGTH_SHORT).show();

        recyclerView=root.findViewById(R.id.view_all_rec);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));



        viewAllModelList = new ArrayList<>();
        viewAllAdapter = new ViewAllAdapter(getContext(),viewAllModelList);

        recyclerView.setAdapter(viewAllAdapter);



            firestore.collection("ShoesCat").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    for(DocumentSnapshot documentSnapshot:task.getResult().getDocuments()){
                        NavCategoryModel navCategoryModel =documentSnapshot.toObject(NavCategoryModel.class);
                        viewAllModelList.add(navCategoryModel);
                        viewAllAdapter.notifyDataSetChanged();
                    }
                }
            });


        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}