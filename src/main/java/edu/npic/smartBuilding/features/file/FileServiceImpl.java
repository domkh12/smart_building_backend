package edu.npic.smartBuilding.features.file;

import edu.npic.smartBuilding.features.file.dto.FileResponse;
import edu.npic.smartBuilding.features.user.UserRepository;
import edu.npic.smartBuilding.util.FileUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService{

    private final UserRepository userRepository;

    @Value("${file-server.server-path}")
    private String serverPath;

    @Value("${file-server.base-uri}")
    private String baseUri;

    @Override
    public List<FileResponse> findAll() {

        Path path = Path.of(serverPath);
        File folder = path.toFile();

        if(!folder.exists()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Folder not found");
        }

        File[] files = folder.listFiles();

        if(files == null || files.length == 0){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No files found");
        }

        List<FileResponse> fileResponses = new ArrayList<>();
        for(File file : files){
            fileResponses.add(FileResponse.builder()
                            .name(file.getName())
                            .extension(FileUtil.extractExtension(file.getName()))
                            .size(file.length())
                            .uri(baseUri+file.getName())
                    .build());
        }

        return fileResponses;
    }

    @Override
    public FileResponse uploadFile(MultipartFile file) throws IOException {

        String newFileName = FileUtil.generateFileName(file.getOriginalFilename());
        String extension = FileUtil.extractExtension(file.getOriginalFilename());

        Path path = Path.of(serverPath + newFileName);
        Files.copy(file.getInputStream(), path);

        return FileResponse.builder()
                .name(newFileName)
                .size(file.getSize())
                .extension(extension)
                .uri(baseUri + newFileName)
                .build();
    }

}
