package com.example.demo123.data.dto.controller;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordRecovery extends UserForm {
    private String num1;
    private String num2;
    private String password;
}
