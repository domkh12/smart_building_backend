package edu.npic.smartBuilding.features.file;

import edu.npic.smartBuilding.features.file.dto.FileResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
@Slf4j
public class FileController {

    private final FileService fileService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    FileResponse uploadFile(@RequestPart MultipartFile file) throws IOException {
        return fileService.uploadFile(file);
    }

    @GetMapping
    List<FileResponse> findAll() {
        return fileService.findAll();
    }

}
