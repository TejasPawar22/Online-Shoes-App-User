package com.shoes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class MainActivity extends AppCompatActivity {
    Button signIn;
    EditText email,password;
    TextView signUp;
    FirebaseAuth auth;

    ProgressBar progressBar;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        if(auth.getCurrentUser()!=null){
            startActivity(new Intent(MainActivity.this,HomeActivity.class));
        }


        //connection status


        //connect
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;


        }
        else {
            connected = false;
            Toast.makeText(this, "Please Check Your Internet", Toast.LENGTH_SHORT).show();
        }

        //connection status
        auth = FirebaseAuth.getInstance();

        progressBar = findViewById(R.id.progressbar);
        progressBar.setVisibility(View.GONE);

        signIn = findViewById(R.id.login_btn);
        email=findViewById(R.id.email);
        password=findViewById(R.id.password);
        signUp=findViewById(R.id.SignIn);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,RegistrationActivity.class));
            }
        });

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();
                progressBar.setVisibility(View.VISIBLE);

            }

            private void loginUser() {
                String userEmail = email.getText().toString();
                String userPassword = password.getText().toString();



                if(TextUtils.isEmpty(userEmail)){
                    Toast.makeText(MainActivity.this, "Please Enter valid Email", Toast.LENGTH_SHORT).show();

                } if(TextUtils.isEmpty(userPassword)){
                    Toast.makeText(MainActivity.this, "Please Enter Password", Toast.LENGTH_SHORT).show();

                }

                if(userPassword.length() < 6){
                    Toast.makeText(MainActivity.this, "Password length must be grater than 6 letters!", Toast.LENGTH_SHORT).show();

                    return;
                }
                auth.signInWithEmailAndPassword(userEmail,userPassword)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(MainActivity.this, "Logged In Successfully !", Toast.LENGTH_SHORT).show();
                                    Intent i = new Intent(MainActivity.this,HomeActivity.class);
                                    startActivity(i);
                                }else{
                                    progressBar.setVisibility(View.GONE);

                                    Toast.makeText(MainActivity.this, String.valueOf(task.getException()), Toast.LENGTH_SHORT).show();

                                }
                            }
                        });

            }
        });


        //userLogin

    }

}