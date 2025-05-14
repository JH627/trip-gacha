package com.gacha.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gacha.global.api.Response;
import com.gacha.model.dto.user.EmailVerificationConfirmRequest;
import com.gacha.model.dto.user.EmailVerificationRequest;
import com.gacha.model.service.MailService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/email")
public class MailController {
    @Autowired
    private MailService mailService;

    @PostMapping("/verification")
    public Response<?> sendVerifyCodeToEmail(@RequestBody EmailVerificationRequest body) throws Exception {
        // TODO : 이메일 형식인지 체킹
        mailService.sendSimpleMessage(body.getEmail());

        return Response.onSuccess();
    }

    @PostMapping("/verification-confirm")
    public ResponseEntity<Response<?>> checkVerifyCode(@RequestBody EmailVerificationConfirmRequest body) throws Exception {
        // TODO : 이메일 형식인지 체킹
        if (!mailService.verifyCode(body.getEmail(), body.getCode())) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Response.onFailure(HttpStatus.BAD_REQUEST, "400", "일치하지 않음", null));
        } else {
            return ResponseEntity.ok(Response.onSuccess());
        }
    }
}
