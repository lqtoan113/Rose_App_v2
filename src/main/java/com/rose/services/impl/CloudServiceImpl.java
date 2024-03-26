package com.rose.services.impl;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.StorageClient;
import com.rose.exceptions.FireBaseException;
import com.rose.services.ICloudService;
import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.IOException;

@Service
public class CloudServiceImpl implements ICloudService {
    private static final Logger LOGGER = LogManager.getLogger(CloudServiceImpl.class);
    @Autowired
    private Properties properties;

    @EventListener
    public void init(ApplicationReadyEvent event) {
        try {

            ClassPathResource serviceAccount = new ClassPathResource("agile-being-318113-firebase-adminsdk-fzy8m-35cb4d29e0.json");

            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount.getInputStream()))
                    .setStorageBucket(properties.getBucketName())
                    .build();
            FirebaseApp.initializeApp(options);

        } catch (Exception ex) {
            LOGGER.error(ex.getMessage());
            throw new FireBaseException(HttpStatus.CONFLICT, "Something went wrong...");
        }
    }

    @Override
    public String getFileUrl(String name) {
        return String.format(properties.fileUrl, name);
    }

    @Override
    public String saveMultipartFile(MultipartFile file) throws IOException {

        Bucket bucket = StorageClient.getInstance().bucket();

        String name = generateFileName(file.getOriginalFilename());

        bucket.create(name, file.getBytes(), file.getContentType());
        LOGGER.info("UPLOADED: "+ name);
        return name;
    }

    @Override
    public String saveBufferedImage(BufferedImage bufferedImage, String originalFileName) throws IOException {

        byte[] bytes = getByteArrays(bufferedImage, getExtension(originalFileName));

        Bucket bucket = StorageClient.getInstance().bucket();

        String name = generateFileName(originalFileName);

        bucket.create(name, bytes);
        LOGGER.info("UPLOADED: "+ name);
        return name;
    }

    @Override
    public void delete(String name) throws IOException {

        Bucket bucket = StorageClient.getInstance().bucket();

        if (StringUtils.isEmpty(name)) {
            throw new FireBaseException(HttpStatus.BAD_REQUEST, "Invalid file name!");
        }
        Blob blob = bucket.get(name);

        if (blob == null) {
            throw new FireBaseException(HttpStatus.BAD_REQUEST, "File not found!");
        }
        LOGGER.info("DELETED IMAGE: "+ name);
        blob.delete();
    }

    @Data
    @Configuration
    @ConfigurationProperties(prefix = "firebase")
    public class Properties {

        private String bucketName;

        private String fileUrl;
    }

}
