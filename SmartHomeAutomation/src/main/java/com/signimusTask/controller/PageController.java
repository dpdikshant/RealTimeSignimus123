package com.signimusTask.controller;



import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class PageController {

    @GetMapping("dashboard")
    public String getDashboard() {
        return "dashboard";
    }

    @GetMapping("scenario")
    public String getScenario() {
        return "scenario";
    }

    @GetMapping("sensors")
    public String getSensors() {
        return "sensors";
    }

    @GetMapping("settings")
    public String getSettings() {
        return "settings";
    }

    @GetMapping("assistant")
    public String getAssistant() {
        return "assistant";
    }

    @GetMapping("auth/login")
    public String getLogin() {
        return "login";
    }

    @GetMapping("auth/register")
    public String getRegister() {
        return "register";
    }
}
