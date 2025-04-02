package com.example.picbooker;

import java.io.FileInputStream;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

@Configuration
public class FirebaseConfig {

    @Value("${app.path-to-firebase}")
    private String pathToConfig;

    @Bean
    public Storage firebaseStorage() throws IOException {
        return StorageOptions.newBuilder()
                .setCredentials(
                        GoogleCredentials.fromStream(new FileInputStream(pathToConfig)))
                .build()
                .getService();

    }

    // snippet from website
    // FileInputStream serviceAccount =
    // new FileInputStream("path/to/serviceAccountKey.json");

    // FirebaseOptions options = new FirebaseOptions.Builder()
    // .setCredentials(GoogleCredentials.fromStream(serviceAccount))
    // .build();

    // FirebaseApp.initializeApp(options);
}
