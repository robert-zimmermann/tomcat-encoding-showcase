package com.example.service;

import org.springframework.stereotype.Service;

@Service
public class RedirectUrlService {
    public String getRedirectUrl() {
        return "https://example.com";
    }
}
