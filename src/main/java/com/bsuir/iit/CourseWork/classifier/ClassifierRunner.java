package com.bsuir.iit.CourseWork.classifier;

import com.bsuir.iit.CourseWork.model.Yogurt;
import com.bsuir.iit.CourseWork.model.enums.Quality;
import com.bsuir.iit.CourseWork.service.DataGenerator;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.atomic.AtomicInteger;

@Log4j2
@Component
@AllArgsConstructor
public class ClassifierRunner {

    private final ClassifierTrainer classifierTrainer;
    private final DataGenerator dataGenerator;

    @PostConstruct
    public void init() {
        var trainingData = dataGenerator.generateTrainingInstance(250000);

        var sentimentPositiveCount = new AtomicInteger();
        var sentimentNegativeCount = new AtomicInteger();
        var addedNum = new AtomicInteger();

        trainingData.forEach((key, value) -> {
            if (value.equals(Quality.GOOD)) {
                sentimentPositiveCount.getAndIncrement();
                addedNum.getAndIncrement();
            } else if (value.equals(Quality.BAD)) {
                sentimentNegativeCount.getAndIncrement();
                addedNum.getAndIncrement();
            }
            classifierTrainer.addTrainingInstance(value, key);
        });

        log.info("Added {} instances of which {} positive instances and {} negative instances",
                addedNum, sentimentPositiveCount, sentimentNegativeCount);

        classifierTrainer.trainModel();
        classifierTrainer.saveModel();

        log.info("Testing model");
        classifierTrainer.testModel();
    }

    public Quality classify(final Yogurt data) {
        return classifierTrainer.classify(data);
    }

}
