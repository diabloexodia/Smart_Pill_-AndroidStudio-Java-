package com.example.smart_pill;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find the input fields and submit button
        EditText nameEditText = findViewById(R.id.name);
        EditText dosageEditText = findViewById(R.id.dosage);
        EditText timeEditText = findViewById(R.id.time);
        Button submitButton = findViewById(R.id.button1);

        // Set up the click listener for the submit button
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the values from the input fields
                String name = nameEditText.getText().toString();
                String dosage = dosageEditText.getText().toString();
                String time = timeEditText.getText().toString();
                DynamoDBManager.putThreeVariables(name, dosage, time);

            }
        });
    }
}
