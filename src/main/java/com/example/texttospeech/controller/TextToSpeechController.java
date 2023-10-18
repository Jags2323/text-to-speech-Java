//package com.example.texttospeech.controller;
//
//import com.google.api.gax.core.FixedCredentialsProvider;
//import com.google.auth.oauth2.GoogleCredentials;
//import com.google.auth.oauth2.ServiceAccountCredentials;
//import com.google.cloud.texttospeech.v1.*;
//import com.google.protobuf.ByteString;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.OutputStream;
//
//@RequestMapping("/texttospeech")
//@RestController
//public class TextToSpeechController {
//
//    @GetMapping("")
//    public String synthesizeTextToSpeech(@RequestParam String text) {
//        try {
//            // Set up authentication using your JSON key file
//            GoogleCredentials credentials = ServiceAccountCredentials.fromStream(
//                    new FileInputStream("C:\\Users\\jagad\\OneDrive\\Desktop\\text-to-speech\\text-to-speech\\text-to-speech-key.json"));
//
//            TextToSpeechClient textToSpeechClient = TextToSpeechClient.create(TextToSpeechSettings.newBuilder().setCredentialsProvider(
//                    FixedCredentialsProvider.create(credentials)
//            ).build());
//
//            SynthesisInput input = SynthesisInput.newBuilder().setText(text).build();
//
//            VoiceSelectionParams voice = VoiceSelectionParams.newBuilder()
//                    .setLanguageCode("en-US")
//                    .setSsmlGender(SsmlVoiceGender.NEUTRAL)
//                    .build();
//
//            AudioConfig audioConfig = AudioConfig.newBuilder().setAudioEncoding(AudioEncoding.LINEAR16).build();
//
//            SynthesizeSpeechResponse response = textToSpeechClient.synthesizeSpeech(input, voice, audioConfig);
//
//            // Save the audio content to an MP3 file
//            try (OutputStream out = new FileOutputStream("output.mp3")) {
//                ByteString audioContents = response.getAudioContent();
//                out.write(audioContents.toByteArray());
//                System.out.println("Audio content written to file \"output.mp3\"");
//            } catch (IOException e) {
//                e.printStackTrace();
//                return "Error saving audio content: " + e.getMessage();
//            }
//
//            textToSpeechClient.close();
//
//            return "Audio content saved as 'output.mp3'";
//        } catch (IOException e) {
//            e.printStackTrace();
//            return "Error: " + e.getMessage();
//        }
//    }
//}

package com.example.texttospeech.controller;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.texttospeech.v1.*;
import com.google.protobuf.ByteString;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

@RequestMapping("/texttospeech")
@RestController
public class TextToSpeechController {

    @GetMapping(value = "", produces = "audio/mpeg")
    public byte[] synthesizeTextToSpeech(@RequestParam String text) {
        try {
            // Setting up authentication using  JSON key file
            GoogleCredentials credentials = ServiceAccountCredentials.fromStream(
                    new FileInputStream("C:\\Users\\jagad\\OneDrive\\Desktop\\text-to-speech\\text-to-speech\\text-to-speech-key.json"));

            TextToSpeechClient textToSpeechClient = TextToSpeechClient.create(TextToSpeechSettings.newBuilder().setCredentialsProvider(
                    FixedCredentialsProvider.create(credentials)
            ).build());

            SynthesisInput input = SynthesisInput.newBuilder().setText(text).build();

            VoiceSelectionParams voice = VoiceSelectionParams.newBuilder()
                    .setLanguageCode("en-US")
                    .setSsmlGender(SsmlVoiceGender.NEUTRAL)
                    .build();

            AudioConfig audioConfig = AudioConfig.newBuilder().setAudioEncoding(AudioEncoding.LINEAR16).build();

            SynthesizeSpeechResponse response = textToSpeechClient.synthesizeSpeech(input, voice, audioConfig);

            // Return the audio content as a byte array
            ByteString audioContents = response.getAudioContent();
            textToSpeechClient.close();

            return audioContents.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }
}
