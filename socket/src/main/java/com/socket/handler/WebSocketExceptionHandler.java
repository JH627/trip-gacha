package com.socket.handler;

import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.stereotype.Controller;
import org.springframework.messaging.simp.annotation.SendToUser;

@Controller
public class WebSocketExceptionHandler {

    @MessageExceptionHandler(IllegalArgumentException.class)
    @SendToUser(destinations = "/queue/errors", broadcast = false)
    public String handleIllegalArgumentException(IllegalArgumentException ex) {
        return "WebSocket 인증 실패: " + ex.getMessage();
    }
}
