package com.example.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import java.net.HttpURLConnection;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.service.RedirectUrlService;

@Controller
@RequestMapping(value = "/hello")
@RequiredArgsConstructor
public class SomeController {
    private final RedirectUrlService redirectUrlService;

    @GetMapping
    public void helloRedirect(HttpServletRequest request, HttpServletResponse response) {
        var redirectUrl = redirectUrlService.getRedirectUrl();
        response.setHeader("CUSTOM", "not important");
        response.setHeader(HttpHeaders.LOCATION, redirectUrl);
        response.setStatus(HttpURLConnection.HTTP_MOVED_PERM);
    }
}
