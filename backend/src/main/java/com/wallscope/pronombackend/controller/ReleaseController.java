package com.wallscope.pronombackend.controller;

import com.wallscope.pronombackend.config.ApplicationConfig;
import com.wallscope.pronombackend.dao.ContainerSignatureDAO;
import com.wallscope.pronombackend.dao.FileFormatDAO;
import com.wallscope.pronombackend.dao.SubmissionDAO;
import com.wallscope.pronombackend.model.ContainerSignature;
import com.wallscope.pronombackend.model.Feedback;
import com.wallscope.pronombackend.model.FileFormat;
import com.wallscope.pronombackend.model.InternalSignature;
import com.wallscope.pronombackend.soap.BinarySignatureFileWrapper;
import com.wallscope.pronombackend.soap.ContainerSignatureFileWrapper;
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
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.wallscope.pronombackend.controller.RESTController.distinctByKey;
import static com.wallscope.pronombackend.dao.SubmissionDAO.statusList;
import static com.wallscope.pronombackend.utils.RDFUtil.makeResource;

@Controller
public class ReleaseController {
    Logger logger = LoggerFactory.getLogger(ReleaseController.class);

    // EDITORIAL
    @GetMapping("/editorial/releases")
    public String editorialIndex(Model model) {
        Map<String, Instant> binary = SignatureStorageManager.getBinarySignatureList();
        Map<String, Instant> container = SignatureStorageManager.getContainerSignatureList();
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

    @GetMapping(value = "/releases/download/{type}/{file}", produces = "application/xml")
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
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Targeted file doesn't exist: " + file);
            }
            return Files.readAllBytes(download);
        } catch (IOException e) {
            logger.trace("FAILED TO RETURN FILE: " + file);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong");
        }
    }

    @PostMapping(value = {"/editorial/releases/create-release"}, produces = "application/xml")
    public String create(
            HttpServletRequest request,
            @RequestParam("target") String target,
            @RequestParam("type") String type,
            @RequestParam("status") String status,
            @RequestParam("version") String vStr,
            @RequestParam(defaultValue = "false") Boolean fullRelease
    ) throws Exception {
        if (!List.of("release", "test").contains(target)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid release type: " + target);
        }
        if (!List.of("container", "binary").contains(type)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid signature type: " + type);
        }
        int version = 0;
        if (!vStr.isBlank()) {
            try {
                version = Integer.parseInt(vStr);
            } catch (Exception ignored) {
            }
        }
        // Publishes a release
        if (target.equals("release")) {
            FileFormatDAO ffDao = new FileFormatDAO();
            ffDao.publishRelease();
            String filename;
            Object signature;
            File xml;
            JAXBContext ctx;
            if (type.equals("binary")) {
                FileFormatDAO dao = new FileFormatDAO();
                List<FileFormat> fs = dao.getAllForSignature();
                List<InternalSignature> signatures = fs.stream().flatMap(f -> f.getInternalSignatures().stream())
                        .filter(distinctByKey(InternalSignature::getID))
                        .sorted(InternalSignature::compareTo)
                        .collect(Collectors.toList());
                fs.sort(FileFormat::compareTo);
                request.setAttribute("formats", fs);
                request.setAttribute("signatures", signatures);
                BinarySignatureFileWrapper binSig = new RESTController().xmlBinarySignatureHandler(request);
                filename = ApplicationConfig.BINARY_SIG_NAME.formatted(version);
                binSig.setVersion(new BigInteger("" + version));
                ctx = JAXBContext.newInstance(BinarySignatureFileWrapper.class);
                xml = Paths.get(SignatureStorageManager.getBinaryDir().toString(), filename).toFile();
                signature = binSig;
            } else {
                ContainerSignatureDAO dao = new ContainerSignatureDAO();
                List<FileFormat> fs = dao.getAllForContainerSignature();
                List<ContainerSignature> signatures = fs.stream().flatMap(f -> f.getContainerSignatures().stream())
                        .filter(distinctByKey(ContainerSignature::getID))
                        .sorted(ContainerSignature::compareTo)
                        .collect(Collectors.toList());
                fs.sort(FileFormat::compareTo);
                request.setAttribute("formats", fs);
                request.setAttribute("signatures", signatures);
                String datePattern = "yyyyMMdd";
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(datePattern);
                filename = ApplicationConfig.CONTAINER_SIG_NAME.formatted(dateFormatter.format(LocalDate.now(ZoneId.of("Europe/London"))));
                ContainerSignatureFileWrapper contSig = new RESTController().xmlContainerSignatureHandler(request);
                contSig.setVersion(new BigInteger("" + version));
                ctx = JAXBContext.newInstance(ContainerSignatureFileWrapper.class);
                xml = Paths.get(SignatureStorageManager.getContainerDir().toString(), filename).toFile();
                signature = contSig;
            }
            Marshaller marshaller = ctx.createMarshaller();
            marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_ENCODING, "UTF-8"); //NOI18N
            marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.marshal(signature, new FileOutputStream(xml));
            return "redirect:/releases/download/" + type + "/" + filename;
        }

        // Generates a test release file
        Resource minStatus = makeResource(RDFUtil.PRONOM.Submission.statusId + status);
        if (!SubmissionDAO.statuses.contains(minStatus.getURI())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid signature status: " + status);
        }
        SubmissionDAO dao = new SubmissionDAO();
        if (type.equals("binary")) {
            List<FileFormat> fs = dao.getForBinarySignature(statusList(minStatus.getURI()));
            List<InternalSignature> signatures = fs.stream().flatMap(f -> f.getInternalSignatures().stream()).collect(Collectors.toList());
            if (fullRelease) {
                FileFormatDAO ffDao = new FileFormatDAO();
                fs.addAll(ffDao.getAllForSignature());
                signatures.addAll(fs.stream().flatMap(f -> f.getInternalSignatures().stream()).collect(Collectors.toList()));
            }
            signatures = signatures.stream().filter(distinctByKey(InternalSignature::getID))
                    .sorted(InternalSignature::compareTo)
                    .collect(Collectors.toList());
            fs.sort(FileFormat::compareTo);
            request.setAttribute("formats", fs);
            request.setAttribute("signatures", signatures);
            return "forward:/signature.xml";
        }
        List<FileFormat> fs = dao.getForContainerSignature(statusList(minStatus.getURI()));
        List<ContainerSignature> signatures = fs.stream().flatMap(f -> f.getContainerSignatures().stream()).collect(Collectors.toList());
        if (fullRelease) {
            ContainerSignatureDAO ffDao = new ContainerSignatureDAO();
            fs.addAll(ffDao.getAllForContainerSignature());
            signatures.addAll(fs.stream().flatMap(f -> f.getContainerSignatures().stream()).collect(Collectors.toList()));
        }
        signatures = signatures.stream().filter(distinctByKey(ContainerSignature::getID))
                .sorted(ContainerSignature::compareTo)
                .collect(Collectors.toList());
        fs.sort(FileFormat::compareTo);
        request.setAttribute("formats", fs);
        request.setAttribute("signatures", signatures);
        return "forward:/container-signature.xml";
    }

    // PUBLIC
    @GetMapping("/release-notes/{version}")
    public String relNotesVersion(Model model, @PathVariable(required = false) String version) {
        return "rel-notes-single";
    }

    @GetMapping("/release-notes")
    public String publicIndex(Model model) {
        Map<String, Instant> binary = SignatureStorageManager.getBinarySignatureList();
        Map<String, Instant> container = SignatureStorageManager.getContainerSignatureList();
        model.addAttribute("binary", binary);
        model.addAttribute("container", container);
        return "rel-notes-list";
    }
}
