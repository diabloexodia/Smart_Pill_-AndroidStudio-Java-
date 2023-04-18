package com.example.myapplication;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Regions;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import android.os.Bundle;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;


public class MainActivity extends AppCompatActivity {
    private EditText timingEditText;
    private EditText naemEditText;
    private EditText dosageEdittext;
    private EditText pillIdedittext;
    private Button sendButton;
    private Button clearButton;
    private static final String BUCKET_NAME = "smartpills";
    private static final String OBJECT_KEY = "userdetails/userdetails.txt";

    private AmazonS3Client s3Client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        sendButton = findViewById(R.id.button);
        clearButton = findViewById(R.id.clear);
        pillIdedittext=findViewById(R.id.pillId);

        BasicAWSCredentials credentials = new BasicAWSCredentials("YOUR-ACCESS-KEY", "YOUR-SECRET-KEY");
        s3Client = new AmazonS3Client(credentials);
        s3Client.setRegion(Region.getRegion(Regions.AP_SOUTHEAST_1));

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timingEditText = findViewById(R.id.time);
                dosageEdittext = findViewById(R.id.dosage);
                naemEditText = findViewById(R.id.name);

                pillIdedittext = findViewById(R.id.pillId);
                if(!naemEditText.getText().toString().equals(""))
                naemEditText.setText(" ");
                if(!timingEditText.getText().toString().equals(""))
                timingEditText.setText(" ");
                if(!dosageEdittext.getText().toString().equals(""))
                dosageEdittext.setText(" ");
                if(!pillIdedittext.getText().toString().equals(""))
                pillIdedittext.setText(" ");
            }
        });


        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //String userInput = timingEditText.getText().toString();
                UploadDataTask uploadDataTask = new UploadDataTask();
                uploadDataTask.execute();
            }
        });



    }
    private class UploadDataTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            timingEditText = findViewById(R.id.time);
            dosageEdittext = findViewById(R.id.dosage);
            naemEditText = findViewById(R.id.name);
            pillIdedittext = findViewById(R.id.pillId);
            String FinalpillConfig="Patient Name: "+naemEditText.getText().toString()+" Time of consumption: "+timingEditText.getText().toString()+" Dosage(Nos): "+dosageEdittext.getText().toString()+" Pill Id: "+pillIdedittext.getText().toString();
            AmazonS3 s3 = new AmazonS3Client(new BasicAWSCredentials("YOUR-SECRET-KEY", "YOUR-ACCESS-KEY"));
            String bucketName = "smartpills";
            String key = "userdetails/userdetails.txt";
            String fileContents = FinalpillConfig; // Replace with your own file contents

            byte[] contentAsBytes = fileContents.getBytes(StandardCharsets.UTF_8);
            ByteArrayInputStream contentsAsStream = new ByteArrayInputStream(contentAsBytes);
            ObjectMetadata md = new ObjectMetadata();
            md.setContentLength(contentAsBytes.length);

            try {
                PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key,contentsAsStream, md);
                s3.putObject(putObjectRequest);
            } catch (AmazonServiceException e) {
                // Handle AmazonServiceException errors
                e.printStackTrace();
            } catch (AmazonClientException e) {
                // Handle AmazonClientException errors
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            // Code to be executed after the upload is complete, such as displaying a success message to the user
            Toast.makeText(MainActivity.this, "Pill Confguration Stored", Toast.LENGTH_SHORT).show();
        }
    }



}
