package com.project.back_end.mvc;
import com.project.back_end.services.TokenService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
@Controller
public class DashboardController {
    private final TokenService tokens;
    public DashboardController(TokenService tokens) {this.tokens=tokens;}
    @GetMapping("/adminDashboard") public String adminDashboard(@CookieValue(value="clinic_session",required=false) String token,Model model) {
        if(!tokens.validateToken(token,"admin")) return "redirect:/";
        model.addAttribute("role","admin");return "admin/adminDashboard";
    }
    @GetMapping("/doctorDashboard") public String doctorDashboard(@CookieValue(value="clinic_session",required=false) String token,Model model) {
        if(!tokens.validateToken(token,"doctor")) return "redirect:/";
        model.addAttribute("role","doctor");return "doctor/doctorDashboard";
    }
    @GetMapping("/patientDashboard") public String patientDashboard(@CookieValue(value="clinic_session",required=false) String token,Model model) {
        if(!tokens.validateToken(token,"patient")) return "redirect:/";
        model.addAttribute("role","patient");return "patient/patientDashboard";
    }
}
