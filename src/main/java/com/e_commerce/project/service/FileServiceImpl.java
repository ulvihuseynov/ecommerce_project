package com.e_commerce.project.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {

    @Override
    public String uploadImage(String path, MultipartFile multipartFile) throws IOException {

        String originalFilename = multipartFile.getOriginalFilename();

        String randomId= UUID.randomUUID().toString();

        assert originalFilename != null;
        String fileName=randomId.concat(originalFilename.substring(originalFilename.lastIndexOf(".")));

        String filePath=path+ File.separator+fileName;

        File folder=new File(path);

        if (!folder.exists())
            folder.mkdir();

        Files.copy(multipartFile.getInputStream(), Paths.get(filePath));
        return fileName;
    }
}
