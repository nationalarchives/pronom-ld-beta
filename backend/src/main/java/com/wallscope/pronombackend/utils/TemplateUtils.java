package com.wallscope.pronombackend.utils;

import com.github.rjeschke.txtmark.Processor;

import java.io.File;
import java.io.IOException;

public class TemplateUtils {
    private final String mdDir;

    public TemplateUtils(String mdDir) {
        this.mdDir = mdDir;
    }

    public String md(String section) {
        try {
            File f = new File(mdDir, section + ".md");
            return Processor.process(f);
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }
}
