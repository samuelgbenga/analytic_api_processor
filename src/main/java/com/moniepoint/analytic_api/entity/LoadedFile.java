package com.moniepoint.analytic_api.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "loaded_files")
public class LoadedFile {

    @Id
    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "loaded_at", nullable = false)
    private LocalDateTime loadedAt;

    @Column(name = "record_count", nullable = false)
    private long recordCount;

    public LoadedFile() {}

    public LoadedFile(String fileName, LocalDateTime loadedAt, long recordCount) {
        this.fileName = fileName;
        this.loadedAt = loadedAt;
        this.recordCount = recordCount;
    }

    public String getFileName() { return fileName; }
    public LocalDateTime getLoadedAt() { return loadedAt; }
    public long getRecordCount() { return recordCount; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public void setLoadedAt(LocalDateTime loadedAt) { this.loadedAt = loadedAt; }
    public void setRecordCount(long recordCount) { this.recordCount = recordCount; }
}