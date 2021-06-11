package com.jitterted.mobreg.adapter.in.web;

import com.jitterted.mobreg.adapter.DateTimeFormatting;
import com.jitterted.mobreg.domain.Huddle;
import com.jitterted.mobreg.domain.HuddleId;
import com.jitterted.mobreg.domain.HuddleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import java.time.ZonedDateTime;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminDashboardController {

    private final HuddleService huddleService;

    @Autowired
    public AdminDashboardController(HuddleService huddleService) {
        this.huddleService = huddleService;
    }

    @GetMapping("/dashboard")
    public String dashboardView(Model model, @AuthenticationPrincipal AuthenticatedPrincipal principal) {
        if (principal instanceof OAuth2User oAuth2User) {
            model.addAttribute("username", oAuth2User.getAttribute("login"));
            model.addAttribute("name", oAuth2User.getAttribute("name"));
            model.addAttribute("github_id", oAuth2User.getAttribute("id"));
        }
        List<Huddle> huddles = huddleService.allHuddles();
        List<HuddleSummaryView> huddleSummaryViews = HuddleSummaryView.from(huddles);
        model.addAttribute("huddles", huddleSummaryViews);
        model.addAttribute("scheduleHuddleForm", new ScheduleHuddleForm());
        return "dashboard";
    }

    @GetMapping("/huddle/{huddleId}")
    public String huddleDetailView(Model model, @PathVariable("huddleId") Long huddleId) {
        Huddle huddle = huddleService.findById(HuddleId.of(huddleId))
                                     .orElseThrow(() -> {
                                         throw new ResponseStatusException(HttpStatus.NOT_FOUND);
                                     });

        HuddleDetailView huddleDetailView = HuddleDetailView.from(huddle);
        model.addAttribute("huddle", huddleDetailView);
        model.addAttribute("registration", new RegistrationForm(huddle.getId()));

        return "huddle-detail";
    }

    @PostMapping("/schedule")
    public String scheduleHuddle(ScheduleHuddleForm scheduleHuddleForm) {
        ZonedDateTime dateTime = DateTimeFormatting.fromBrowserDateAndTime(
                scheduleHuddleForm.getDate(),
                scheduleHuddleForm.getTime());
        huddleService.scheduleHuddle(scheduleHuddleForm.getName(), dateTime);
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/register")
    public String registerParticipant(RegistrationForm registrationForm) {
        HuddleId huddleId = HuddleId.of(registrationForm.getHuddleId());
        huddleService.registerParticipant(huddleId,
                                          registrationForm.getName(),
                                          registrationForm.getGithubUsername(),
                                          registrationForm.getDiscordUsername());

        return "redirect:/admin/huddle/" + huddleId.id();
    }
}