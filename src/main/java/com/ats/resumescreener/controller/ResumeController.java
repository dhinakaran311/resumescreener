package com.ats.resumescreener.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ats.resumescreener.service.ResumeService;

@RestController
@RequestMapping("/api")
public class ResumeController {

    @Autowired
    ResumeService service;
    @PostMapping("/resume")
    public String uploadFile(@RequestParam("file") MultipartFile file) {
        return service.handleFile(file);
    }

    @PostMapping("/match")
    public String matchResume(
            @RequestParam("file") MultipartFile file,
            @RequestParam("jd") String jobDescription) {

        return service.matchResume(file, jobDescription);
    }
}