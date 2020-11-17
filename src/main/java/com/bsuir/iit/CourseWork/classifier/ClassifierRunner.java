package com.bsuir.iit.CourseWork.classifier;

import com.bsuir.iit.CourseWork.model.enums.Quality;
import com.bsuir.iit.CourseWork.service.DataGenerator;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class ClassifierRunner {

    @PostConstruct
    public void init() {
        var generator = new DataGenerator();

        // Generating data for learning
        var trainingData = generator.generateTrainingInstance(500000);

        var trainer = new ClassifierTrainer();

        var sentimentPositiveCount = new AtomicInteger();
        var sentimentNegativeCount = new AtomicInteger();
        var addedNum = new AtomicInteger();

        System.out.println("Adding training instances");
        trainingData.forEach((key, value) -> {
            if (value.equals(Quality.GOOD)) {
                sentimentPositiveCount.getAndIncrement();
                addedNum.getAndIncrement();
            } else if (value.equals(Quality.BAD)) {
                sentimentNegativeCount.getAndIncrement();
                addedNum.getAndIncrement();
            }
            trainer.addTrainingInstance(value, key);
        });

        System.out.printf("Added %s instances%n", addedNum);
        System.out.printf("Of which %s positive instances and %s negative instances%n",
                sentimentPositiveCount, sentimentNegativeCount);

        System.out.println("Training and saving Model");
        trainer.trainModel();
        trainer.saveModel();

        System.out.println("Testing model");
        trainer.testModel();
    }

}
