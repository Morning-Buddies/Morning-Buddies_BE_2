package com.ghpg.morningbuddies.domain.file.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileCommandService {

    String saveFile(MultipartFile file) throws IOException;
}
