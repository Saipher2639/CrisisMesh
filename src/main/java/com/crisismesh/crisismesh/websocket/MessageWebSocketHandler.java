package com.crisismesh.crisismesh.websocket;

import com.crisismesh.crisismesh.security.EncryptionService;
import com.crisismesh.crisismesh.model.Message;
import com.crisismesh.crisismesh.routing.RoutingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.HashSet;

@Component
public class MessageWebSocketHandler extends TextWebSocketHandler {

    private final EncryptionService encryptionService =
            new EncryptionService();
    private final RoutingService routingService;
    private final ObjectMapper objectMapper;

    private final Map<String, WebSocketSession> connectedNodes =
            new ConcurrentHashMap<>();
    private final PriorityMessageQueue messageQueue =
            new PriorityMessageQueue();
    private final HashSet<String> processedMessages =
            new HashSet<>();
    public MessageWebSocketHandler(
            RoutingService routingService,
            ObjectMapper objectMapper) {

        this.routingService = routingService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {

        String node = getNodeName(session);

        if (node != null) {

            connectedNodes.put(node, session);

            System.out.println(
                    "Node connected: " + node
            );
        }
    }

    @Override
    protected void handleTextMessage(
            WebSocketSession session,
            TextMessage message) {

        try {

            String currentNode = getNodeName(session);

            if (currentNode == null) {
                return;
            }

            String decryptedJson =
                    encryptionService.decrypt(message.getPayload());

            Message msg =
                    objectMapper.readValue(decryptedJson, Message.class);
            if (!processedMessages.add(msg.getMessageId())) {

                System.out.println(
                        "⚠️ Duplicate message ignored: "
                                + msg.getMessageId()
                );

                return;
            }
            messageQueue.addMessage(msg);

            Message nextMessage =
                    messageQueue.getNextMessage();

            if (nextMessage != null) {

                processMessage(
                        currentNode,
                        nextMessage
                );
            }

        } catch (Exception e) {

            System.out.println("❌ WebSocket error");
            e.printStackTrace();
        }
    }

    private void processMessage(
            String currentNode,
            Message msg) {

        System.out.println(
                "Message received at "
                        + currentNode
                        + " for "
                        + msg.getTarget()
        );

        // ACK message
        if (msg.isAcknowledgement()) {

            System.out.println(
                    "📨 ACK received at "
                            + currentNode
                            + " for message "
                            + msg.getMessageId()
            );

            return;
        }

        // Target reached
        if (currentNode.equals(msg.getTarget())) {

            System.out.println(
                    "✅ MESSAGE DELIVERED TO "
                            + currentNode
                            + ": "
                            + msg.getContent()
            );

            // Send ACK back to source
            sendAcknowledgement(msg);

            return;
        }

        // Find route
        List<String> route =
                routingService.findRoute(
                        currentNode,
                        msg.getTarget()
                );

        System.out.println(
                "Route from "
                        + currentNode
                        + ": "
                        + route
        );

        if (route.isEmpty()) {

            System.out.println(
                    "❌ No route available from "
                            + currentNode
                            + " to "
                            + msg.getTarget()
            );

            return;
        }

        int currentIndex =
                route.indexOf(currentNode);

        if (currentIndex == -1 ||
                currentIndex == route.size() - 1) {

            return;
        }

        String nextNode =
                route.get(currentIndex + 1);

        WebSocketSession nextSession =
                connectedNodes.get(nextNode);

        if (nextSession == null ||
                !nextSession.isOpen()) {

            System.out.println(
                    "❌ Next node offline: "
                            + nextNode
            );

            return;
        }

        try {

            String json =
                    objectMapper.writeValueAsString(msg);

            nextSession.sendMessage(
                    new TextMessage(json)
            );

            System.out.println(
                    "📡 Forwarded "
                            + currentNode
                            + " -> "
                            + nextNode
            );

            processMessage(
                    nextNode,
                    msg
            );

        } catch (Exception e) {

            System.out.println(
                    "❌ Failed to forward message"
            );

            e.printStackTrace();
        }
    }

    private void sendAcknowledgement(Message originalMessage) {

        try {

            Message ack = new Message();

            ack.setMessageId(
                    originalMessage.getMessageId()
            );

            ack.setSource(
                    originalMessage.getTarget()
            );

            ack.setTarget(
                    originalMessage.getSource()
            );

            ack.setContent(
                    "ACK: Message delivered successfully"
            );

            ack.setPriority(10);

            ack.setAcknowledgement(true);

            processAcknowledgement(
                    originalMessage.getTarget(),
                    ack
            );

        } catch (Exception e) {

            System.out.println(
                    "❌ Failed to create ACK"
            );

            e.printStackTrace();
        }
    }

    private void processAcknowledgement(
            String currentNode,
            Message ack) {

        if (currentNode.equals(ack.getTarget())) {

            System.out.println(
                    "✅ ACK DELIVERED TO "
                            + currentNode
                            + " FOR "
                            + ack.getMessageId()
            );

            return;
        }

        List<String> route =
                routingService.findRoute(
                        currentNode,
                        ack.getTarget()
                );

        if (route.isEmpty()) {

            System.out.println(
                    "❌ No route available for ACK"
            );

            return;
        }

        int currentIndex =
                route.indexOf(currentNode);

        if (currentIndex == -1 ||
                currentIndex == route.size() - 1) {

            return;
        }

        String nextNode =
                route.get(currentIndex + 1);

        WebSocketSession nextSession =
                connectedNodes.get(nextNode);

        if (nextSession == null ||
                !nextSession.isOpen()) {

            System.out.println(
                    "❌ ACK next node offline: "
                            + nextNode
            );

            return;
        }

        try {

            String json =
                    objectMapper.writeValueAsString(ack);

            nextSession.sendMessage(
                    new TextMessage(json)
            );

            System.out.println(
                    "📨 ACK forwarded "
                            + currentNode
                            + " -> "
                            + nextNode
            );

            processAcknowledgement(
                    nextNode,
                    ack
            );

        } catch (Exception e) {

            System.out.println(
                    "❌ Failed to forward ACK"
            );

            e.printStackTrace();
        }
    }

    @Override
    public void afterConnectionClosed(
            WebSocketSession session,
            org.springframework.web.socket.CloseStatus status) {

        String node = getNodeName(session);

        if (node != null) {

            connectedNodes.remove(node);

            System.out.println(
                    "Node disconnected: "
                            + node
            );
        }
    }

    private String getNodeName(
            WebSocketSession session) {

        if (session.getUri() == null) {
            return null;
        }

        String query =
                session.getUri().getQuery();

        if (query == null) {
            return null;
        }

        for (String parameter :
                query.split("&")) {

            String[] parts =
                    parameter.split("=");

            if (parts.length == 2 &&
                    parts[0].equals("node")) {

                return parts[1];
            }
        }

        return null;
    }
}