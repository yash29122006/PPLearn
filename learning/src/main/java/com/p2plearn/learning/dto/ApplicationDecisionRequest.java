package com.p2plearn.learning.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationDecisionRequest {

    @NotNull(message = "Decision is required")
    private Decision decision;

    public enum Decision {
        ACCEPT,
        REJECT
    }
}
