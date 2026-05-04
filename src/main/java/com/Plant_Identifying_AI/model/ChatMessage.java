package com.Plant_Identifying_AI.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage implements Serializable {

    public enum Role{USER , ASSISTANT}

    private Role role;
    private String content;
    private LocalDateTime timestamp;

    public static ChatMessage user(String content) {
        return ChatMessage.builder()
                .role(Role.USER)
                .content(content)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static ChatMessage assistant(String content) {
        return ChatMessage.builder()
                .role(Role.ASSISTANT)
                .content(content)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
