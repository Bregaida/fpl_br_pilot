package br.com.fplbr.pilot.auth.application.dto;

import java.util.Objects;

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
    
    // Construtor padrão
    public RegisterRequest() {}
    
    // Construtor com parâmetros
    public RegisterRequest(String fullName, String email, String cpf, String phoneDdd, String phoneNumber, String cep, String address, String addressNumber, String addressComplement, String neighborhood, String city, String uf, String birthDate, String maritalStatus, String pilotType, String company, String loginAlias, String password) {
        this.fullName = fullName;
        this.email = email;
        this.cpf = cpf;
        this.phoneDdd = phoneDdd;
        this.phoneNumber = phoneNumber;
        this.cep = cep;
        this.address = address;
        this.addressNumber = addressNumber;
        this.addressComplement = addressComplement;
        this.neighborhood = neighborhood;
        this.city = city;
        this.uf = uf;
        this.birthDate = birthDate;
        this.maritalStatus = maritalStatus;
        this.pilotType = pilotType;
        this.company = company;
        this.loginAlias = loginAlias;
        this.password = password;
    }
    
    // Getters e Setters básicos
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}

class RegisterResponse {
    public String otpauthUrl;
    public String secretMasked;
    
    public RegisterResponse() {}
    public RegisterResponse(String otpauthUrl, String secretMasked) {
        this.otpauthUrl = otpauthUrl;
        this.secretMasked = secretMasked;
    }
    
    public String getOtpauthUrl() { return otpauthUrl; }
    public void setOtpauthUrl(String otpauthUrl) { this.otpauthUrl = otpauthUrl; }
    public String getSecretMasked() { return secretMasked; }
    public void setSecretMasked(String secretMasked) { this.secretMasked = secretMasked; }
}

class VerifyTotpRequest {
    public String email;
    public int code;
    
    public VerifyTotpRequest() {}
    public VerifyTotpRequest(String email, int code) {
        this.email = email;
        this.code = code;
    }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public int getCode() { return code; }
    public void setCode(int code) { this.code = code; }
}

class LoginRequest {
    public String login;
    public String password;
    public Integer totp; // optional
    
    public LoginRequest() {}
    public LoginRequest(String login, String password, Integer totp) {
        this.login = login;
        this.password = password;
        this.totp = totp;
    }
    
    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public Integer getTotp() { return totp; }
    public void setTotp(Integer totp) { this.totp = totp; }
}

class ForgotRequest {
    public String login;
    
    public ForgotRequest() {}
    public ForgotRequest(String login) {
        this.login = login;
    }
    
    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }
}

class ResetRequest {
    public String tokenOrTemp;
    public String newPassword;
    
    public ResetRequest() {}
    public ResetRequest(String tokenOrTemp, String newPassword) {
        this.tokenOrTemp = tokenOrTemp;
        this.newPassword = newPassword;
    }
    
    public String getTokenOrTemp() { return tokenOrTemp; }
    public void setTokenOrTemp(String tokenOrTemp) { this.tokenOrTemp = tokenOrTemp; }
    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
}


