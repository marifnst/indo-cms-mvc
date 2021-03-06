package com.indocms.mvcapp.controller;

import java.util.List;
import java.util.Map;

import com.indocms.mvcapp.service.ApprovalService;
import com.indocms.mvcapp.service.AuthService;
import com.indocms.mvcapp.service.DocumentService;
import com.indocms.mvcapp.service.JobService;
import com.indocms.mvcapp.service.MenuService;

import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.security.core.context.SecurityContextHolder;
// import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class AuthController {

    @Autowired
    private MenuService menuService;

    @Autowired
    private AuthService authService;

    @Autowired
    private ApprovalService approvalService;

    @Autowired
    private JobService jobService;

    @Autowired
    private DocumentService documentService;

    // @GetMapping("/")
    // public ModelAndView defaultPage(@ModelAttribute("login_model_attribute") LoginModel loginModel) {
    //     ModelAndView output = new ModelAndView();
    //     // System.out.println(loginModel.getLoginMessage());
    //     if (loginModel != null && loginModel.getLoginMessage() != null) {
    //         // System.out.println("loginModel.getLoginMessage() : " + loginModel.getLoginMessage());
    //         output.addObject("login_message", loginModel.getLoginMessage());
    //     } else {
    //         output.addObject("login_message", "");
    //     }

    //     output.setViewName("pages/login");
    //     return output;
    // }

    // @RequestMapping(value = "/login", method = RequestMethod.GET)
    // public ModelAndView login() {
    //     ModelAndView output = new ModelAndView();
    //     output.setViewName("login");        
    //     return output;
    // }

    // @RequestMapping(value = "/login", method = RequestMethod.POST)
    // public ModelAndView login(@ModelAttribute LoginModel loginModel, RedirectAttributes redirectAttributes) {
    //     ModelAndView output = new ModelAndView();
    //     System.out.println(loginModel.getUsername() + " : " + loginModel.getPassword());
    //     String username = loginModel.getUsername();
    //     String password = loginModel.getPassword();

        // if (username.equals("admin@admin.com") && password.equals("admin")) {
        //     output.setViewName("redirect:/home");
        // } else {
        //     output.setViewName("redirect:/");
        //     loginModel.setLoginMessage("Invalid Username or Password");
        //     redirectAttributes.addFlashAttribute("login_model_attribute", loginModel);
        // }
    //     output.setViewName("redirect:/home");
    //     return output;
    // }

    // @RequestMapping(value = "/logout", method = RequestMethod.GET)
    // public ModelAndView logout() {
    //     ModelAndView output = new ModelAndView();
    //     output.setViewName("redirect:/login");        
    //     return output;
    // }

    // @GetMapping("/user")
    // public Object getUser() {
    //     Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    //     if (principal instanceof UserDetails) {
    //         String username = ((UserDetails) principal).getUsername();
    //     } else {
    //         String username = principal.toString();
    //     }
    //     return principal;
    // }

    @GetMapping("/register")
    public String registerPage() {
        return "pages/register";
    }

    @RequestMapping(value = "/register/process", method = RequestMethod.POST)
    public String registerProcess() {
        return "redirect:/home";
    }

    @GetMapping("/home")
    public ModelAndView home() {
        ModelAndView output = new ModelAndView();
        output.setViewName("home");
        try {
            // UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();            
            // System.out.println(auth.getName() + " : " + auth.getPrincipal() + " : " + auth.getAuthorities() + " : " + auth.getDetails() + " : " + auth.getCredentials());
            
            Map<String, Object> userInfo = authService.getCurrentUserInfo();
            System.out.println("userInfo : " + userInfo);
            List<Map<String, Object>> menu = menuService.getMenuByAuthority();
            output.addObject("menu", menu);
            output.addObject("user", userInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return output;
    }

    @GetMapping("/dashboard")
    public ModelAndView dashboard() {
        ModelAndView output = new ModelAndView("dashboard");
        try {
            List<Map<String, Object>> approvalTaskList = approvalService.getApprovalTaskList();
            output.addObject("approval_task_list_count", approvalTaskList.size());

            List<Map<String, Object>> runningJobList = jobService.getRunningJob();
            output.addObject("running_job_count", runningJobList.size());

            List<Map<String, Object>> documentList = documentService.viewDocument();
            output.addObject("document_count", documentList.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return output;
    }

    @GetMapping("/profile")
    public String profilePage() {
        return "pages/profile";
    }

    @GetMapping("/role")
    public String rolePage() {
        return "pages/role";
    }

    @GetMapping("/send/verified")
    public String sendVerifiedPage() {
        return "pages/send_verified";
    }

    @GetMapping("/verify/review")
    public String verifyReviewPage() {
        return "pages/verify_review";
    }

    @GetMapping("/score/review")
    public String scoreReviewPage() {
        return "pages/score_review";
    }

    @GetMapping("/dupak")
    public String dupakPage() {
        return "pages/dupak";
    }


    @GetMapping("/document")
    public String documentPage() {
        return "pages/document";
    }

    @GetMapping("/forms")
    public String tes(@RequestParam(name="name", required=false, defaultValue="World") String name, Model model) {
        model.addAttribute("name", name);
        return "pages/forms/general";
    }

    @GetMapping("/chartjs")
    public String chartJs(@RequestParam(name="name", required=false, defaultValue="World") String name, Model model) {
        model.addAttribute("name", name);
        return "pages/charts/chartjs";
    }

}
