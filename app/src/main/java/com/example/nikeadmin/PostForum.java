package com.example.nikeadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class PostForum extends AppCompatActivity implements View.OnClickListener {

    StorageReference mstorageReference;
    FirebaseAuth mAuth;
    FirebaseFirestore mfirestore;
    EditText post_Title, post_Content;
    Long timestamp;
    

    Button Okbutton;
    CircleImageView birth_date;
    ImageView circleImageView;

    Uri mainImageUri=null;
    boolean isChaanged = false;
    ProgressBar progressBar;
    String user_id;
    Bitmap compressedImageBitmap;
    CheckBox checkBox;
    boolean check_box_value=false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_forum);

        //initializing firebase critical objects
        mstorageReference = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mfirestore = FirebaseFirestore.getInstance();
        
        post_Content = findViewById(R.id.post_content);
        post_Title = findViewById(R.id.post_title);
        

        Okbutton = findViewById(R.id.profile_btn_submit);
        circleImageView = findViewById(R.id.profile_circular);
        progressBar= findViewById(R.id.profile_progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        

        circleImageView.setOnClickListener(this);




        Okbutton.setOnClickListener(this);





    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.profile_circular:
                //allow user to select profile pic from phone memory
                profilePicPicker();

                break;
                

            case R.id.profile_btn_submit:

                if(isChaanged){ //becomes true when new image is selected
                    final String postTitle=post_Title.getText().toString().trim();
                    final String postContent=post_Content.getText().toString().trim();
                    

                    user_id=mAuth.getCurrentUser().getUid();



                    if (!TextUtils.isEmpty(postTitle)&&!TextUtils.isEmpty(postContent)&& mainImageUri !=null){

                        progressBar.setVisibility(View.VISIBLE);

                        StorageReference image_path=mstorageReference.child("Profile_image").child(user_id+".jpg");

                        image_path.putFile(mainImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                                if (task.isSuccessful()){
                                    final String main_profile_uri=task.getResult().getDownloadUrl().toString();
                                    storeProfileThumburi(postTitle,postContent,main_profile_uri);

                                }else {
                                    String exception=task.getException().getMessage();
                                    Toast.makeText(PostForum.this, "Image Upload Error is: "+exception, Toast.LENGTH_LONG).show();
                                }
                            }
                        });

                    }else {
                        Toast.makeText(PostForum.this, "Ensure all fields have been filled", Toast.LENGTH_LONG).show();
                    }

                }else {
                    Toast.makeText(PostForum.this, "Please select image first by clicking image icon above to setup your account", Toast.LENGTH_LONG).show();

                }
                break;

        }
    }

  

    private void storeProfileThumburi(final String postTitle1, final String postContent1, final String mainUrl) {
        File actualImageFile=new File(mainImageUri.getPath());


        try {
            compressedImageBitmap = new Compressor(PostForum.this)
                    //compressing image of high quality to a thumbnail bitmap for faster loading
                    .setMaxWidth(60)
                    .setMaxHeight(60)
                    .setQuality(5)
                    .setCompressFormat(Bitmap.CompressFormat.WEBP)
                    .compressToBitmap(actualImageFile);


        } catch (IOException e) {
            e.printStackTrace();
        }

        //compressing image of high quality to a thumbnail bitmap for faster loading
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        compressedImageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        UploadTask thumbfilepath=mstorageReference.child("Forum_images/forum_thumbs").child(user_id+".jpg").putBytes(data);

        thumbfilepath.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                if(task.isSuccessful()){
                    //after the image thumbnail has been uploaded successfully to the cloud storage
                    //it publishes the contents to the firestore cloud storage
                    storeFirestore(task,postTitle1,postContent1,mainUrl);//used for storing image and the user name in firebase
                }
                else{
                    String exception=task.getException().getMessage();
                    Toast.makeText(PostForum.this, "Thumb Error is: "+exception, Toast.LENGTH_LONG).show();
                }

            }
        });





    }

    private void storeFirestore(Task<UploadTask.TaskSnapshot> task, String postTitle,
                                String postContent, String mainUrl) {


        long timeStamp = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());

        Uri download_uri;
        download_uri=task.getResult().getDownloadUrl();
        progressBar.setVisibility(View.INVISIBLE);
        Map<String,Object> user_details=new HashMap<>();
        user_details.put("title",postTitle);
        user_details.put("description",postContent);
        user_details.put("user_id",mAuth.getCurrentUser().getUid());
        user_details.put("timestamp",timeStamp);
        user_details.put("imageuri",mainUrl);
        user_details.put("thumburi",download_uri.toString());



        mfirestore.collection("Forum_Posts").document(user_id).set(user_details).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    gotMainActivity();

                }else{
                    String exception=task.getException().getMessage();
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(PostForum.this, "Text Error is: "+exception, Toast.LENGTH_LONG).show();
                }

            }
        });





    }

    private void gotMainActivity() {
        Toast.makeText(this, "Your Content has been posted successfully", Toast.LENGTH_LONG).show();
         post_Content.setText("");
         post_Title.setText("");

    }

    private void profilePicPicker() {
        // the first if statement checks whether the user is running android Mash mellow and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (ContextCompat.checkSelfPermission(PostForum.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                // if the permission has been denied allow user to request for the permission

                ActivityCompat.requestPermissions(PostForum.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

            } else {

                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(4, 3)
                        .setScaleType(CropImageView.ScaleType.CENTER_CROP)
                        .start(this);

            }
        } else {

            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(4, 3)
                    .setScaleType(CropImageView.ScaleType.CENTER_CROP)
                    .start(this);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mainImageUri = result.getUri();
                circleImageView.setImageURI(mainImageUri);
                isChaanged = true;

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }


        }
    }









    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){


            case 1:

                if (grantResults.length>0){

                    if (grantResults[0]==PackageManager.PERMISSION_GRANTED){



                    }else if(grantResults[0]==PackageManager.PERMISSION_DENIED){

                        Toast.makeText(getApplicationContext(),"Permission Denied",Toast.LENGTH_LONG).show();

                    }
                }
        }

    }


   

}
