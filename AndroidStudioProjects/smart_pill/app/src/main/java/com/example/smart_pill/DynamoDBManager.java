package com.example.smart_pill;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;

public class DynamoDBManager {

    private static final String ACCESS_KEY = "AKIAWZE4LHRMMINZAQYH";
    private static final String SECRET_KEY = "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";
    private static final String TABLE_NAME = "smart_pill_user_details";
    private static final Regions REGION = Regions.AP_SOUTHEAST_1;

    public static void putThreeVariables(String variable1, String variable2, String variable3) {
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(ACCESS_KEY, SECRET_KEY);
        AWSStaticCredentialsProvider awsCredentialsProvider = new AWSStaticCredentialsProvider(awsCredentials);
        AmazonDynamoDBClientBuilder builder = AmazonDynamoDBClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .withRegion(REGION);
        DynamoDB dynamoDB = new DynamoDB(builder.build());
        Table table = dynamoDB.getTable(TABLE_NAME);
        Item item = new Item()
                .withPrimaryKey("Variable1", variable1)
                .withString("Variable2", variable2)
                .withString("Variable3", variable3);
        table.putItem(item);
    }
}
