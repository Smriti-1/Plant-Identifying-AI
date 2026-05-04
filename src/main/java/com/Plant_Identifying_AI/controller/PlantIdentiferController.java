package com.Plant_Identifying_AI.controller;

import com.Plant_Identifying_AI.model.ChatMessage;
import com.Plant_Identifying_AI.model.PlantInfo;
import com.Plant_Identifying_AI.service.PlantIdentifierService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:63342", allowCredentials = "true")
public class PlantIdentiferController {

    @Autowired
    private PlantIdentifierService plantIdentifierService;

    private static final String PLANT_INFO = "plantInfo";
    private static final String QUESTION_COUNT = "quesCount";
    private static final String CHAT_HISTORY = "chatHistory";
    private static final int MAX_QUESTIONS = 20;

    @PostMapping(value = "/identify", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> identifyPlant(@RequestParam("image") MultipartFile image, HttpSession session) {

        try {
            if (image.isEmpty()) {
                return ResponseEntity.badRequest().body("No image provided");
            }
            String contentType = image.getContentType();

            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.badRequest().body("File must be Image");
            }

            if (image.getSize() > 10 * 1024 * 1024) {
                return ResponseEntity.badRequest().body("Image Too large Max siz is 10MB");
            }

            byte[] imageBytes = image.getBytes();
            var result = plantIdentifierService.identifyPlant(imageBytes, contentType);

            if (!result.isPlant()) {
                return ResponseEntity.ok("This doesn't apper to be a plant image" + result.reason());
            }

            session.setAttribute(PLANT_INFO, result.plantInfo());
            session.setAttribute(QUESTION_COUNT, 0);
            session.setAttribute(CHAT_HISTORY, new ArrayList<ChatMessage>());

            return ResponseEntity.ok(result.plantInfo());

        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                    .body("Failed to analyze image. Please try again.");
        }
    }

    @PostMapping("/ask")
    public ResponseEntity<?> askQuestion(
            @RequestBody ChatMessage chatMessage,
            HttpSession session
    ) {
        //System.out.println(chatMessage);
        Integer count = (Integer) session.getAttribute(QUESTION_COUNT);
        if (count == null) count = 0;

        if (count >= MAX_QUESTIONS) {
            return ResponseEntity.badRequest()
                    .body("Limit reached: 20 questions.");
        }

        PlantInfo plantInfo = (PlantInfo) session.getAttribute(PLANT_INFO);
        System.out.println(session.getId());
        if (plantInfo == null) {
            return ResponseEntity.badRequest()
                    .body("Please identify a plant first.");
        }

        List<ChatMessage> history =
                (List<ChatMessage>) session.getAttribute(CHAT_HISTORY);

        if (history == null) {
            history = new ArrayList<>();
        }

        String answer = plantIdentifierService.askQuestion(
                chatMessage,
                plantInfo,
                history
        );

        history.add(chatMessage);
        history.add(ChatMessage.assistant(answer));

        session.setAttribute(CHAT_HISTORY, history);
        session.setAttribute(QUESTION_COUNT, count + 1);

        return ResponseEntity.ok(answer);
    }


    @PostMapping("/reset")
    public ResponseEntity<?> resetSession(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok("Session cleared");
    }

}

