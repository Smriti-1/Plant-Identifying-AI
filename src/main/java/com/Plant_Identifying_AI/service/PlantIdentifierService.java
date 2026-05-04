package com.Plant_Identifying_AI.service;

import com.Plant_Identifying_AI.model.ChatMessage;
import com.Plant_Identifying_AI.model.PlantInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.content.Media;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;

import java.util.List;

@RequiredArgsConstructor
@Service
public class PlantIdentifierService {

    private final ChatModel chatModel;
    private final ObjectMapper objectMapper;

    private static final String IDENTIFICATION_PROMPT = """
            You are PlantAI, an expert botanist and plant identification system.
            
            Analyze this image carefully:
            
            STEP 1: First determine if the image contains a plant.
            Plants include: trees, shrubs, flowers, grasses, ferns, mosses, succulents, cacti, herbs, vegetables, fruits, aquatic plants, vines, houseplants, or any botanical organism.
            
            STEP 2: If NOT a plant, respond with ONLY this JSON:
            {"isPlant": false, "reason": "brief explanation of what is actually in the image"}
            
            STEP 3: If it IS a plant, respond with ONLY this JSON (no markdown, no backticks):
            {
              "isPlant": true,
              "commonName": "Common name",
              "scientificName": "Scientific binomial name",
              "family": "Plant family",
              "kingdom": "Plantae",
              "nativeRegion": "Original native region",
              "countriesFound": ["Country1", "Country2", "Country3"],
              "habitat": "Natural habitat description",
              "climateType": "Tropical/Temperate/Mediterranean/etc",
              "description": "Comprehensive description of this plant in 3-4 sentences",
              "appearance": "Detailed visual appearance description",
              "leafCharacteristics": ["shape", "size", "color", "texture"],
              "flowerCharacteristics": ["color", "size", "fragrance", "season"],
              "fruitCharacteristics": ["type", "color", "edibility"],
              "plantingGuide": "Step-by-step planting guide",
              "soilType": "Ideal soil conditions",
              "sunlightRequirement": "Full sun/Partial shade/etc",
              "wateringSchedule": "How often to water",
              "wateringMethod": "Best method to water this plant",
              "fertilizationTips": "Fertilization recommendations",
              "pruningTips": "When and how to prune",
              "propagationMethods": "How to propagate this plant",
              "lifespan": "Annual/Biennial/Perennial with years",
              "growthRate": "Slow/Moderate/Fast with details",
              "seasonalBehavior": "Behavior across seasons",
              "floweringSeason": "When it flowers",
              "commonDiseases": "Common diseases to watch for",
              "commonPests": "Common pests affecting this plant",
              "careDifficulty": "Easy/Moderate/Expert",
              "careTips": ["Tip 1", "Tip 2", "Tip 3", "Tip 4", "Tip 5"],
              "similarSpecies": ["Species 1 (Scientific name)", "Species 2", "Species 3"],
              "colorVarieties": ["Color variety 1", "Color variety 2", "Color variety 3"],
              "popularCultivars": ["Cultivar 1", "Cultivar 2", "Cultivar 3"],
              "uses": ["Use 1", "Use 2", "Use 3", "Use 4"],
              "edibility": "Edible/Non-edible/Toxic with details",
              "medicinalProperties": "Known medicinal uses if any",
              "toxicToAnimals": false,
              "toxicToHumans": false,
              "funFacts": ["Interesting fact 1", "Interesting fact 2", "Interesting fact 3"],
              "conservationStatus": "Least Concern/Vulnerable/Endangered/etc",
              "confidencePercent": 95
            }
            
            Respond with ONLY valid JSON, no other text.
            """;

    public record IdentificationResult(boolean isPlant, String reason, PlantInfo plantInfo) {
    }

    public IdentificationResult identifyPlant(byte[] imageBytes, String contentType) {

        try {
            MimeType imageMineType = MimeTypeUtils.parseMimeType(contentType);
            Media imageMedia = new Media(imageMineType, new ByteArrayResource(imageBytes));

            UserMessage message = UserMessage.builder().text(IDENTIFICATION_PROMPT).media(imageMedia).build();

            Prompt prompt = new Prompt(List.of(message));

            String response = chatModel.call(prompt).getResult().getOutput().getText();

            String cleanJson = response.trim().replaceAll("```json\\s*", "").replaceAll("```\\s*", "").trim();

            var jsonNode = objectMapper.readTree(cleanJson);

            Boolean isPlant = jsonNode.get("isPlant").asBoolean();
            if (!isPlant) {
                String reason = jsonNode.has("reason") ? jsonNode.get("reason").asText() : "Not a plant image";
                return new IdentificationResult(false, reason, null);
            }

            PlantInfo plantInfo = objectMapper.treeToValue(jsonNode, PlantInfo.class);
            return new IdentificationResult(true, null, plantInfo);

        } catch (Exception e) {
            throw new RuntimeException("Failed to analyze image: " + e.getMessage(), e);
        }
    }

    public String askQuestion(ChatMessage chatMessage, PlantInfo plantInfo, List<ChatMessage> history) {

        String historyText = history.stream()
                .map(m -> m.getRole() + ": " + m.getContent())
                .reduce("", (a, b) -> a + "\n" + b);

        String plantContext = """
                Plant: %s
                Scientific Name: %s
                Family: %s
                Description: %s
                Care Tips: %s
                """
                .formatted(
                        plantInfo.getCommonName(),
                        plantInfo.getScientificName(),
                        plantInfo.getFamily(),
                        plantInfo.getDescription(),
                        plantInfo.getCareTips()
                );

        String finalPrompt = """
                You are a plant expert.
                %s
                Conversation:
                %s
                User Question:
                %s
                Answer clearly and practically.
                """.formatted(plantContext, historyText, chatMessage.getContent());

        UserMessage message = new UserMessage(finalPrompt);
        Prompt prompt = new Prompt(List.of(message), OpenAiChatOptions.builder().maxTokens(100).build());

        return chatModel.call(prompt).getResult().getOutput().getText();
    }
}
