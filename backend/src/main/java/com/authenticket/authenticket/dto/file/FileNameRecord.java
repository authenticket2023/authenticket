package com.authenticket.authenticket.dto.file;

import org.springframework.core.io.InputStreamResource;

public record FileNameRecord(String name, InputStreamResource file) implements Comparable<FileNameRecord> {
    @Override
    public int compareTo(FileNameRecord o) {
        return o.name.compareTo(name);
    }
}
