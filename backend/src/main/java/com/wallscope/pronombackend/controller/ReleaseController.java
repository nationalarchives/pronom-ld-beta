package com.wallscope.pronombackend.controller;

import com.wallscope.pronombackend.model.Feedback;
import com.wallscope.pronombackend.utils.SignatureStorageManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
public class ReleaseController {
    Logger logger = LoggerFactory.getLogger(ReleaseController.class);

    @GetMapping("/editorial/releases")
    public String index(Model model) {

        List<String> binary = Stream.of(Objects.requireNonNull(SignatureStorageManager.getBinaryDir().toFile().listFiles()))
                .filter(f -> f.isFile() && f.getName().endsWith(".xml"))
                .map(f -> f.getName().replace(".xml", ""))
                .collect(Collectors.toList());
        List<String> container = Stream.of(Objects.requireNonNull(SignatureStorageManager.getContainerDir().toFile().listFiles()))
                .filter(f -> f.isFile() && f.getName().endsWith(".xml"))
                .map(f -> f.getName().replace(".xml", ""))
                .collect(Collectors.toList());
        model.addAttribute("binary", binary);
        model.addAttribute("container", container);
        return "release-manager";
    }

    @PostMapping("/editorial/releases/new") // //new annotation since 4.3
    public String singleFileUpload(@RequestParam("file") MultipartFile file, @RequestParam(defaultValue = "false") Boolean container, RedirectAttributes redirectAttributes) {
        try {
            if (file == null || file.getOriginalFilename() == null || !file.getOriginalFilename().endsWith(".xml")) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid signature file: should be an XML file");
            }
            Path dir = SignatureStorageManager.getBinaryDir();
            if (container) {
                dir = SignatureStorageManager.getContainerDir();
            }
            // Get the file and save it somewhere
            byte[] bytes = file.getBytes();
            Path path = Paths.get(dir.toString(), file.getOriginalFilename());
            Files.write(path, bytes);
            redirectAttributes.addFlashAttribute("feedback", new Feedback(Feedback.Status.SUCCESS, "You successfully uploaded '" + file.getOriginalFilename() + "'"));

        } catch (IOException e) {
            e.printStackTrace();
        }

        return "redirect:/editorial/releases";
    }

    @PostMapping("/editorial/releases/rename-release/{type}/{file}")
    public String rename(@PathVariable String type, @PathVariable String file, @RequestParam("filename") String newName) {
        if (!List.of("container", "binary").contains(type)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid signature type: " + type);
        }
        Path dir = SignatureStorageManager.getBinaryDir();
        if (type.equals("container")) {
            dir = SignatureStorageManager.getContainerDir();
        }
        Path existing = Paths.get(dir.toString(), file + ".xml");
        File f = existing.toFile();
        if (!f.isFile()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Targeted file doesn't exist: " + file);
        }
        Path destination = Paths.get(dir.toString(), newName + ".xml");
        File destFile = destination.toFile();
        if (destFile.isFile()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A file with the target name already exists: " + newName);
        }
        if (!f.renameTo(destFile)) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to rename file");
        }

        return "redirect:/editorial/releases";
    }

    @PostMapping("/editorial/releases/delete-release/{type}/{file}")
    public String delete(@PathVariable String type, @PathVariable String file) {
        if (!List.of("container", "binary").contains(type)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid signature type: " + type);
        }
        Path dir = SignatureStorageManager.getBinaryDir();
        if (type.equals("container")) {
            dir = SignatureStorageManager.getContainerDir();
        }
        Path existing = Paths.get(dir.toString(), file + ".xml");
        File f = existing.toFile();
        if (!f.isFile()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Targeted file doesn't exist: " + file);
        }
        if (!f.delete()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to delete file");
        }

        return "redirect:/editorial/releases";

    }
}
