package com.wallscope.pronombackend;

import com.wallscope.pronombackend.dao.FileFormatDAO;
import com.wallscope.pronombackend.model.MarkdownHelpers;
import com.wallscope.pronombackend.utils.RDFUtil;
import com.wallscope.pronombackend.utils.TemplateUtils;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryException;
import org.apache.jena.query.QueryFactory;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UtilTest {
    @Test
    void trimOptionalWorks() {
        System.out.println(FileFormatDAO.FILE_FORMAT_QUERY);
        String test = RDFUtil.trimOptionals(FileFormatDAO.FILE_FORMAT_QUERY);
        System.out.println(test);
        Query q = null;
        try {
            q = QueryFactory.create(test);
        } catch (QueryException ignored) {
        }
        assertNotEquals(FileFormatDAO.FILE_FORMAT_QUERY, test);
        assertFalse(test.contains("OPTIONAL"));
        assertNotNull(q);
    }

    @Test
    void parseFAQsWorks() {
        TemplateUtils t = new TemplateUtils();
        List<MarkdownHelpers.FAQCategory> result = t.parseFAQ(t.raw("doesn't_exist"));
        assertTrue(result.isEmpty());
        result = t.parseFAQ("faq");
        assertFalse(result.isEmpty());
        System.out.println(result);
    }

    @Test
    void parseSubmissionStepsWorks() {
        TemplateUtils t = new TemplateUtils();
        List<MarkdownHelpers.NumberedStep> result = t.parseSubmissionSteps("contribute_how-to-submit-steps");
        assertFalse(result.isEmpty());
        System.out.println(result);
    }

    @Test
    void parseTeamWorks(){
        TemplateUtils t = new TemplateUtils();
        List<MarkdownHelpers.TeamMember> result = t.parseTeam("about_pronom-team");
        assertFalse(result.isEmpty());
        System.out.println(result);
    }
}
