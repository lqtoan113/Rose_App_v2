package com.rose.models.recaptcha;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RecaptchaResponse implements Serializable {
    private float score;
    private String action;
    @JsonProperty("challenge_ts")
    private String challengeTs;
    private String hostname;
    private boolean success;
}
