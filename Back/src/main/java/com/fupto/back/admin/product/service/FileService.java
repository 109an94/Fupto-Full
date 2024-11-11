package com.fupto.back.admin.product.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileService {
    String saveFile(MultipartFile file, Long productId) throws IOException;
}
