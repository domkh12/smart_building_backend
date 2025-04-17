package edu.npic.smartBuilding.features.file;

import edu.npic.smartBuilding.features.file.dto.FileResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface FileService {

    List<FileResponse> findAll();

    FileResponse uploadFile(MultipartFile file) throws IOException;
}
