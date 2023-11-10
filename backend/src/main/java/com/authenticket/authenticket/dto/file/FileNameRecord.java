package com.authenticket.authenticket.dto.file;

import org.springframework.core.io.InputStreamResource;

/**
 * A data transfer object (DTO) representing a file record with a name and its content.
 * This class is used for comparing and working with files in your application.
 */
public record FileNameRecord(
        /**
         * The name of the file.
         */
        String name,

        /**
         * The content of the file as an InputStreamResource.
         */
        InputStreamResource file
) implements Comparable<FileNameRecord> {

    /**
     * Compares this FileNameRecord with another based on their names in descending order.
     *
     * @param o The FileNameRecord to compare with.
     * @return A negative integer, zero, or a positive integer if this name is less than, equal to, or greater than
     *         the name of the specified FileNameRecord, respectively.
     */
    @Override
    public int compareTo(FileNameRecord o) {
        return o.name.compareTo(name);
    }
}
