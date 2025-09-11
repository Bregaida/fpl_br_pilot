package br.com.fplbr.pilot.auth.application.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
class RegisterRequest {
    public String fullName;
    public String email;
    public String cpf;
    public String phoneDdd;
    public String phoneNumber;
    public String cep;
    public String address;
    public String addressNumber;
    public String addressComplement;
    public String neighborhood;
    public String city;
    public String uf;
    public String birthDate; // dd/MM/yyyy
    public String maritalStatus;
    public String pilotType;
    public String company;
    public String loginAlias;
    public String password;
}

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
class RegisterResponse {
    public String otpauthUrl;
    public String secretMasked;
}

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
class VerifyTotpRequest {
    public String email;
    public int code;
}

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
class LoginRequest {
    public String login;
    public String password;
    public Integer totp; // optional
}

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
class ForgotRequest {
    public String login;
}

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
class ResetRequest {
    public String tokenOrTemp;
    public String newPassword;
}


