package com.shoes.ui.slideshow;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.shoes.PlacedOrderActivity;
import com.shoes.R;
import com.shoes.models.userModel;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;

public class SlideshowFragment extends Fragment {

    ImageView profileImg;
    EditText name,email,number,address;
    Button update;

    FirebaseStorage storage;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseFirestore db;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_slideshow, container, false);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        db = FirebaseFirestore.getInstance();


        profileImg = root.findViewById(R.id.profile_img);
        name= root.findViewById(R.id.profile_name);
        email = root.findViewById(R.id.profile_email);
        number = root.findViewById(R.id.profile_phone);
        address = root.findViewById(R.id.profile_addr);
        update = root.findViewById(R.id.btn_update);



        DocumentReference docRef = db.collection("UserData").document(auth.getCurrentUser().getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.toObject(userModel.class).getAddress());
                        userModel userModel = document.toObject(userModel.class);
                        Glide.with(getContext()).load(userModel.getProfileImg()).into(profileImg);
                        email.setText(userModel.getEmail());
                        name.setText(userModel.getName());
                        number.setText(userModel.getNumber());
                        address.setText(userModel.getAddress());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });


        profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,33);
            }
        });
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String id = auth.getCurrentUser().getUid();

                Map<String, Object> userData = new HashMap<>();
                userData.put("name", name.getText().toString());
                userData.put("email", email.getText().toString());
                userData.put("address", address.getText().toString());
                userData.put("number", number.getText().toString());

                db.collection("UserData").document(id)
                        .update(userData)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "DocumentSnapshot successfully written!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error writing document", e);
                            }
                        });





                /*   updateUserProfile(userName,userEmail,userAddress,UserPh);*/

            }
        });
        return root;
    }

    /* private void updateUserProfile(String userName, String userEmail, String userAddress, String userPh) {
         Toast.makeText(getContext(), userName, Toast.LENGTH_SHORT).show();

         userModel.setName(userName);
         userModel.setEmail(userEmail);
         userModel.setNumber(userPh);
         userModel.setAddress(userAddress);

     }
 */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(data.getData()!=null){
            Uri profileUri = data.getData();
            profileImg.setImageURI(profileUri);

            final StorageReference reference = storage.getReference().child("profile_picture")
                    .child(FirebaseAuth.getInstance().getUid());

            reference.putFile(profileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(getContext(), "Profile Picture Updated", Toast.LENGTH_SHORT).show();


                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String id = auth.getCurrentUser().getUid();

                            Map<String, Object> userData = new HashMap<>();
                            userData.put("name", name.getText().toString());
                            userData.put("email", email.getText().toString());
                            userData.put("address", address.getText().toString());
                            userData.put("number", number.getText().toString());
                            userData.put("profileImg", uri.toString());

                            db.collection("UserData").document(id)
                                    .set(userData)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(getContext(), "Profile Picture Updated!", Toast.LENGTH_SHORT).show();
                                            Log.d(TAG, "DocumentSnapshot successfully written!");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "Error writing document", e);
                                        }
                                    });
                            Toast.makeText(getContext(), "Profile Picture Uploaded!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });

        }
    }
}