package com.theshootapp.world.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theshootapp.world.R;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickResult;

import java.io.ByteArrayOutputStream;

import com.theshootapp.world.ModelClasses.UserProfile;

public class UserProfileActivity extends AppCompatActivity implements IPickResult {

    ImageView profilePic;
    EditText Name;
    FirebaseStorage storage;
    DatabaseReference userProfileRef;
    FirebaseDatabase firebaseDatabase;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        profilePic=findViewById(R.id.UserProfileImage);
        Name=findViewById(R.id.UserProfileName);
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        firebaseDatabase=FirebaseDatabase.getInstance();
        userProfileRef=firebaseDatabase.getReference().child("User").child(firebaseUser.getUid());
        storage=FirebaseStorage.getInstance();
    }


    public void onSaveClick(View view)
    {
        //Check for name and Picture
        UploadPicture();

    }
    public void onPicClick(View view)
    {
        PickSetup setup = new PickSetup().setSystemDialog(true);
        PickImageDialog.build(setup).show(this);
    }

    @Override
    public void onPickResult(PickResult pickResult) {
        if (pickResult.getError() == null) {
            profilePic.setImageBitmap(pickResult.getBitmap());
        } else {
            //Handle possible errors
            //TODO: do what you have to do with r.getError();
            Toast.makeText(this, pickResult.getError().getMessage(), Toast.LENGTH_LONG).show();
        }

    }

    void UploadPicture()
    {
        StorageReference profilePicRef = storage.getReference().child("UserDP/"+firebaseUser.getUid()+".jpg");

        profilePic.setDrawingCacheEnabled(true);
        profilePic.buildDrawingCache();
        Bitmap bitmap = profilePic.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = profilePicRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(UserProfileActivity.this, "Failed To Upload Profile Pic", Toast.LENGTH_SHORT).show();

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
               // setResult(1);
                //finish();
                UserProfile userProfile=new UserProfile(Name.getText().toString(),firebaseUser.getPhoneNumber());
                userProfileRef.setValue(userProfile);
                startActivity(new Intent(UserProfileActivity.this,MainCameraActivity.class));
            }
        });
    }
}
