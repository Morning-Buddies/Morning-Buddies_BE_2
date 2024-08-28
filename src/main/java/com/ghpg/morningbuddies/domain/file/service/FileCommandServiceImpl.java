package com.ghpg.morningbuddies.domain.file.service;

import com.ghpg.morningbuddies.domain.file.entity.FileEntity;
import com.ghpg.morningbuddies.domain.file.repository.FileRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@Transactional
@RequiredArgsConstructor
public class FileCommandServiceImpl implements FileCommandService {

    private final FileRepository fileRepository;

    @Override
    public String saveFile(MultipartFile file) throws IOException {
        // 파일 데이터를 BLOB 형식으로 저장
        FileEntity dbFile = new FileEntity();
        dbFile.setFileName(file.getOriginalFilename());
        dbFile.setFileType(file.getContentType());
        dbFile.setData(file.getBytes());

        // 파일을 데이터베이스에 저장
        FileEntity savedFile = fileRepository.save(dbFile);
        return savedFile.getFileURL(); // 저장된 파일의 ID 반환
    }
}
