package com.bsuir.iit.CourseWork.api;

import com.bsuir.iit.CourseWork.classifier.ClassifierTrainer;
import com.bsuir.iit.CourseWork.service.DataGenerator;
import com.bsuir.iit.CourseWork.service.DataProcessingService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@AllArgsConstructor
public class ClassifierApi {

    private final ClassifierTrainer classifierTrainer;
    private final DataGenerator dataGenerator;
    private final DataProcessingService dataProcessingService;

    @PostMapping("/classify/{:id}")
    public String classify(@PathVariable final Integer id, @RequestPart final MultipartFile file) {
        var data = dataProcessingService.read(file);
        var quality = classifierTrainer.classify(data.get(0));
        return quality.name();
    } // TODO: type of id?

    @PutMapping("/write")
    public void write(@RequestParam("number") final Integer number) {
        var data = dataGenerator.generateTestingInstance(number);
        dataProcessingService.write("test.tsv", data);
    }
}
