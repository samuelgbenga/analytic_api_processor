package com.moniepoint.analytic_api.utils;

import com.moniepoint.analytic_api.entity.MerchantActivityRecord;
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
import java.util.UUID;

@Component
public class CsvLoader {

    private static final Logger log = LoggerFactory.getLogger(CsvLoader.class);

    private final MerchantActivityRecordRepository repository;

    public CsvLoader(MerchantActivityRecordRepository repository) {
        this.repository = repository;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void loadCsvsOnStartup() {
        log.info("Starting background CSV loading...");
        log.info("Working directory: {}", System.getProperty("user.dir"));

        File folder;
        try {
            folder = new ClassPathResource("data/").getFile();
        } catch (Exception e) {
            log.error("Could not locate data/ folder on classpath: {}", e.getMessage());
            return;
        }

        File[] files = folder.listFiles((dir, name) -> name.matches("activities_.*\\.csv"));
        if (files == null || files.length == 0) {
            log.warn("No CSV files found in data/");
            return;
        }

        log.info("Found {} CSV files", files.length);
        for (File file : files) {
            loadCsvAsync(file.getAbsolutePath());
        }
    }

    @Async
    public void loadCsvAsync(String path) {
        log.info("Loading CSV file asynchronously: {}", path);
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                try {
                    MerchantActivityRecord record = parseLine(line);
                    repository.save(record);
                } catch (Exception e) {
                    log.error("Skipping malformed line in {}: {} | Error: {}", path, line, e.getMessage());
                }
            }
            log.info("Finished loading CSV file: {}", path);
        } catch (Exception e) {
            log.error("Error loading CSV {}: {}", path, e.getMessage());
        }
    }

    private MerchantActivityRecord parseLine(String line) {
        String[] cols = line.split(",", -1);
        MerchantActivityRecord record = new MerchantActivityRecord();

        record.setEventId(UUID.fromString(cols[0].trim()));
        record.setMerchantId(cols[1].trim());

        if (!cols[2].isEmpty()) {
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