package com.kwery.services.mail;

import java.io.File;

public class KweryMailAttachmentImpl implements KweryMailAttachment {
    protected File file;
    protected String name;
    protected String description;

    @Override
    public File getFile() {
        return file;
    }

    @Override
    public void setFile(File fileName) {
        this.file = fileName;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }
}
