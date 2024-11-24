package com.fupto.back.user.emitter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class AlertDto {
    private Long id;
    private String message;
    private Instant ceateDate;
}
