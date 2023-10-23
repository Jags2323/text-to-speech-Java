//package com.example.texttospeech.controller;
//
//import com.google.api.gax.core.FixedCredentialsProvider;
//import com.google.auth.oauth2.GoogleCredentials;
//import com.google.auth.oauth2.ServiceAccountCredentials;
//import com.google.cloud.bigtable.data.v2.BigtableDataClient;
//import com.google.cloud.bigtable.data.v2.BigtableDataSettings;
//import com.google.cloud.bigtable.data.v2.models.*;
//import com.google.cloud.texttospeech.v1.*;
//import com.google.protobuf.ByteString;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//import java.util.UUID;
//
//@RequestMapping("/texttospeech")
//@RestController
//public class TextToSpeechController {
//
//    private final TextToSpeechClient textToSpeechClient;
//    private final BigtableDataClient bigtableDataClient;
//
//    public TextToSpeechController() {
//        try {
//            // Set up authentication for Text-to-Speech
//            GoogleCredentials textToSpeechCredentials = ServiceAccountCredentials.fromStream(
//                    new FileInputStream("C:\\Users\\jagad\\OneDrive\\Desktop\\text-to-speech\\text-to-speech\\text-to-speech-key.json"));
//
//                    textToSpeechClient = TextToSpeechClient.create(
//                    TextToSpeechSettings.newBuilder().setCredentialsProvider(
//                            FixedCredentialsProvider.create(textToSpeechCredentials)
//                    ).build());
//
//            // Set up authentication for Bigtable
//            GoogleCredentials bigtableCredentials = ServiceAccountCredentials.fromStream(
//                    new FileInputStream("C:\\Users\\jagad\\OneDrive\\Desktop\\text-to-speech\\text-to-speech\\text-to-speech-key.json"));
//
//                    BigtableDataSettings bigtableSettings = BigtableDataSettings.newBuilder()
//                    .setCredentialsProvider(FixedCredentialsProvider.create(bigtableCredentials))
//                    .setProjectId("awesome-pilot-400117")
//                    .setInstanceId("text-to-speech")
//                    .build();
//            bigtableDataClient = BigtableDataClient.create(bigtableSettings);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//            throw new RuntimeException("Error initializing clients: " + e.getMessage());
//        }
//    }
//
//    @GetMapping(value = "", produces = "audio/mpeg")
//    public byte[] synthesizeTextToSpeech(@RequestParam String text) {
//        // Text-to-Speech logic
//        SynthesisInput input = SynthesisInput.newBuilder().setText(text).build();
//        VoiceSelectionParams voice = VoiceSelectionParams.newBuilder()
//                .setLanguageCode("en-US")
//                .setSsmlGender(SsmlVoiceGender.NEUTRAL)
//                .build();
//        AudioConfig audioConfig = AudioConfig.newBuilder()
//                .setAudioEncoding(AudioEncoding.LINEAR16)
//                .build();
//        SynthesizeSpeechResponse response = textToSpeechClient.synthesizeSpeech(input, voice, audioConfig);
//        ByteString audioContents = response.getAudioContent();
//
//        // Store user input in Bigtable
//        String tableId = "text-to-speech"; // Your Bigtable Table ID
//        String rowKey = generateUniqueRowKey();
//
//        RowMutation rowMutation = RowMutation.create(tableId, rowKey);
//        rowMutation.setCell("text-to-speech", "user_input", Arrays.toString(audioContents.toByteArray()));
//
//        bigtableDataClient.mutateRow(rowMutation);
//
//        return audioContents.toByteArray();
//    }
//
//    @GetMapping("/listdata")
//    public List<String> listDataFromBigtable() {
//        List<String> dataEntries = new ArrayList<>();
//
//        try {
//            // Define the Bigtable settings
//            BigtableDataSettings bigtableSettings = BigtableDataSettings.newBuilder()
//                    .setProjectId("awesome-pilot-400117")
//                    .setInstanceId("text-to-speech")
//                    .build();
//
//            // Create a BigtableDataClient
//            BigtableDataClient bigtableDataClient = BigtableDataClient.create(bigtableSettings);
//
//            // Define the Bigtable table and column family
//            String tableId = "text-to-speech"; // Your Bigtable Table ID
//            String columnFamily = "text-to-speech"; // Your column family name
//
//            // Fetch rows from Bigtable
//            Iterable<Row> rows = bigtableDataClient.readRows(Query.create(tableId));
//
//            for (Row row : rows) {
//                // Process each row and extract the relevant data
//                for (RowCell cell : row.getCells(Arrays.toString(columnFamily.getBytes()))) {
//                    // Assuming that the data is stored as a string
//                    String cellData = new String(cell.getValue().toByteArray());
//                    dataEntries.add(cellData);
//                }
//            }
//
//            // Close the Bigtable client when done
//            bigtableDataClient.close();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//            throw new RuntimeException("Error reading data from Bigtable: " + e.getMessage());
//        }
//
//        return dataEntries;
//    }
//
//    private String generateUniqueRowKey() {
//        // Generate a unique row key using a combination of timestamp and a random UUID
//        long timestamp = System.currentTimeMillis();
//        String uniqueId = UUID.randomUUID().toString();
//        return timestamp + "-" + uniqueId;
//    }
//}
//
//
//
//
//
package com.example.texttospeech.controller;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.bigtable.data.v2.BigtableDataClient;
import com.google.cloud.bigtable.data.v2.BigtableDataSettings;
import com.google.cloud.bigtable.data.v2.models.*;
import com.google.cloud.texttospeech.v1.*;
import com.google.protobuf.ByteString;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RequestMapping("/texttospeech")
@RestController
public class TextToSpeechController {

