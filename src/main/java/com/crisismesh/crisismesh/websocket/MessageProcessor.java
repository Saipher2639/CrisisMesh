package com.crisismesh.crisismesh.websocket;

import com.crisismesh.crisismesh.model.Message;
import org.springframework.stereotype.Component;

@Component
public class MessageProcessor {

    private final PriorityMessageQueue messageQueue;

    public MessageProcessor(PriorityMessageQueue messageQueue) {
        this.messageQueue = messageQueue;
    }

    public void processNext() {

        Message message = messageQueue.getNextMessage();

        if (message == null) {
            return;
        }

        System.out.println(
                "🚨 Processing message: "
                        + message.getMessageId()
                        + " | Priority: "
                        + message.getPriority()
        );
    }
}