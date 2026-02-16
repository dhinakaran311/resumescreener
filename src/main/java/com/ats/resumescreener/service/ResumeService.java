package com.ats.resumescreener.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ResumeService {

    public String handleFile(MultipartFile file){
        return file.getOriginalFilename();
    }
}
