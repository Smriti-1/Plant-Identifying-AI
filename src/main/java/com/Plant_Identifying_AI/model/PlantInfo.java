package com.Plant_Identifying_AI.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlantInfo implements Serializable {

    private String commonName;
    private String scientificName;
    private String family;
    private String kingdom;

    // Origin & Habitat
    private String nativeRegion;
    private List<String> countriesFound;
    private String habitat;
    private String climateType;

    // Description
    private String description;
    private String appearance;
    private List<String> leafCharacteristics;
    private List<String> flowerCharacteristics;
    private List<String> fruitCharacteristics;

    // Growing Info
    private String plantingGuide;
    private String soilType;
    private String sunlightRequirement;
    private String wateringSchedule;
    private String wateringMethod;
    private String fertilizationTips;
    private String pruningTips;
    private String propagationMethods;

    // Life Cycle
    private String lifespan;
    private String growthRate;
    private String seasonalBehavior;
    private String floweringSeason;

    // Health & Care
    private String commonDiseases;
    private String commonPests;
    private String careDifficulty;
    private List<String> careTips;

    // Varieties
    private List<String> similarSpecies;
    private List<String> colorVarieties;
    private List<String> popularCultivars;

    // Usefulness
    private List<String> uses;
    private String edibility;
    private String medicinalProperties;
    private boolean toxicToAnimals;
    private boolean toxicToHumans;

    // Fun Facts
    private List<String> funFacts;
    private String conservationStatus;

    // Confidence
    private int confidencePercent;

}
