package com.game.chess.Service;

import com.game.chess.Exception.DepositNotFoundException;
import com.game.chess.Exception.FileSecurityException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.UUID;

@Service
public class SecureUploadService {

    private static final long MAX_SIZE = 5L * 1024 * 1024; // 5 MB
    private static final byte[] PNG  = {(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};
    private static final byte[] JPEG = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF};

    private final Path root;

    public SecureUploadService(@Value("${app.upload.dir}") String dir) throws IOException {
        this.root = Paths.get(dir).toAbsolutePath().normalize(); // Folder OUTSIDE the web root[cite: 1]
        Files.createDirectories(root);
    }

    /** Saves the uploaded receipt securely and returns a randomized UUID filename. */
    public String saveReceipt(MultipartFile file) {
        if (file == null || file.isEmpty()) throw new FileSecurityException("File is empty");
        if (file.getSize() > MAX_SIZE) throw new FileSecurityException("File is larger than 5 MB");

        try (InputStream in = file.getInputStream()) {
            byte[] header = in.readNBytes(8);
            String extension;
            if (startsWith(header, PNG)) extension = ".png";
            else if (startsWith(header, JPEG)) extension = ".jpg";
            else throw new FileSecurityException("Only real PNG or JPEG images are allowed");

            String storedName = UUID.randomUUID() + extension;
            Path target = root.resolve(storedName).normalize();

            if (!target.startsWith(root)) throw new FileSecurityException("Invalid path");

            try (OutputStream out = Files.newOutputStream(target, StandardOpenOption.CREATE_NEW)) {
                out.write(header);
                in.transferTo(out);
            }
            return storedName;
        } catch (IOException e) {
            throw new FileSecurityException("Could not store the file");
        }
    }

    /** Loads a stored receipt safely for admin review, preventing path traversal attacks. */
    public Resource load(String storedName) {
        Path file = root.resolve(storedName).normalize();

        // Defense-in-depth: Ensure the resolved path stays strictly within our upload root
        if (!file.startsWith(root)) {
            throw new FileSecurityException("Cannot read file outside upload directory");
        }

        Resource resource = new FileSystemResource(file);
        if (resource.exists() && resource.isReadable()) {
            return resource;
        } else {
            throw new DepositNotFoundException("Receipt file not found on disk: " + storedName);
        }
    }

    private boolean startsWith(byte[] data, byte[] prefix) {
        if (data.length < prefix.length) return false;
        for (int i = 0; i < prefix.length; i++) if (data[i] != prefix[i]) return false;
        return true;
    }
}