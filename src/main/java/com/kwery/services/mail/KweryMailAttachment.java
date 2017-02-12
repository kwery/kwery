package com.kwery.services.mail;

import java.io.File;

public interface KweryMailAttachment {
     File getFile();
     void setFile(File file);
     String getName();
     void setName(String name);
     String getDescription();
     void setDescription(String description);
}
