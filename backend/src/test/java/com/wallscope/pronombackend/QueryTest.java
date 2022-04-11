package com.wallscope.pronombackend;

import com.wallscope.pronombackend.dao.FileFormatDAO;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryException;
import org.apache.jena.query.QueryFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class QueryTest {
    @Test
    void FileFormatQueryIsValid() {
        String test = FileFormatDAO.FILE_FORMAT_QUERY;
        System.out.println(test);
        Query q = null;
        try {
            q = QueryFactory.create(test);
        } catch (QueryException ignored) {
        }
        assertNotNull(q);
    }
}
