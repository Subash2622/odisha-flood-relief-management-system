package com.odisha.floodrelief.controller;

import com.odisha.floodrelief.exception.ResourceNotFoundException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Api(tags = "Files")
@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileController {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @ApiOperation("Download uploaded file by relative path segments")
    @GetMapping("/{folder}/{filename:.+}")
    public ResponseEntity<Resource> download(
            @PathVariable String folder,
            @PathVariable String filename) throws Exception {

        Path base = Paths.get(uploadDir).toAbsolutePath().normalize();
        Path file = base.resolve(folder).resolve(filename).normalize();

        if (!file.startsWith(base) || !Files.exists(file) || !Files.isRegularFile(file)) {
            throw new ResourceNotFoundException("File not found");
        }

        Resource resource = new UrlResource(file.toUri());
        String contentType = Files.probeContentType(file);
        if (contentType == null) {
            contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                .body(resource);
    }
}
