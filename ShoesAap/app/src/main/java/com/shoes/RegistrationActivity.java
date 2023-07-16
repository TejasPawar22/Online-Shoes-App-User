package com.shoes;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.shoes.models.userModel;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;



import cn.pedant.SweetAlert.SweetAlertDialog;

public class RegistrationActivity extends AppCompatActivity {
    Button signUp;
    EditText name,email,password;
    TextView signIn;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseFirestore db;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        db= FirebaseFirestore.getInstance();

        progressBar = findViewById(R.id.progressbar);
        progressBar.setVisibility(View.GONE);

        signUp = findViewById(R.id.Regbtn);
        name=findViewById(R.id.name);
        email=findViewById(R.id.email);
        password=findViewById(R.id.password);
        signIn=findViewById(R.id.SignIn);
        auth = FirebaseAuth.getInstance();
        if(auth.getCurrentUser()!=null){
            startActivity(new Intent(RegistrationActivity.this,HomeActivity.class));
        }


        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegistrationActivity.this,MainActivity.class));
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                createUser();
                progressBar.setVisibility(View.VISIBLE);

            }

            private void createUser() {
                String userName = name.getText().toString();
                String userEmail = email.getText().toString();
                String userPassword = password.getText().toString();

                if(TextUtils.isEmpty(userName)){
                    Toast.makeText(RegistrationActivity.this, "Please Enter Name", Toast.LENGTH_SHORT).show();


                }

                if(TextUtils.isEmpty(userEmail)){
                    Toast.makeText(RegistrationActivity.this, "Please Enter valid Email", Toast.LENGTH_SHORT).show();


                } if(TextUtils.isEmpty(userPassword)){
                    Toast.makeText(RegistrationActivity.this, "Please Enter Password", Toast.LENGTH_SHORT).show();

                }

                if(userPassword.length() < 6){
                    Toast.makeText(RegistrationActivity.this, "Password length must be grater than 6 letters!", Toast.LENGTH_SHORT).show();

                    return;
                }

                //createUser

                auth.createUserWithEmailAndPassword(userEmail,userPassword)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){

                                    String id = task.getResult().getUser().getUid();

                                    Map<String, Object> userData = new HashMap<>();
                                    userData.put("name", userName);
                                    userData.put("email", userEmail);
                                    userData.put("password", userPassword);

                                    db.collection("UserData").document(id)
                                            .set(userData)
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





                                    new SweetAlertDialog(RegistrationActivity.this, SweetAlertDialog.SUCCESS_TYPE).setTitleText("Account Created Successfully !").show();

                                }else{
                                    progressBar.setVisibility(View.GONE);

                                    new SweetAlertDialog(RegistrationActivity.this, SweetAlertDialog.WARNING_TYPE).setTitleText(String.valueOf(task.getException())).show();

                                }
                            }
                        });

            }
        });
    }
}