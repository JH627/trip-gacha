// package com.socket.controller;

// import org.springframework.messaging.handler.annotation.MessageMapping;
// import org.springframework.messaging.simp.SimpMessagingTemplate;
// import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
// import org.springframework.stereotype.Controller;

// import com.socket.model.dto.plan.StartPlanRequest;
// import com.socket.model.store.PlanSessionStore;
// import com.socket.model.store.RoomSessionStore;

// import lombok.RequiredArgsConstructor;

// @RequiredArgsConstructor
// @Controller
// public class GameController {
//     private final SimpMessagingTemplate messagingTemplate;
//     private final PlanSessionStore planStore;
//     private final RoomSessionStore roomStore;

//     @MessageMapping("/plan/start")
//     public void startPlan(StompHeaderAccessor accessor, StartPlanRequest request){

//     }
// }
