package com.bsuir.iit.CourseWork.classifier;

import com.bsuir.iit.CourseWork.model.Yogurt;
import com.bsuir.iit.CourseWork.model.enums.Quality;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import weka.classifiers.evaluation.Evaluation;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static weka.core.SerializationHelper.write;

@Data
@Log4j2
@Component
public class ClassifierTrainer {

    private static final String MODEL_FILE = "src/main/resources/model.model";

    private MultilayerPerceptron classifier;
    private Instances dataRaw;

    @Autowired
    public ClassifierTrainer() {
        classifier = new MultilayerPerceptron();
        classifier.setLearningRate(0.1);
        classifier.setMomentum(0.2);
        classifier.setTrainingTime(2000);
        classifier.setHiddenLayers("3");

        var classVal = List.of(Quality.values()).stream().map(Enum::name).collect(toList());

        var attributes = new ArrayList<>(List.of(
                new Attribute("content", (ArrayList<String>) null),
                new Attribute("@@class@@", classVal)));

        dataRaw = new Instances("TrainingInstances", attributes, 10);
    }

    public void addTrainingInstance(final Quality quality, final Yogurt yogurt) {
        var instanceValue = new double[dataRaw.numAttributes()];
        instanceValue[0] = dataRaw.attribute(0).addStringValue(prepareContent(yogurt));
        instanceValue[1] = quality.ordinal();

        dataRaw.add(new DenseInstance(1.0, instanceValue));
        dataRaw.setClassIndex(1);
    }

    @SneakyThrows
    public void trainModel() {
        classifier.buildClassifier(dataRaw);
    }

    @SneakyThrows
    public void testModel() {
        Evaluation eTest = new Evaluation(dataRaw);
        eTest.evaluateModel(classifier, dataRaw);
        String strSummary = eTest.toSummaryString();
        log.info(strSummary);
    }

    @SneakyThrows
    public void saveModel() {
        write(MODEL_FILE, classifier);
    }

    @SneakyThrows
    public Quality classify(final Yogurt yogurt) {
        var instanceValue = new double[dataRaw.numAttributes()];
        instanceValue[0] = dataRaw.attribute(0).addStringValue(prepareContent(yogurt));

        var toClassify = new DenseInstance(1.0, instanceValue);
        dataRaw.setClassIndex(1);
        toClassify.setDataset(dataRaw);

        var prediction = this.classifier.classifyInstance(toClassify);
        var distribution = this.classifier.distributionForInstance(toClassify);

        var quality = distribution[0] != distribution[1] ?
                Quality.values()[(int) prediction] : Quality.UNDEFINED;
        log.info("Product {} is {}", yogurt, quality);

        return quality;
    }

    private String prepareContent(final Yogurt yogurt) {
        return String.format("%s %s %s %s",
                yogurt.getUniformity(), yogurt.getMassFatFraction(),
                yogurt.getMassProteinFraction(), yogurt.getHasMold());
    }

}
