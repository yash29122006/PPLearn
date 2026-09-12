package com.p2plearn.learning.service;

import com.p2plearn.learning.dto.WebSocketEvent;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebSocketBroadcastService {

    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketBroadcastService(
            SimpMessagingTemplate messagingTemplate
    ) {
        this.messagingTemplate = messagingTemplate;
    }

    public void broadcastQuestion(Object message) {

        messagingTemplate.convertAndSend(
                "/topic/questions",
                new WebSocketEvent<>(
                        "QUESTION_POSTED",
                        message
                )
        );
    }

    public void broadcastAttempt(Object message) {

        messagingTemplate.convertAndSend(
                "/topic/attempts",
                new WebSocketEvent<>(
                        "ATTEMPT_UPDATED",
                        message
                )
        );
    }

    public void broadcastLeaderboard(Object message) {

        messagingTemplate.convertAndSend(
                "/topic/leaderboard",
                new WebSocketEvent<>(
                        "LEADERBOARD_UPDATED",
                        message
                )
        );
    }
}