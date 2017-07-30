package com.example.chirag.pdfconverter;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.chirag.pdfconverter.adapters.ImagesAdapter;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    static final int IMAGE_CAPTURE = 101;
    private String mCurrentPhotoPath;
    private static final String TAG = MainActivity.class.getSimpleName();

    private Uri mImageUri;
    private ArrayList<String> images;

    ImageView image;
    FloatingActionButton fab;

    RecyclerView mRecyclerView;
    RecyclerView.Adapter adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        checkPermission();
        images = new ArrayList<>();



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        else if(id == R.id.save_to_pdf){
            PdfUtils.covertToPdf(images);
            showFile();
        }

        return super.onOptionsItemSelected(item);
    }
    private void saveImage() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            //Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = Uri.fromFile(photoFile);
                Log.i(TAG, "Image saved at "+ photoURI);
                mImageUri = photoURI;
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, IMAGE_CAPTURE);
            }
        }
    }
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        Log.i(TAG, "image path: "+mCurrentPhotoPath);
        return image;
    }
    private boolean hasCamera(){
        if(getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            return true;
        }
        else {
            return false;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "it's getting called");
        if(requestCode == IMAGE_CAPTURE && data!=null){
            if (resultCode == RESULT_OK){
//                Toast.makeText(this, "Image saved at "+ mImageUri, Toast.LENGTH_LONG).show();
//                Picasso.with(getApplicationContext()).load(mCurrentPhotoPath).into(imageView);
                images.add(mCurrentPhotoPath);
                adapter = new ImagesAdapter(images, this);
                mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
                mRecyclerView.setAdapter(adapter);


            }
            else if(resultCode == RESULT_CANCELED){
                Toast.makeText(this, "Failed to save image", Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(this, "Error in saving image", Toast.LENGTH_LONG).show();

            }
        }
    }
    void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            //if permission granted, initialize the views

            initViews();
//            if(!fileExistance("test.pdf")){
//            }
//            else {
//                showFile();
//            }
        } else {
            //show the dialog requesting to grant permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }
    private void showFile(){
        File pdfFile = new File(PdfUtils.BASE_PDF_DIRECTORY + "/" + "example.pdf");  // -> filename = maven.pdf
        Uri path = Uri.fromFile(pdfFile);
        Log.d(TAG, "file path: " + path);
        Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
        pdfIntent.setDataAndType(path, "application/pdf");
        pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        try {
            Toast.makeText(MainActivity.this, "Pdf successfully created!", Toast.LENGTH_SHORT).show();
            startActivity(pdfIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(MainActivity.this, "No Application available to view PDF", Toast.LENGTH_SHORT).show();
        }
    }
    private boolean fileExistance(String fileName){
        String path = Environment.getExternalStorageDirectory() + "/papers/" + fileName;
        File file = new File(path);
        Log.d(TAG, "path check:" + path);
        return file.exists();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    if (!fileExistance("test.pdf")){
//                    }
//                    else
//                        showFile();
                    initViews();

                } else {
                    //permission is denied (this is the first time, when "never ask again" is not checked)
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        finish();
                    }
                }
        }
    }

    private void initViews(){
        mRecyclerView = (RecyclerView)findViewById(R.id.recycler_view);
//        adapter = new ImagesAdapter(null, this);

        image =(ImageView)findViewById(R.id.image);

      fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(hasCamera()){
                    saveImage();
                }
            }
        });
    }



}
