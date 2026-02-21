package com.moniepoint.analytic_api.utils;

import com.moniepoint.analytic_api.entity.LoadedFile;
import com.moniepoint.analytic_api.entity.MerchantActivityRecord;
import com.moniepoint.analytic_api.repository.LoadedFileRepository;
import com.moniepoint.analytic_api.repository.MerchantActivityRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class CsvLoader {

    private static final Logger log = LoggerFactory.getLogger(CsvLoader.class);
    private static final int BATCH_SIZE = 500;

    private final MerchantActivityRecordRepository repository;
    private final LoadedFileRepository loadedFileRepository;

    public CsvLoader(MerchantActivityRecordRepository repository,
                     LoadedFileRepository loadedFileRepository) {
        this.repository = repository;
        this.loadedFileRepository = loadedFileRepository;
    }

//    @EventListener(ApplicationReadyEvent.class)
//    public void loadCsvsOnStartup() {
//        log.info("Starting background CSV loading...");
//
//        File folder;
//        try {
//            folder = new ClassPathResource("data/").getFile();
//        } catch (Exception e) {
//            log.error("Could not locate data/ folder on classpath: {}", e.getMessage());
//            return;
//        }
//
//        File[] files = folder.listFiles((dir, name) -> name.matches("activities_.*\\.csv"));
//        if (files == null || files.length == 0) {
//            log.warn("No CSV files found in data/");
//            return;
//        }
//
//        log.info("Found {} CSV files", files.length);
//        for (File file : files) {
//            if (loadedFileRepository.existsById(file.getName())) {
//                log.info("Skipping already loaded file: {}", file.getName());
//                continue;
//            }
//            loadCsvAsync(file.getAbsolutePath(), file.getName());
//        }
//    }

    @EventListener(ApplicationReadyEvent.class)
    public void loadCsvsOnStartup() {
        log.info("Starting background CSV loading...");

        String rootPath = System.getProperty("user.dir");
        File folder = new File(rootPath, "data");

        log.info("Looking for CSV files in: {}", folder.getAbsolutePath());

        if (!folder.exists() || !folder.isDirectory()) {
            log.error("data/ folder not found at: {}", folder.getAbsolutePath());
            return;
        }

        File[] files = folder.listFiles((dir, name) -> name.matches("activities_.*\\.csv"));
        if (files == null || files.length == 0) {
            log.warn("No CSV files found in data/");
            return;
        }

        log.info("Found {} CSV files", files.length);
        for (File file : files) {
            if (loadedFileRepository.existsById(file.getName())) {
                log.info("Skipping already loaded file: {}", file.getName());
                continue;
            }
            loadCsvAsync(file.getAbsolutePath(), file.getName());
        }
    }
    @Async
    public void loadCsvAsync(String path, String fileName) {
        log.info("Loading CSV file: {}", path);
        List<MerchantActivityRecord> batch = new ArrayList<>(BATCH_SIZE);
        long recordCount = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            boolean firstLine = true;

            while ((line = br.readLine()) != null) {
                if (firstLine) { firstLine = false; continue; }

                try {
                    batch.add(parseLine(line));
                    recordCount++;

                    if (batch.size() >= BATCH_SIZE) {
                        repository.saveAll(batch);
                        batch.clear();
                    }
                } catch (Exception e) {
                    log.error("Skipping malformed line in {}: {} | Error: {}", fileName, line, e.getMessage());
                }
            }

            // save any remaining records
            if (!batch.isEmpty()) {
                repository.saveAll(batch);
            }

            // mark file as loaded
            loadedFileRepository.save(new LoadedFile(fileName, LocalDateTime.now(), recordCount));
            log.info("Finished loading: {} | Total records: {}", fileName, recordCount);

        } catch (Exception e) {
            log.error("Error loading CSV {}: {}", fileName, e.getMessage());
            // do NOT mark file as loaded — it will retry on next restart
        }
    }

    private MerchantActivityRecord parseLine(String line) {
        String[] cols = line.split(",", -1);
        MerchantActivityRecord record = new MerchantActivityRecord();

        record.setEventId(UUID.fromString(cols[0].trim()));
        record.setMerchantId(cols[1].trim());

        if (!cols[2].trim().isEmpty()) {
            record.setEventTimestamp(LocalDateTime.parse(cols[2].trim(), DateTimeFormatter.ISO_DATE_TIME));
        }

        record.setProduct(cols[3].trim());
        record.setEventType(cols[4].trim());

        try {
            record.setAmount(new BigDecimal(cols[5].trim()));
        } catch (Exception e) {
            record.setAmount(BigDecimal.ZERO);
        }

        record.setStatus(cols[6].trim());
        record.setChannel(cols[7].trim());
        record.setRegion(cols[8].trim());
        record.setMerchantTier(cols[9].trim());

        return record;
    }
}