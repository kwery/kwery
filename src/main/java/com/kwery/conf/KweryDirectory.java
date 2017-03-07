package com.kwery.conf;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.io.Files;
import org.apache.commons.codec.Charsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.UUID;

public class KweryDirectory {
    protected Logger logger = LoggerFactory.getLogger(KweryDirectory.class);

    protected char[] hex = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    protected File root;

    //This is intentionally package private so that only the Guice module init code present in the same package instantiates this class
    KweryDirectory(File root) {
        this.root = root;
    }

    public void checkAndRepairDirectories() {
        for (char c0 : hex) {
            File file0 = new File(root, String.valueOf(c0));
            if (!file0.exists()) {
                logger.error("Directory {} has been deleted, recreating it", file0);
                try {
                    java.nio.file.Files.createDirectory(file0.toPath());
                } catch (IOException e) {
                    logger.error("Kwery exiting because report storage directory {} creation failed", file0);
                    System.exit(-1);
                }
            }
            for (char c1 : hex) {
                File file1 = new File(file0, String.valueOf(c1));
                if (!file1.exists()) {
                    logger.error("Directory {} has been deleted, recreating it", file1);
                    try {
                        java.nio.file.Files.createDirectory(file1.toPath());
                    } catch (IOException e) {
                        logger.error("Kwery exiting because report storage directory {} creation failed", file1);
                        System.exit(-1);
                    }
                }
                for (char c2 : hex) {
                    File file2 = new File(file1, String.valueOf(c2));
                    if (!file2.exists()) {
                        logger.error("Directory {} has been deleted, recreating it", file2);
                        try {
                            java.nio.file.Files.createDirectory(file2.toPath());
                        } catch (IOException e) {
                            logger.error("Kwery exiting because report storage directory {} creation failed", file2);
                            System.exit(-1);
                        }
                    }
                }
            }
        }
    }

    public File getDirectory(String fileName) {
        try {
            UUID.fromString(fileName);
            File level0 = new File(root, String.valueOf(fileName.charAt(0)));
            File level1 = new File(level0, String.valueOf(fileName.charAt(1)));
            return new File(level1, String.valueOf(fileName.charAt(2)));
        } catch (IllegalArgumentException e) {
            logger.error("Exception while parsing file name {} to UUID", fileName);
        }

        return null;
    }

    public File createFile() {
        String name = UUID.randomUUID().toString();
        File dir = getDirectory(name);
        try {
            File file = new File(dir, name);
            if (!file.createNewFile()) {
                logger.error("Could not create file {}", file);
            }
            return file;
        } catch (IOException e) {
            logger.error("Exception while creating file with name {}", name, e);
        }

        return null;
    }

    public File getFile(String fileName) {
        return new File(getDirectory(fileName), fileName);
    }

    @VisibleForTesting
    public String getContent(String fileName) {
        File file = getFile(fileName);
        try {
            return Files.toString(file, Charsets.UTF_8).trim(); //To remove the new line at the end
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws IOException {
        KweryDirectory kweryDirectory = new KweryDirectory(new File("/tmp/foo"));
        String fileName = UUID.randomUUID().toString();
        System.out.println("Getting file with name - " + fileName);
        System.out.println("File exists - " + kweryDirectory.getFile(fileName).exists());
        File newFile = kweryDirectory.createFile();
        System.out.println("Creating file with name - " + newFile);
        File file = kweryDirectory.getFile(newFile.getName());
        System.out.println("File with name exists - " + file.exists());

        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file, true)))) {
            pw.write("foo bar moo");
        };
    }
}
