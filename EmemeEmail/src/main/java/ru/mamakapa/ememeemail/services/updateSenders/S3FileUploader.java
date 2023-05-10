package ru.mamakapa.ememeemail.services.updateSenders;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class S3FileUploader implements FileUploader{

    private final AmazonS3Client s3Client;

    @Value("${aws.bucketName}")
    private String bucketName;

    @Override
    public List<String> uploadFiles(List<File> files) {
        List<String> links = new ArrayList<>();
        for (var file : files) {
            var link = uploadFile(file);
            links.add(link);
        }
        return links;
    }

    private String uploadFile(File file) {
        String s3SavingDir = file.getName();
        s3Client.putObject(new PutObjectRequest(bucketName, s3SavingDir, file)
                .withCannedAcl(CannedAccessControlList.PublicRead));
        return s3SavingDir;
    }
}