    private final TextToSpeechClient textToSpeechClient;
    private final BigtableDataClient bigtableDataClient;

    public TextToSpeechController() {
        try {
            // Set up authentication for Text-to-Speech
            Credentials textToSpeechCredentials = GoogleCredentials.getApplicationDefault();

            textToSpeechClient = TextToSpeechClient.create(
                    TextToSpeechSettings.newBuilder().setCredentialsProvider(
                            FixedCredentialsProvider.create(textToSpeechCredentials)
                    ).build());

            // Set up authentication for Bigtable
            Credentials bigtableCredentials = GoogleCredentials.getApplicationDefault();

            BigtableDataSettings bigtableSettings = BigtableDataSettings.newBuilder()
                    .setCredentialsProvider(FixedCredentialsProvider.create(bigtableCredentials))
                    .setProjectId("awesome-pilot-400117")
                    .setInstanceId("text-to-speech")
                    .build();
            bigtableDataClient = BigtableDataClient.create(bigtableSettings);

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error initializing clients: " + e.getMessage());
        }
    }

    @GetMapping(value = "", produces = "audio/mpeg")
    public byte[] synthesizeTextToSpeech(@RequestParam String text) {
        // Text-to-Speech logic
        SynthesisInput input = SynthesisInput.newBuilder().setText(text).build();
        VoiceSelectionParams voice = VoiceSelectionParams.newBuilder()
                .setLanguageCode("en-US")
                .setSsmlGender(SsmlVoiceGender.NEUTRAL)
                .build();
        AudioConfig audioConfig = AudioConfig.newBuilder()
                .setAudioEncoding(AudioEncoding.LINEAR16)
                .build();
        SynthesizeSpeechResponse response = textToSpeechClient.synthesizeSpeech(input, voice, audioConfig);
        ByteString audioContents = response.getAudioContent();

        // Store user input in Bigtable
        String tableId = "text-to-speech"; // Your Bigtable Table ID
        String rowKey = generateUniqueRowKey();

        RowMutation rowMutation = RowMutation.create(tableId, rowKey);
        rowMutation.setCell("text-to-speech", "user_input", Arrays.toString(audioContents.toByteArray()));

        bigtableDataClient.mutateRow(rowMutation);

        return audioContents.toByteArray();
    }

    @GetMapping("/listdata")
    public List<String> listDataFromBigtable() {
        List<String> dataEntries = new ArrayList<>();

        try {
            // Define the Bigtable settings
            BigtableDataSettings bigtableSettings = BigtableDataSettings.newBuilder()
                    .setProjectId("awesome-pilot-400117")
                    .setInstanceId("text-to-speech")
                    .build();

            // Create a BigtableDataClient
            BigtableDataClient bigtableDataClient = BigtableDataClient.create(bigtableSettings);

            // Define the Bigtable table and column family
            String tableId = "text-to-speech"; // Your Bigtable Table ID
            String columnFamily = "text-to-speech"; // Your column family name

            // Fetch rows from Bigtable
            Iterable<Row> rows = bigtableDataClient.readRows(Query.create(tableId));

            for (Row row : rows) {
                // Process each row and extract the relevant data
                for (RowCell cell : row.getCells(Arrays.toString(columnFamily.getBytes()))) {
                    // Assuming that the data is stored as a string
                    String cellData = new String(cell.getValue().toByteArray());
                    dataEntries.add(cellData);
                }
            }

            // Close the Bigtable client when done
            bigtableDataClient.close();

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error reading data from Bigtable: " + e.getMessage());
        }

        return dataEntries;
    }

    private String generateUniqueRowKey() {
        // Generate a unique row key using a combination of timestamp and a random UUID
        long timestamp = System.currentTimeMillis();
        String uniqueId = UUID.randomUUID().toString();
        return timestamp + "-" + uniqueId;
    }
}





