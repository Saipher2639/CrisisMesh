package com.crisismesh.crisismesh.websocket;

import com.crisismesh.crisismesh.model.Message;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.concurrent.PriorityBlockingQueue;
@Component
public class PriorityMessageQueue {

    private final PriorityBlockingQueue<Message> queue =
            new PriorityBlockingQueue<>(
                    10,
                    Comparator.comparingInt(Message::getPriority)
                            .reversed()
            );

    public void addMessage(Message message) {
        queue.offer(message);

        System.out.println(
                "📥 Message added to priority queue: "
                        + message.getMessageId()
                        + " | Priority: "
                        + message.getPriority()
        );
    }

    public Message getNextMessage() {
        return queue.poll();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public int size() {
        return queue.size();
    }
}