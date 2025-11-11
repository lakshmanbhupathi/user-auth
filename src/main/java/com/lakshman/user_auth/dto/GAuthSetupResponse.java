package com.lakshman.user_auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GAuthSetupResponse {
    private String message;
    private String secretKey;
    private String qrCodeUrl;
}

