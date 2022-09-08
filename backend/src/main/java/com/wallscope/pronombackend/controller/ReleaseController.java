package com.wallscope.pronombackend.controller;

import com.wallscope.pronombackend.dao.ContainerSignatureDAO;
import com.wallscope.pronombackend.dao.FileFormatDAO;
import com.wallscope.pronombackend.dao.SubmissionDAO;
import com.wallscope.pronombackend.model.ContainerSignature;
import com.wallscope.pronombackend.model.Feedback;
import com.wallscope.pronombackend.model.FileFormat;
import com.wallscope.pronombackend.model.InternalSignature;
import com.wallscope.pronombackend.utils.RDFUtil;
import com.wallscope.pronombackend.utils.SignatureStorageManager;
import org.apache.jena.rdf.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.wallscope.pronombackend.controller.RESTController.distinctByKey;
import static com.wallscope.pronombackend.dao.SubmissionDAO.statusList;
import static com.wallscope.pronombackend.utils.RDFUtil.makeResource;

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

    @PostMapping("/editorial/releases/new")
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

    @GetMapping(value = "/editorial/releases/download/{type}/{file}", produces = "application/xml")
    public @ResponseBody
    byte[] downloadRelease(@PathVariable String type, @PathVariable String file) throws IOException {
        try {
            if (!file.endsWith(".xml")) {
                file = file + ".xml";
            }
            if (!List.of("container", "binary").contains(type)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid signature type: " + type);
            }
            Path dir = SignatureStorageManager.getBinaryDir();
            if (type.equals("container")) {
                dir = SignatureStorageManager.getContainerDir();
            }
            Path download = Paths.get(dir.toString(), file);
            File f = download.toFile();
            if (!f.isFile()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Targeted file doesn't exist: " + file);
            }
            return Files.readAllBytes(download);
        } catch (IOException e) {
            logger.debug("FAILED TO RETURN FILE: " + file);
            e.printStackTrace();
            return null;
        }
    }

    @PostMapping("/editorial/releases/create-release")
    public String create(HttpServletRequest request, @RequestParam("target") String target, @RequestParam("type") String type, @RequestParam("status") String status, @RequestParam("filename") String filename) throws DatatypeConfigurationException, JAXBException {
        // TODO: Generate XML file and store it alongside others when it's an actual release
        if (!List.of("release", "test").contains(target)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid release type: " + target);
        }
        if (!List.of("container", "binary").contains(type)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid signature type: " + type);
        }
        if (target.equals("release")) {
            FileFormatDAO dao = new FileFormatDAO();
            dao.publishRelease();
            return "redirect:/editorial/releases/download/" + type + "/" + filename;
        }
        Resource minStatus = makeResource(RDFUtil.PRONOM.Submission.statusId + status);
        if (!SubmissionDAO.statuses.contains(minStatus.getURI())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid signature status: " + status);
        }
        SubmissionDAO dao = new SubmissionDAO();
        if (type.equals("binary")) {
            List<FileFormat> fs = dao.getForBinarySignature(statusList(minStatus.getURI()));
            List<InternalSignature> signatures = fs.stream().flatMap(f -> f.getInternalSignatures().stream())
                    .filter(distinctByKey(InternalSignature::getID))
                    .sorted(InternalSignature::compareTo)
                    .collect(Collectors.toList());

            fs.sort(Comparator.comparingInt(f -> Integer.parseInt(f.getID())));
            request.setAttribute("formats", fs);
            request.setAttribute("signatures", signatures);
            return "forward:/signature.xml";
        }
        List<FileFormat> fs = dao.getForContainerSignature(statusList(minStatus.getURI()));
        List<ContainerSignature> signatures = fs.stream().flatMap(f -> f.getContainerSignatures().stream())
                .filter(distinctByKey(ContainerSignature::getID))
                .sorted(ContainerSignature::compareTo)
                .collect(Collectors.toList());

        fs.sort(Comparator.comparingInt(f -> Integer.parseInt(f.getID())));
        request.setAttribute("formats", fs);
        request.setAttribute("signatures", signatures);
        return "forward:/container-signature.xml";
    }
}
