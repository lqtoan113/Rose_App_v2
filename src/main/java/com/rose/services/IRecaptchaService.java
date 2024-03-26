package com.rose.services;

import com.rose.models.recaptcha.RecaptchaResponse;

public interface IRecaptchaService {
    RecaptchaResponse verify(String response);
}
