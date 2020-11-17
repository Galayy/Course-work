package com.bsuir.iit.CourseWork.service;

import com.bsuir.iit.CourseWork.model.Yogurt;
import com.bsuir.iit.CourseWork.model.enums.Quality;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Data
@Service
public class DataGenerator {

    private static final double UNIFORMITY_THRESHOLD = 0.8;

    private static final double MIN_MASS_FAT_FRACTION = 0.1;
    private static final double MAX_MASS_FAT_FRACTION = 10.0;

    private static final double MIN_MASS_PROTEIN_FRACTION = 2.8;


    public Map<Yogurt, Quality> generateTrainingInstance(final int size) {
        final Map<Yogurt, Quality> instances = new HashMap<>();
        for (var i = 0; i < size; i++) {
            var uniformity = generateValue(0.75, 1);

            var massFatFraction = generateValue(0, 11);
            var massProteinFraction = generateValue(2, 40);

            var hasMold = ThreadLocalRandom.current().nextBoolean();

            var quality = isGoodUniformity(uniformity) && isGoodMassFatFraction(massFatFraction)
                    && isGoodMassProteinFraction(massProteinFraction) && !hasMold ? Quality.GOOD : Quality.BAD;

            instances.put(
                    Yogurt.builder()
                            .uniformity(uniformity)
                            .massFatFraction(massFatFraction)
                            .massProteinFraction(massProteinFraction)
                            .hasMold(hasMold)
                            .build(),
                    quality);
        }
        return instances;
    }

    public List<Yogurt> generateTestingInstance(final int size) {
        var instances = new ArrayList<Yogurt>();
        for (var i = 0; i < size; i++) {
            var uniformity = generateValue(0.75, 1);

            var massFatFraction = generateValue(0, 11);
            var massProteinFraction = generateValue(2, 40);

            var hasMold = ThreadLocalRandom.current().nextBoolean();

            instances.add(
                    Yogurt.builder()
                            .uniformity(uniformity)
                            .massFatFraction(massFatFraction)
                            .massProteinFraction(massProteinFraction)
                            .hasMold(hasMold)
                            .build());
        }
        return instances;
    }

    private boolean isGoodUniformity(double value) {
        return value >= UNIFORMITY_THRESHOLD;
    }

    private boolean isGoodMassFatFraction(double value) {
        return value >= MIN_MASS_FAT_FRACTION && value <= MAX_MASS_FAT_FRACTION;
    }

    private boolean isGoodMassProteinFraction(double value) {
        return value >= MIN_MASS_PROTEIN_FRACTION;
    }

    private Double generateValue(double min, double max) {
        return ThreadLocalRandom.current().nextDouble(min, max);
    }

}

