package com.e_commerce.project.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {

    String uploadImage(String path, MultipartFile multipartFile) throws Exception;
}
