package com.signimusTask.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.dialogflow.v2.SessionsClient;
import com.google.cloud.dialogflow.v2.SessionsSettings;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;

@Configuration
public class GoogleAssistantAuthConfig {

    @Value("${google.assistant.service-account-key}")
    private String serviceAccountKeyPath;

    @Bean
    public GoogleCredentials googleCredentials() throws IOException {
        return GoogleCredentials.fromStream(new FileInputStream(serviceAccountKeyPath))
                .createScoped("https://www.googleapis.com/auth/dialogflow");
    }
    
    @Bean
    public SessionsClient sessionsClient() throws IOException {
        GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(serviceAccountKeyPath))
                .createScoped("https://www.googleapis.com/auth/cloud-platform");

        SessionsSettings sessionsSettings = SessionsSettings.newBuilder()
                .setCredentialsProvider(() -> credentials)
                .build();
                
        return SessionsClient.create(sessionsSettings);
    }
}
