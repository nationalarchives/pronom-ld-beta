package com.wallscope.pronombackend.utils;

import com.github.rjeschke.txtmark.Processor;
import com.wallscope.pronombackend.config.ApplicationConfig;
import com.wallscope.pronombackend.model.MarkdownHelpers;
import com.wallscope.pronombackend.soap.Converter;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.nibor.autolink.LinkExtractor;
import org.nibor.autolink.LinkSpan;
import org.nibor.autolink.LinkType;
import org.nibor.autolink.Span;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.HtmlUtils;
import org.thymeleaf.util.StringUtils;

import javax.xml.bind.JAXBElement;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.wallscope.pronombackend.utils.RDFUtil.labelMap;
import static com.wallscope.pronombackend.utils.RDFUtil.safelyGetUriOrNull;

public class TemplateUtils {
    Logger logger = LoggerFactory.getLogger(TemplateUtils.class);

    private final String mdDir;
    private static TemplateUtils staticInstance = null;

    public static TemplateUtils getInstance() {
        if (staticInstance == null) {
            staticInstance = new TemplateUtils();
        }
        return staticInstance;
    }

    public TemplateUtils() {
        this.mdDir = ApplicationConfig.MARKDOWN_DIR;
        logger.info("loading markdown directory: " + mdDir);
    }

    public String getLabel(RDFNode n) {
        return getLabel(n, null);
    }

    public String getLabel(RDFNode n, Map<String, String> extra) {
        if (n == null) return "";
        if (n.isLiteral()) return n.asLiteral().getString();
        return getLabel(safelyGetUriOrNull(n), extra);
    }

    public String getLabel(Resource uri) {
        return getLabel(safelyGetUriOrNull(uri), null);
    }

    public String getLabel(Resource uri, Map<String, String> extra) {
        return getLabel(safelyGetUriOrNull(uri), extra);
    }

    public String getLabel(String uri) {
        return getLabel(uri, null);
    }

    public String getLabel(String uri, Map<String, String> extra) {
        if (uri == null) return "";
        String label = labelMap.getOrDefault(uri, null);
        if (extra != null) {
            label = extra.getOrDefault(uri, null);
        }
        if (label == null) {
            logger.trace("LABEL MAP: No label for URI: " + uri);
            return "";
        }
        return label;
    }

    public <T> String getString(T obj) {
        return obj != null && !obj.toString().isBlank() ? obj.toString() : "";
    }

    public String md(String region) {
        try {
            File f = new File(mdDir, region + ".md");
            return Processor.process(f);
        } catch (IOException e) {
            logger.debug("MD LOADER: No content for region: " + region);
            return "";
        }
    }

    public String mdString(String md) {
        return Processor.process(md);
    }

    public String parseDate(Instant ins) {
        if (ins == null) return "";
        return DateTimeFormatter.ofPattern("dd MMM yyyy").withLocale(Locale.UK).withZone(ZoneId.systemDefault()).format(ins);
    }

