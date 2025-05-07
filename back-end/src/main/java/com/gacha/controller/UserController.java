package com.gacha.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gacha.model.dto.request.RegistRequest;
import com.gacha.model.dto.response.RegistResponse;

@RestController
@RequestMapping("/user")
public class UserController {
    @PostMapping("/regist")
    public ResponseEntity<?> regist(@ModelAttribute RegistRequest registRequest){
        return ResponseEntity.ok().body(new RegistResponse("ok", "200", "가입 성공", null));
    }
}
