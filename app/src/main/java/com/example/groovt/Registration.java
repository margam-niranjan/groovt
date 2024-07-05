package com.example.groovt;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class Registration extends AppCompatActivity {
    TextView loginBtn;
    EditText rg_username,rg_email,rg_password,rg_repassword;
    Button rg_signUp;
    CircleImageView rg_CircleImage;
    FirebaseAuth auth;
    Uri imageURI;
    String passMatch =  "^(?=.*[A-Za-z])(?=.*0-9)[A-Za-z0-9]{8,}$";
    String imageuri;
    String emailPattern = "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$";
    FirebaseDatabase database;
    FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registration);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        loginBtn = findViewById(R.id.signInButton);
        rg_username = findViewById(R.id.editUserName);
        rg_email = findViewById(R.id.editEmail);
        rg_password = findViewById(R.id.editTextPassword);
        rg_repassword = findViewById(R.id.editReenter);
        rg_CircleImage = findViewById(R.id.logo);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( Registration.this, Login.class);
                startActivity(intent);
                finish();
            }
        });

        rg_CircleImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"),10);
            }
        });
        rg_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = rg_username.getText().toString();
                String email1 = rg_email.getText().toString();
                String pass = rg_password.getText().toString();
                String cpass = rg_repassword.getText().toString();
                String status = "Hey I'm using this Application";

                if(TextUtils.isEmpty(name) || TextUtils.isEmpty(email1) || TextUtils.isEmpty(pass) || TextUtils.isEmpty(cpass)){
                    Toast.makeText(Registration.this, "Please Enter Valid Information", Toast.LENGTH_SHORT).show();
                } else if (!email1.matches(emailPattern)) {
                    rg_email.setError("Enter  a Valid Email");
                } else if (!pass.matches(passMatch)) {
                    rg_password.setError("Enter a Valid Password");
                } else if (!pass.matches(cpass)) {
                    rg_password.setError("Password doesn't match");
                }
                else {
                    auth.createUserWithEmailAndPassword(email1,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                String id = task.getResult().getUser().getUid();
                                DatabaseReference reference = database.getReference().child("user").child("id");
                                StorageReference storageReference = storage.getReference().child("Upload").child("id");

                                if(imageURI!=null){
                                    storageReference.putFile(imageURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                            if(task.isSuccessful()){
                                                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        imageuri = uri.toString();
                                                    }
                                                });
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    });
                }

            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 10){
            if(data!=null){
                imageURI =  data.getData();
                rg_CircleImage.setImageURI(imageURI);
            }
        }
    }
}