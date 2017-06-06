package com.kwery.conf;

import com.google.common.io.Files;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class TemplateDirectory {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    protected File dir;

    //Only Guice should be able to instantiate this class
    TemplateDirectory(File dir) {
        this.dir = dir;
    }

    public File getTemplate(String name) {
        return new File(dir, name);
    }

    public boolean delete(String name) {
       return new File(dir, name).delete();
    }

    public File save(File templateFile) {
        String name = UUID.randomUUID().toString();
        try {
            File file = new File(dir, name);
            if (!file.createNewFile()) {
                logger.error("Could not create empty template file {}", file);
            }
            Files.copy(templateFile, file);
            return file;
        } catch (IOException e) {
            logger.error("Exception while creating template file with name {}", name, e);
        }
        return null;
    }
}
