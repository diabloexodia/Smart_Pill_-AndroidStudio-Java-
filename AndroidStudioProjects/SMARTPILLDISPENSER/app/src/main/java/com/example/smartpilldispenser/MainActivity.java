package com.example.smartpilldispenser;


import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

import com.example.smartpilldispenser.Pill;
import com.example.smartpilldispenser.Human;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

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
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import android.os.Bundle;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private EditText timingEditText;
    static private String selecteddosageValue;
    // List<String> pillTimingArrayList;
    private EditText naemEditText;
    private Spinner dosageEdittext;
    private Button pillListadder;
    private EditText pillIdedittext;
    private Button sendButton;
    private Button pillTimePicker;
    private Button clearButton;
    private static final String BUCKET_NAME = "smartpills";
    private static final String OBJECT_KEY = "userdetails/userdetails.txt";

    private AmazonS3Client s3Client;

    Human human = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pillTimePicker = findViewById(R.id.pillAdd);
        sendButton = findViewById(R.id.button);
        clearButton = findViewById(R.id.clear);
        pillIdedittext = findViewById(R.id.pillId);
        dosageEdittext = findViewById(R.id.dosageSpinner);
        //  pillTimingArrayList=new ArrayList<>();
        pillListadder = findViewById(R.id.pillAdd2);
        naemEditText = findViewById(R.id.name);

        BasicAWSCredentials credentials = new BasicAWSCredentials("AKIAWZE4LHRMMINZAQYH", "/J0o48inVABsBgAvHSS6jQ0rfslq72hpZYTd7sAi");
        s3Client = new AmazonS3Client(credentials);
        s3Client.setRegion(Region.getRegion(Regions.AP_SOUTHEAST_1));
        // Get the Spinner view by its id

        dosageEdittext.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selecteddosageValue = parent.getItemAtPosition(position).toString(); // Get the selected item and convert it to a String
                // Do something with the current value, such as update a TextView or send it to a server
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timingEditText = findViewById(R.id.timeEditText);


                pillIdedittext = findViewById(R.id.pillId);
                if (!naemEditText.getText().toString().isEmpty()) naemEditText.setText(" ");
                if (!timingEditText.getText().toString().isEmpty()) timingEditText.setText(" ");
                if (!pillIdedittext.getText().toString().isEmpty()) pillIdedittext.setText(" ");
                human = null;
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

        pillIdedittext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(MainActivity.this, view);
                popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());

                // Set an OnMenuItemClickListener for the menu items
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        pillIdedittext.setText(menuItem.getTitle().toString());
                        return true;
                    }
                });

                // Show the pop-up menu
                popupMenu.show();
            }
        });

        pillTimePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get current time
                final Calendar c = Calendar.getInstance();
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                        // Update text view with selected time

                        timingEditText = findViewById(R.id.timeEditText);
                        String newtime = hourOfDay + ":" + String.format("%02d", minute);
                        timingEditText.setText(newtime);
                        if (human == null) {
                            human = new Human(naemEditText.getText().toString());
//                            selecteddosageValue=dosageEdittext.toString();
//                            Log.d("broo",selecteddosageValue);
                            human.setPillObject(pillIdedittext.getText().toString(), Integer.parseInt(selecteddosageValue));
                        }

                    }
                }, hour, minute, false);
                timePickerDialog.show();
            }
        });

        pillListadder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //  String TimePickerTime = timingEditText.getText().toString();
                // empty edge case
//                Log.d("brogh",naemEditText.getText().toString());
                if (timingEditText == null || pillIdedittext == null || naemEditText == null) {
                    Toast.makeText(MainActivity.this, "Please input other details first !", Toast.LENGTH_SHORT).show();
                } else if (!human.pillObject.time.contains(timingEditText.getText().toString().isEmpty())) {

                    human.pillObject.time.add(timingEditText.getText().toString());
//                    Toast.makeText(MainActivity.this, "Time Added", Toast.LENGTH_SHORT).show();
                    Toast.makeText(MainActivity.this, human.pillObject.getName() + " " + String.valueOf(human.pillObject.getPillDosage()) + " " + human.pillObject.time.get(human.pillObject.time.size() - 1), Toast.LENGTH_SHORT).show();

                } else
                    Toast.makeText(MainActivity.this, "Time already exists !", Toast.LENGTH_SHORT).show();

            }
        });

    }

    private class UploadDataTask extends AsyncTask<Void, Void, Void> {


        protected Void doInBackground(Void... voids) {
            File directory = new File(getApplicationContext().getFilesDir() + "/data/data/com.example.smartpilldispenser/code_cache/.ll/");
            if (!directory.exists()) {
                directory.mkdir();
            }
            timingEditText = findViewById(R.id.timeEditText);
            dosageEdittext = findViewById(R.id.dosageSpinner);
            naemEditText = findViewById(R.id.name);
            pillIdedittext = findViewById(R.id.pillId);


            // Get the current time


//----------------------------------------OOP-----------------------------------------
//            Human human = new Human(naemEditText.getText().toString());


            String allTimings = "";
//            for(String s:pillTimingArrayList) {
//                human.pillObject.setTime(s);
//                allTimings+=s;
//                allTimings+=",";
//
//            }

            for (String s : human.pillObject.time) {
//                human.pillObject.setTime(s);
//                if(s!=null) {
                allTimings += s;
                allTimings += ",";
//                }
            }

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Name: ").append(human.getHumanName()).append("\n").append("Time: ").append(allTimings).append("\n").append("Pill Name: ").append(human.pillObject.getName()).append("\n").append("Dosage: ").append(human.pillObject.getPillDosage());

            String formattedString = stringBuilder.toString();


//-------------------------------------------------------------------------------------


            String FinalpillConfig = naemEditText.getText().toString() + "," + timingEditText.getText().toString() + "," + selecteddosageValue + "," + pillIdedittext.getText().toString();
            AmazonS3 s3 = new AmazonS3Client(new BasicAWSCredentials("AKIAWZE4LHRMDMAQAMUT", "rjMk9by0v2ZIvY2SIQgKDhF/A8eaaSnzEA9LB0SF"));
            String bucketName = "smartpills";
            String key = "userdetails/userdetails.txt";
            String fileContents = formattedString; // Replace with your own file contents
            human = null;
            byte[] contentAsBytes = fileContents.getBytes(StandardCharsets.UTF_8);
            ByteArrayInputStream contentsAsStream = new ByteArrayInputStream(contentAsBytes);
            ObjectMetadata md = new ObjectMetadata();
            md.setContentLength(contentAsBytes.length);

            try {
                PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, contentsAsStream, md);
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


        protected void onPostExecute(Void aVoid) {

            // Code to be executed after the upload is complete, such as displaying a success message to the user
            Toast.makeText(MainActivity.this, "Pill Confguration Stored", Toast.LENGTH_SHORT).show();
            human = null;
        }
    }


}
