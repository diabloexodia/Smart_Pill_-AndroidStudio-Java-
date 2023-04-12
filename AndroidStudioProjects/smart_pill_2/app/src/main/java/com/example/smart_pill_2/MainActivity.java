package com.example.smart_pill_2;

import androidx.appcompat.app.AppCompatActivity;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.core.model.AWSConfiguration;

import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.core.model.temporal.Temporal;
import com.amplifyframework.datastore.AWSDataStorePlugin;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Button submit=findViewById(R.id.submit);
        try {
            Amplify.addPlugin(new AWSDataStorePlugin());
            Amplify.configure(getApplicationContext());

            // Use your AWS access key and secret key here
            Amplify.AWSConfiguration awsConfiguration = Amplify.AlreadyConfiguredException.builder()
                    .put("accessKey", "YOUR_ACCESS_KEY")
                    .put("secretKey", "YOUR_SECRET_KEY")
                    .build();
            Amplify.configure();

            Log.i("Amplify", "Initialized Amplify");

        } catch (AmplifyException error) {
            Log.e("Amplify", "Could not initialize Amplify", error);
        }
        EditText nameEditText=findViewById(R.id.name);
        EditText dosageEditText=findViewById(R.id.dosage);
        EditText timeEditText=findViewById(R.id.timeeditext);
        try {
            //Amplify.addPlugin(new AWSApiPlugin()); // UNCOMMENT this line once backend is deployed
            Amplify.addPlugin(new AWSDataStorePlugin());
            Amplify.configure(getApplicationContext());
            Log.i("Amplify", "Initialized Amplify");

        } catch (AmplifyException error) {
            Log.e("Amplify", "Could not initialize Amplify", error);
        }


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the values from the UI
                String name = nameEditText.getText().toString();
                int dosage = Integer.parseInt(dosageEditText.getText().toString());
                String time = timeEditText.getText().toString();

                // Create a SmartPill object with the input values
                SmartPill item = SmartPill.builder()
                        .name(name)
                        .dosage(dosage)
                       // .time(Temporal.DateTime(time))
                        .build();

                // Save the SmartPill object to AWS DynamoDB
                Amplify.DataStore.save(
                        item,
                        success -> Log.i("Amplify", "Saved item: " + success.item().getId()),
                        error -> Log.e("Amplify", "Could not save item to DataStore", error)
                );
            }
        });



    }

}