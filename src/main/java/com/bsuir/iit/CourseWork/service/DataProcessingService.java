package com.bsuir.iit.CourseWork.service;

import com.bsuir.iit.CourseWork.model.Yogurt;
import com.univocity.parsers.common.processor.RowListProcessor;
import com.univocity.parsers.tsv.TsvParser;
import com.univocity.parsers.tsv.TsvParserSettings;
import com.univocity.parsers.tsv.TsvWriter;
import com.univocity.parsers.tsv.TsvWriterSettings;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;

import static java.nio.file.Files.newOutputStream;
import static java.util.stream.Collectors.toList;

@Log4j2
@Service
public class DataProcessingService {

    private static final String DIR = "src/main/resources";

    @SneakyThrows
    public List<Yogurt> read(final MultipartFile file) {
        var preparedFile = prepareFile(file);
        var settings = new TsvParserSettings();
        settings.setLineSeparatorDetectionEnabled(true);

        var rowProcessor = new RowListProcessor();

        settings.setProcessor(rowProcessor);
        settings.setHeaderExtractionEnabled(true);

        var parser = new TsvParser(settings);
        parser.parseAll(preparedFile);

        var rows = rowProcessor.getRows();

        return prepareRowsForRead(rows);
    }

    // do not use generated files for testing!!! It is some weird error when parsing it
    public void write(final String filename, final List<Yogurt> instances) {
        var writer = new TsvWriter(Paths.get(DIR, filename).toFile(), new TsvWriterSettings());
        writer.writeHeaders("Uniformity", "Mass fat fraction", "Mass protein fraction", "Has mold");

        var preparedRows = prepareRows(instances);
        writer.writeRowsAndClose(preparedRows);
    }

    private List<Yogurt> prepareRowsForRead(final List<String[]> rows) {
        return rows.stream().map(row -> Yogurt.builder()
                .uniformity(Double.valueOf(row[0]))
                .massFatFraction(Double.valueOf(row[1]))
                .massProteinFraction(Double.valueOf(row[2]))
                .hasMold(Boolean.valueOf(row[3]))
                .build()).collect(toList());
    }

    private Collection<Object[]> prepareRows(final List<Yogurt> instances) {
        return instances.stream().parallel().map(instance -> new String[]{
                instance.getUniformity().toString(),
                instance.getMassFatFraction().toString(),
                instance.getMassProteinFraction().toString(),
                instance.getHasMold().toString()
        }).collect(toList());
    }

    private File prepareFile(final MultipartFile file) {
        var filename = file.getOriginalFilename();
        var filepath = Paths.get(DIR, filename);
        try (var os = newOutputStream(filepath)) {
            os.write(file.getBytes());
        } catch (Exception e) {
            log.error("Exception during file MultipartFile saving process. Message: " + e.getMessage());
        }
        return filepath.toFile();
    }

}
