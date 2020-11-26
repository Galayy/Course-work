package com.bsuir.iit.CourseWork.api;

import com.bsuir.iit.CourseWork.classifier.ClassifierRunner;
import com.bsuir.iit.CourseWork.service.DataGenerator;
import com.bsuir.iit.CourseWork.service.DataProcessingService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Log4j2
@RestController
@AllArgsConstructor
public class ClassifierApi {

    private final ClassifierRunner classifierRunner;
    private final DataGenerator dataGenerator;
    private final DataProcessingService dataProcessingService;

    @PostMapping("/classify")
    public String classify(@RequestPart final MultipartFile file) {
        var data = dataProcessingService.read(file);
        var quality = classifierRunner.classify(data.get(0));
        return quality.name();
    }

    @PutMapping("/write")
    public void write(@RequestParam("number") final Integer number) {
        var data = dataGenerator.generateTestingInstance(number);
        dataProcessingService.write("test.tsv", data);
    }
}
