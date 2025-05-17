package com.example.bordjroject.Service;

import com.example.bordjroject.Entity.Board_Entity;
import com.example.bordjroject.repository.BordRepository;
import jakarta.servlet.ServletOutputStream;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpServletResponse;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


@RequiredArgsConstructor
@Service
public class BoardService {

    private final BordRepository bordRepository;

    public void write(Board_Entity board, MultipartFile file) throws IOException {
        if (file != null && !file.isEmpty()) {
            String uploadDir = System.getProperty("user.dir") + File.separator + "upload";

            File directory = new File(uploadDir);
            if (!directory.exists() && !directory.mkdirs()) {
                throw new IOException("업로드 폴더 생성 실패: " + uploadDir);
            }

            String originalFilename = file.getOriginalFilename();
            String filePath = uploadDir + File.separator + originalFilename;

            file.transferTo(new File(filePath));
            board.setFilename(originalFilename);
        }
        bordRepository.save(board);
    }

    public List<Board_Entity> findAllpost() {
        return bordRepository.findAll();
    }

    public void downloadFile(String filename, HttpServletResponse response) throws IOException {
        String uploadDir = System.getProperty("user.dir") + File.separator + "upload";
        Path filePath = Paths.get(uploadDir).resolve(filename);
        File file = filePath.toFile();

        if (!file.exists()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        response.setContentType("application/octet-stream");

        String encodedFilename = URLEncoder.encode(filename, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + encodedFilename);

        try (FileInputStream fis = new FileInputStream(file);
             ServletOutputStream os = response.getOutputStream()) {

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.flush();
        }
    }

    public List<Board_Entity> findPostsByUsername(String username) {
        return bordRepository.findByUsername(username);
    }

    public void deletePost(Integer id, String username) {
        Board_Entity post = bordRepository.findById(id).orElse(null);

        if (post != null && post.getUsername().equals(username)) {
            if (post.getFilename() != null) {
                String uploadDir = System.getProperty("user.dir") + File.separator + "upload";
                Path filePath = Paths.get(uploadDir).resolve(post.getFilename());
                File file = filePath.toFile();
                if (file.exists()) {
                    file.delete();
                }
            }
            bordRepository.delete(post);
        }
    }

    public Board_Entity findPostById(Integer id) {
        return bordRepository.findById(id).orElse(null);
    }

    public void updatePost(Integer id, Board_Entity updatedPost, MultipartFile file, String username) throws IOException {
        Board_Entity existingPost = bordRepository.findById(id).orElse(null);

        if (existingPost != null && existingPost.getUsername().equals(username)) {
            existingPost.setTitle(updatedPost.getTitle());
            existingPost.setContent(updatedPost.getContent());

            if (file != null && !file.isEmpty()) {
                // 기존 파일 삭제
                if (existingPost.getFilename() != null) {
                    String uploadDir = System.getProperty("user.dir") + File.separator + "upload";
                    Path filePath = Paths.get(uploadDir).resolve(existingPost.getFilename());
                    File oldFile = filePath.toFile();
                    if (oldFile.exists()) oldFile.delete();
                }

                // 새 파일 저장
                String uploadDir = System.getProperty("user.dir") + File.separator + "upload";
                String newFilename = file.getOriginalFilename();
                String newFilePath = uploadDir + File.separator + newFilename;
                file.transferTo(new File(newFilePath));

                existingPost.setFilename(newFilename);
            }

            bordRepository.save(existingPost);
        }
    }
}