    public String raw(String region) {
        try {
            File f = new File(mdDir, region + ".md");
            return Files.readString(f.toPath(), StandardCharsets.UTF_8);
        } catch (NoSuchFileException e) {
            logger.info("RAW LOADER: No template for region: " + region);
            return "";
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    public String humaniseRegion(String region) {
        // regions names are the following format: page_region-name
        // so by splitting on "_" and replacing "-" with " " and then joining again on ": "
        // we turn it into: "page: region name"
        // the last step capitalises the page name: "Page: region name"
        try {
            String name = Arrays.stream(region.split("_"))
                    .map(s -> s.replaceAll("-", " "))
                    .collect(Collectors.joining(": "));
            return StringUtils.capitalize(name);
        } catch (Exception e) {
            logger.info("HUMANISER: Failed to transform region name: " + region);
            return "";
        }
    }

    public String autolinker(String input) {
        LinkExtractor linkExtractor = LinkExtractor.builder()
                .linkTypes(EnumSet.of(LinkType.URL, LinkType.WWW)) // limit to URLs
                .build();
        Iterable<Span> spans = linkExtractor.extractSpans(input);

        StringBuilder sb = new StringBuilder();
        for (Span span : spans) {
            String text = input.substring(span.getBeginIndex(), span.getEndIndex());
            if (span instanceof LinkSpan) {
                // span is a URL
                sb.append("<a class=\"autolink\" target=\"_blank\" href=\"");

                // The replace here makes PRONOM/id/ links relative
                sb.append(HtmlUtils.htmlEscape(text).replace(RDFUtil.PRONOM.uri, "/"));
                sb.append("\">");
                sb.append(HtmlUtils.htmlEscape(text.replace(RDFUtil.PRONOM.id, "")));
                sb.append("</a>");
            } else {
                // span is plain text before/after link
                sb.append(HtmlUtils.htmlEscape(text));
            }
        }
        return sb.toString();
    }

    // This function is used to customise the escaping for the container-signature output.
    // This is used because by default quote characters ([",']) are escaped but in the PRONOM
    // handwritten container signature files they are not escaped.
    // This should not affect the semantics of the file as quotes are allowed in XML fields
    public String customEscape(String raw) {
        if (raw == null) return null;
        return raw.replaceAll("&", "&amp;") // replace & characters
                .replaceAll("<", "&lt;")   // replace < characters
                .replaceAll(">", "&gt;");  // replace > characters
    }

    public String jaxb(JAXBElement<?> elem) {
        String conv = Converter.jaxbObjectToXML(elem);
        if (conv == null) return "";
        return reverseEscape(conv.replace("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n", ""));
    }

    // The JAXB converter will automatically escape single quotes ' as &apos; and double quotes " as &quot;
    // This funciton reverts that for compatibility with the previously manually edited container signature file.
    public String reverseEscape(String escaped) {
        return escaped.replaceAll("&apos;", "'")
                .replaceAll("&quot;", "\"");
    }


    // Custom Markdown processing: This block includes functions for parsing markdown for regions that require custom parsing

    // FAQ page:
    // Requirement: Create a hierarchy of Categories and category items.
    // Categories have titles.
    // Category items also have titles (question in the FAQ) and the rest of the text is the answer to the question
    // Example:
    /*
    ## Category 1 Title
    ### Cat 1 Item 1 Title
    Cat 1 Item 1 text which can be infinitely long.
    It can also have line breaks
    The only things that are not allowed here are headers 1 ('#') and 2 ('##') which will cause unexpected output if present at the start of a line

    ### Cat 1 Item 2 Title
    Some text for the 2nd item of category 1

    Random line breaks may occur and will be treated as they are normally by the markdown processor

    ## Category 2 Title
    ### Cat 2 Item 1 Title
    Some text for cat 2 item 1

     */
    private static final Pattern catRegex = Pattern.compile("^## ", Pattern.MULTILINE);
    private static final Pattern itemRegex = Pattern.compile("^### ", Pattern.MULTILINE);

    public List<MarkdownHelpers.FAQCategory> parseFAQ(String region) {
        String md = this.raw(region);
        List<MarkdownHelpers.FAQCategory> FAQs = new ArrayList<>();
        // break into category blocks
        Arrays.stream(catRegex.split(md)).forEach(c -> {
            int firstBreak = c.indexOf("\n");
            if (firstBreak < 0) return;
            String catTitle = c.substring(0, firstBreak).trim();
            List<MarkdownHelpers.FAQCategory.FAQItem> items = new ArrayList<>();
            Arrays.stream(itemRegex.split(c.substring(firstBreak + 1))).forEach(it -> {
                int itFirstBreak = it.indexOf("\n");
                if (itFirstBreak < 0) return;
                String itemTitle = it.substring(0, itFirstBreak).trim();
                String text = it.substring(itFirstBreak + 1);
                items.add(new MarkdownHelpers.FAQCategory.FAQItem(itemTitle, text));
            });
            FAQs.add(new MarkdownHelpers.FAQCategory(catTitle, items));
        });
        return FAQs;
    }

    // Submission page steps section:
    // Requirement: Create a list of numbered steps that contain a title, an explanation and an example
    // Example:
    /*
    # This is the title
    This is the first step which explains how people can better help the PRONOM team with their submissions

    It can include all markdown modifiers such as **bold** and _italic_

    ## headers can also be used
    ### as long as they are level 2+ since the first level header ('#') is reserved for titles.

    > This goes in the example section of the numbered step

    ---
    This is the second step and **all** modifiers can _be_ used, even [links](#)

    ```
    And fenced code blocks
    ```

    The marker for the example is a blockquote character at the start of a line (>)
    The separator of numbered steps is the horizontal divider at the start of a line, 3 or more hyphen characters (---)

    > This is an example, which can be
    multi-line

    And include line breaks.
    ---
    # Third step title

    some text

    > example
     */
    private static final Pattern horizontalDividerRegex = Pattern.compile("^---+", Pattern.MULTILINE);
    private static final Pattern stepTitleRegex = Pattern.compile("^#(.*)", Pattern.MULTILINE);
    private static final Pattern exampleRegex = Pattern.compile("^> ", Pattern.MULTILINE);

    public List<MarkdownHelpers.NumberedStep> parseSubmissionSteps(String region) {
        String md = this.raw(region);
        List<MarkdownHelpers.NumberedStep> steps = new ArrayList<>();
        // break into category blocks
        Arrays.stream(horizontalDividerRegex.split(md)).forEach(s -> {
            String[] parts = exampleRegex.split(s);
            String text = parts[0];
            String example = "";
            if (parts.length > 1) {
                example = parts[1];
            }
            Matcher titleMatcher = stepTitleRegex.matcher(text);
            String title = "";
            if (titleMatcher.find()) {
                title = titleMatcher.group(1);
                text = text.substring(titleMatcher.end(1));
            }
            steps.add(new MarkdownHelpers.NumberedStep(title.trim(), text, example));
        });
        return steps;
    }

    // About page team section:
    // Requirement: Create a list of numbered steps that contain a title, an explanation and an example
    // Example:
    /*
    [This is the image alt](https://placekitten.com/200/110)
    Person Name
    Person Title
    person.email@nationalarchives.com

    Text about the person. This can be infinitely long and use *all* __mardown__ allowed markers.

    ---
    [A photo of Jane Doe](https://placekitten.com/400/220)
    Jane Doe
    Executive Assistant of Business
    jane.doe@nationalarchives.com

    Jane Doe started as PRONOM's executive assistant of business in... blah blah blah...
    */

    // Matches a markdown URL in the format [text goes here](http://some.url/goes/here)
    // The first part is taken as the alt of the image
    // The second part is taken as the source of the image
    private static final Pattern imgSrcAltRegex = Pattern.compile("\\[([^\\]]*)\\]\\(([^)]*)\\)");

    public List<MarkdownHelpers.TeamMember> parseTeam(String region) {
        String md = this.raw(region);
        List<MarkdownHelpers.TeamMember> members = new ArrayList<>();
        // break into different team members
        Arrays.stream(horizontalDividerRegex.split(md)).forEach(m -> {
            String[] lines = m.split("\n");
            // remove any possible empty lines at the start of the team member markdown
            // The loop breaks as soon as we find the first line that is not blank
            // Leaving the rest of the lines in the noBlanks list.
            List<String> noBlanks = Arrays.stream(lines).sequential().dropWhile(String::isBlank).collect(Collectors.toList());
            // If there aren't at least 4 lines in the noBlanks list there aren't enough lines to fill the different fields
            // In this case we return to ignore this section.
            if (noBlanks.size() < 4) return;
            String img = noBlanks.get(0);
            Matcher imgMatcher = imgSrcAltRegex.matcher(img);
            String imgSource = null;
            String imgAlt = null;
            if (imgMatcher.find()) {
                imgAlt = imgMatcher.group(1);
                imgSource = imgMatcher.group(2);
            }
            String name = noBlanks.get(1);
            String title = noBlanks.get(2);
            String email = noBlanks.get(3);
            String description = String.join("\n", noBlanks.subList(4, noBlanks.size()));

            members.add(new MarkdownHelpers.TeamMember(imgAlt, imgSource, name, title, email, description));
        });
        return members;
    }
}
