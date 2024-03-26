package com.rose.services.impl;

import com.rose.models.recaptcha.RecaptchaResponse;
import com.rose.services.IRecaptchaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Service
public class RecaptchaServiceImpl implements IRecaptchaService {
    @Autowired
    private RestTemplate restTemplate;

    @Value("${google.recaptcha.key.site}")
    private String reCaptchaKeySite;
    @Value("${google.recaptcha.key.secret}")
    private String reCaptchaKeySecret;
    @Value("${google.recaptcha.threshold}")
    private float threshold;
    @Override
    public RecaptchaResponse verify(String response) {
        URI verifyURI = URI.create(
                String.format("https://www.google.com/recaptcha/api/siteverify?secret=%s&response=%s",reCaptchaKeySecret, response)
        );

        RecaptchaResponse recaptchaResponse = restTemplate.getForObject(verifyURI,RecaptchaResponse.class);
        if (recaptchaResponse != null){
            if (recaptchaResponse.isSuccess() && (recaptchaResponse.getScore()< threshold)
            || (!recaptchaResponse.getAction().equals("login") && !recaptchaResponse.getAction().equals("register"))){
                recaptchaResponse.setSuccess(false);
            }else {
                recaptchaResponse.setSuccess(true);
            }
        }
        return recaptchaResponse;
    }
}
