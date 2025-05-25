package com.gacha.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gacha.global.api.Response;
import com.gacha.model.dto.user.EmailVerificationConfirmRequest;
import com.gacha.model.dto.user.EmailVerificationRequest;
import com.gacha.model.service.MailService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "이메일 인증 API")
@RestController
@RequestMapping("/email")
@RequiredArgsConstructor
public class MailController {

    private final MailService mailService;

    @Operation(summary = "이메일 인증코드 발송", description = "사용자가 입력한 이메일을 바탕으로 인증코드를 발송합니다.")
    @PostMapping("/verification")
    public Response<?> sendVerifyCodeToEmail(@RequestBody EmailVerificationRequest body) throws Exception {
        mailService.sendSimpleMessage(body.getEmail());
        return Response.onSuccess();
    }

    @Operation(summary = "이메일 인증코드 확인", description = "이메일로 발송된 이메일 인증코드를 확인합니다.")
    @PostMapping("/verification-confirm")
    public Response<?> checkVerifyCode(@RequestBody EmailVerificationConfirmRequest body) throws Exception {
        mailService.verifyCode(body.getEmail(), body.getCode());
        return Response.onSuccess();
    }
}
