package com.gacha.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gacha.model.dto.request.EmailVerificationRequest;
import com.gacha.model.dto.response.Response;
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
}
