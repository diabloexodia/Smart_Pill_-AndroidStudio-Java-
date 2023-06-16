package com.example.smartpilldispenser;


import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;
import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.regions.Regions;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.google.firebase.auth.FirebaseAuth;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    private EditText timingEditText;
    static private String selecteddosageValue;

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
        pillListadder = findViewById(R.id.pillAdd2);
        naemEditText = findViewById(R.id.name);

        BasicAWSCredentials credentials = new BasicAWSCredentials("Access_Key", "Secret_Access_Key");
        s3Client = new AmazonS3Client(credentials);
        s3Client.setRegion(Region.getRegion(Regions.AP_SOUTHEAST_1));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#E91E63"));
        }

        dosageEdittext.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selecteddosageValue = parent.getItemAtPosition(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        pillIdedittext.setText(menuItem.getTitle().toString());
                        return true;
                    }
                });
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
                if (timingEditText == null || pillIdedittext == null || naemEditText == null) {
                    Toast.makeText(MainActivity.this, "Please input other details first !", Toast.LENGTH_SHORT).show();
                } else if (!human.pillObject.time.contains(timingEditText.getText().toString())) {

                    human.pillObject.time.add(timingEditText.getText().toString());
                    Toast.makeText(MainActivity.this, human.pillObject.time.get(human.pillObject.time.size() - 1) +" added", Toast.LENGTH_SHORT).show();

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


//----------------------------------------OOP-----------------------------------------
            String allTimings = "";

            for (String s : human.pillObject.time) {
                allTimings += s;
                allTimings += ",";
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Name: ").append(human.getHumanName()).append("\n").append("Time: ").append(allTimings).append("\n").append("Pill Name: ").append(human.pillObject.getName()).append("\n").append("Dosage: ").append(human.pillObject.getPillDosage());
            String formattedString = stringBuilder.toString();

//-------------------------------------------------------------------------------------


            String FinalpillConfig = naemEditText.getText().toString() + "," + timingEditText.getText().toString() + "," + selecteddosageValue + "," + pillIdedittext.getText().toString();
            AmazonS3 s3 = new AmazonS3Client(new BasicAWSCredentials("Access_Key", "Secret_Access_Key"));
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

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.logout) {
            // Handle the settings item
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            mAuth.signOut();
            Toast.makeText(this, "Logged out Successfully", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));

            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
