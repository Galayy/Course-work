package com.bsuir.iit.CourseWork.model;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
public class Yogurt {

    // Must be 0.8+ for good yogurt
    private Double uniformity;

    // Must be 0.1-10% for good yogurt
    private Double massFatFraction;

    // Must be 2.8+% for good yogurt
    private Double massProteinFraction;

    // False for good yogurt
    private Boolean hasMold;

}
