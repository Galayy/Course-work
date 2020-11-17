package com.bsuir.iit.CourseWork.classifier;

import com.bsuir.iit.CourseWork.model.Yogurt;
import com.bsuir.iit.CourseWork.model.enums.Quality;
import lombok.Data;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import weka.classifiers.bayes.NaiveBayesMultinomialText;
import weka.classifiers.evaluation.Evaluation;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static weka.core.SerializationHelper.write;

@Data
@Component
public class ClassifierTrainer {

    private static final String MODEL_FILE = "src/main/resources/model.model";

    private NaiveBayesMultinomialText classifier;
    private Instances dataRaw;

    @Autowired
    public ClassifierTrainer() {
        classifier = new NaiveBayesMultinomialText();

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
        System.out.println(strSummary);
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

        if (distribution[0] != distribution[1])
            return Quality.values()[(int) prediction];
        else
            return Quality.UNDEFINED;
    }

    private String prepareContent(final Yogurt yogurt) {
        return String.format("%s %s %s %s",
                yogurt.getUniformity(), yogurt.getMassFatFraction(),
                yogurt.getMassProteinFraction(), yogurt.getHasMold());
    }

}
