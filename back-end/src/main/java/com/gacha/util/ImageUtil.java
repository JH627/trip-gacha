package com.gacha.util;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.gacha.model.dto.enums.ImageCategory;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Component
@RequiredArgsConstructor
public class ImageUtil {
    private final S3Client amazonS3Client;
    
    @Value("${cloud.aws.s3.bucket}")
	private String bucket;

    @Value("${cloud.aws.region.static}")
    private String region;

    public static File multipartFileToFile(MultipartFile multipartFile) throws IOException {
        File file = new File(System.getProperty("user.dir") + multipartFile.getOriginalFilename());
        multipartFile.transferTo(file);
        return file;
    }

    public String upload(MultipartFile file, ImageCategory imageCategory){
        String uploadImageUrl = null;
        String dirName = imageCategory.getUrl();

        try{
            String fileName = dirName + UUID.randomUUID() + "image";   // S3에 저장된 파일 이름
            File uploadFile = ImageUtil.multipartFileToFile(file);
            System.out.println(fileName);
            uploadImageUrl = putS3(uploadFile, fileName); // s3로 업로드
        } catch(IOException e){
            e.printStackTrace();
        }
        return uploadImageUrl;
    }

    private String putS3(File uploadFile, String fileName) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(fileName)
                .build();

        amazonS3Client.putObject(
                putObjectRequest,
                RequestBody.fromFile(uploadFile)
        );

        uploadFile.delete();

        return String.format("https://%s.s3.%s.amazonaws.com/%s", bucket, "ap-northeast-2", fileName);
    }
}
