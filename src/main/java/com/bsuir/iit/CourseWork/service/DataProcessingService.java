package com.bsuir.iit.CourseWork.service;

import com.bsuir.iit.CourseWork.model.Yogurt;
import com.univocity.parsers.common.processor.RowListProcessor;
import com.univocity.parsers.tsv.TsvParser;
import com.univocity.parsers.tsv.TsvParserSettings;
import com.univocity.parsers.tsv.TsvWriter;
import com.univocity.parsers.tsv.TsvWriterSettings;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.toList;

@Service
public class DataProcessingService {

    private static final String DIR = "src/main/resources/%s";

    @SneakyThrows
    public List<Yogurt> read(final MultipartFile file) {
        var settings = new TsvParserSettings();
        settings.setLineSeparatorDetectionEnabled(true);

        var rowProcessor = new RowListProcessor();

        settings.setProcessor(rowProcessor);
        settings.setHeaderExtractionEnabled(true);

        var parser = new TsvParser(settings);
        parser.parseAll(new File(Objects.requireNonNull(file.getOriginalFilename()))); // TODO

        var rows = rowProcessor.getRows();

        return prepareRowsForRead(rows);
    }

    public void write(final String filename, final List<Yogurt> instances) {
        var writer = new TsvWriter(new File(String.format(DIR, filename)),
                new TsvWriterSettings());
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

}
