package com.kwery.utils;

import java.io.File;

public interface KweryDirectoryFactory {
    KweryDirectory create(File baseDirectory);
}
