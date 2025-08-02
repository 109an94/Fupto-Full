package com.fupto.back.admin.product.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
@Slf4j
public class DefaultFileService implements FileService {
    @Value("${file.upload.path}")
    private String uploadPath;

    public String saveFile(MultipartFile file, Long productId) throws IOException {
        // 프로젝트 루트 경로 가져오기
        String projectPath = System.getProperty("user.dir");

        // 전체 경로 생성 (프로젝트루트/uploads/products/ID)
        String directoryPath = projectPath + "/" + uploadPath + "/products/" + productId;
        File directory = new File(directoryPath);

        // 디렉토리가 없으면 생성
        if (!directory.exists()) {
            boolean created = directory.mkdirs();
            if (!created) {
                log.error("Failed to create directory: {}", directoryPath);
                throw new IOException("디렉토리 생성 실패: " + directoryPath);
            }
        }

        // 파일명 생성
        String originalName = file.getOriginalFilename();
        String extension = originalName.substring(originalName.lastIndexOf("."));
        String fileName = UUID.randomUUID().toString() + extension;

        // 파일 저장
        File targetFile = new File(directory, fileName);
        file.transferTo(targetFile);

        // uploadPath를 제외한 상대 경로만 반환
        return "products/" + productId + "/" + fileName;
    }
}