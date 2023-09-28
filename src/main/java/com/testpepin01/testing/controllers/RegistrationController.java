package com.testpepin01.testing.controllers;

import com.testpepin01.testing.models.Policy;
import com.testpepin01.testing.models.User;
import com.testpepin01.testing.models.Vehicle;
import com.testpepin01.testing.repository.PolicyRepository;
import com.testpepin01.testing.repository.UserRepository;
import com.testpepin01.testing.repository.VehicleRepository;
import com.testpepin01.testing.utils.AuthenticationUtils;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Controller
@Slf4j
public class RegistrationController {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private VehicleRepository vehicleRepo;

    @Autowired
    private PolicyRepository policyRepo;

    @GetMapping("/")
    public String redirectToHomePage(){
        return "redirect:/home";
    }

    @GetMapping("/home")
    public String homePage(Model model, HttpSession session){

        model.addAttribute("isAdmin", AuthenticationUtils.isAdmin(session));

        User currUser = (User) session.getAttribute("curr_user");

        if(currUser != null){

            model.addAttribute("userExists", true);
        }
        else{
            model.addAttribute("userExists", false);
        }

        return "home";
    }

    @GetMapping("/register")
    public String registrationForm(Model model, HttpSession session){

        User currUser = (User) session.getAttribute("curr_user");

        if(currUser != null){

            return "redirect:/account";
        }

        model.addAttribute("register", new User());
        model.addAttribute("logger", new com.testpepin01.testing.controllers.Logger());
        model.addAttribute("isAdmin", AuthenticationUtils.isAdmin(session));

        return "register";
    }

    @PostMapping("/register")
    public String registrationFormSubmit(@ModelAttribute User user, @ModelAttribute Logger logger, Model model){

        model.addAttribute("register", user);
        model.addAttribute("logger", logger);

        String enteredUsername = user.getUsername();

        User userData = userRepo.findByUsername(enteredUsername);

        /*
        if(StringUtils.isBlank(user.getFirstName()) ||
            StringUtils.isBlank(user.getLastName())  ||
            StringUtils.isBlank(user.getAddress()) ||
            StringUtils.isBlank(user.getCity()) ||
            StringUtils.isBlank(user.getUsername()) ||
            StringUtils.isBlank(user.getPassword())){

            logger.setErrorMessage("Error: All compulsory fields have not been filled");

            model.addAttribute("register", user);
            model.addAttribute("logger", logger);

            return "register";
        }
        */

        if(userData != null){

            logger.debug("Error: Email already taken.");

            model.addAttribute("register", user);
            model.addAttribute("logger", logger);

            return "register";
        }

        if(!user.getPassword().equals(user.getConfirmPassword())){

            logger.debug("Error: Passwords do not match.");

            model.addAttribute("register", user);
            model.addAttribute("logger", logger);

            return "register";
        }

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        user.setConfirmPassword(encodedPassword);

        if(user.getUsername().equals("sri.gr81@gmail.com")){

            user.setRole("Admin");
        }
        else{

            user.setRole("User");
        }

        userRepo.save(user);

        return "register_success";
    }

    @GetMapping("/userlogin")
    public String loginForm(Model model, HttpSession session){

        User currUser = (User) session.getAttribute("curr_user");

        if(currUser != null){

            return "redirect:/account";
        }

        model.addAttribute("userlogin", new User());
        model.addAttribute("logger", new com.testpepin01.testing.controllers.Logger());
        model.addAttribute("isAdmin", AuthenticationUtils.isAdmin(session));

        return "userlogin";
    }

    @PostMapping("/userlogin")
    public String loginFormSubmit(@ModelAttribute User user, @ModelAttribute Logger logger, HttpSession session, Model model){

        /*
        if(StringUtils.isBlank(user.getUsername()) || StringUtils.isBlank(user.getPassword())){

            logger.setErrorMessage("Error: All compulsory fields have not been filled");

            model.addAttribute("userlogin", user);
            model.addAttribute("logger", logger);
            model.addAttribute("isAdmin", "false");

            return "userlogin";
        }
        */

        String enteredUsername = user.getUsername();

        boolean successfulLogin = false;

        User userData = userRepo.findByUsername(enteredUsername);

        model.addAttribute("userlogin", user);

        session.setAttribute("curr_user", userData);

        if(userData == null){

            logger.debug("Error: User not found.");

            model.addAttribute("userlogin", user);
            model.addAttribute("logger", logger);
            model.addAttribute("isAdmin", AuthenticationUtils.isAdmin(session));

            return "userlogin";
        }

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        String enteredPassword = user.getPassword();
        String actualPassword = userData.getPassword();

        successfulLogin = passwordEncoder.matches(enteredPassword, actualPassword);

        if(successfulLogin){

            if(userData.getRole().equals("Admin")){

                session.setAttribute("isAdmin", true);
            }
            else{
                session.setAttribute("isAdmin", false);
            }

            model.addAttribute("isAdmin", AuthenticationUtils.isAdmin(session));

            return "redirect:/account";
        }
        else{

            logger.debug("Error: Wrong password.");

            model.addAttribute("logger", logger);
            model.addAttribute("isAdmin", AuthenticationUtils.isAdmin(session));

            return "userlogin";
        }
    }

    @GetMapping("/account")
    public String accountPage(HttpSession session, Model model){

        User currUser = (User) session.getAttribute("curr_user");

        model.addAttribute("isAdmin", AuthenticationUtils.isAdmin(session));

        if(currUser != null){

            model.addAttribute("curr_account", currUser);
            model.addAttribute("userExists", true);

            return "account";
        }

        model.addAttribute("userExists", false);

        return "account";
    }

    @PostMapping("/account")
    public String logOutOfAccount(HttpSession session, Model model){

        User currUser = (User) session.getAttribute("curr_user");

        if(currUser == null){

            model.addAttribute("userExists", false);

            return "redirect:/home";
        }

        currUser = null;

        session.setAttribute("curr_user", null);
        session.setAttribute("isAdmin", false);
        model.addAttribute("curr_account", null);
        model.addAttribute("isAdmin", false);
        model.addAttribute("userExists", false);

        return "redirect:/home";
    }

    @GetMapping("/account/insurance")
    public String insurancePage(HttpSession session, Model model){

        User currUser = (User) session.getAttribute("curr_user");

        if(currUser == null){

            return "redirect:/home";
        }

        List<Vehicle> currVehicles = vehicleRepo.findByUsername(currUser.getUsername());
        List<Policy> currPolicies = new ArrayList<Policy>();

       // Logger logger= new Logger();
        com.testpepin01.testing.controllers.Logger logger = new com.testpepin01.testing.controllers.Logger();


        boolean vehiclesExists = true;
        boolean policiesExists = true;

        //if no vehicles in database
        if(currVehicles.isEmpty()){

            vehiclesExists = false;
            logger.setErrorMessage("You have not registered any vehicles.");

            policiesExists = false;
            logger.setSecondaryErrorMessage("You must first register a vehicle to apply for insurance.");
        }

        //if there are vehicles in database
        if(vehiclesExists == true){

            for (Vehicle vehicle : currVehicles) {

                if(vehicle.getInsuranceStatus().equals("Applied")){

                    Policy p = policyRepo.findByVehicleNumber(vehicle.getVehicleNumber());
                    currPolicies.add(p);
                }
            }

            //if vehicles are there but no policies in database
            if(currPolicies.isEmpty()){

                policiesExists = false;
                logger.setErrorMessage("You have not applied any vehicle for insurance.");
            }

        }

        model.addAttribute("curr_account", currUser);
        model.addAttribute("vehicles", currVehicles);
        model.addAttribute("policies", currPolicies);
        model.addAttribute("logger", logger);
        model.addAttribute("vehiclesExists", vehiclesExists);
        model.addAttribute("policiesExists", policiesExists);
        model.addAttribute("isAdmin", AuthenticationUtils.isAdmin(session));

        return "account/insurance";
    }

    @PostMapping("/account/insurance")
    public String logOutOfAccountFromInsurancePage(HttpSession session, Model model){

        User currUser = (User) session.getAttribute("curr_user");

        if(currUser == null){

            model.addAttribute("userExists", false);

            return "redirect:/home";
        }

        currUser = null;

        session.setAttribute("curr_user", null);
        session.setAttribute("isAdmin", false);

        return "redirect:/home";
    }

    @GetMapping("/account/register_vehicle")
    public String vehicleForm(HttpSession session, Model model){

        User currUser = (User) session.getAttribute("curr_user");

        if(currUser == null){

            return "redirect:/home";
        }

        model.addAttribute("vehicle_details", new Vehicle());
        model.addAttribute("logger", new com.testpepin01.testing.controllers.Logger());
        model.addAttribute("isAdmin", AuthenticationUtils.isAdmin(session));

        return "account/register_vehicle";
    }

    @PostMapping("/account/register_vehicle")
    public String vehicleFormSubmit(@ModelAttribute Vehicle vehicle, @ModelAttribute Logger logger, HttpSession session, Model model){

        User currUser = (User) session.getAttribute("curr_user");

        /*
        if(StringUtils.isBlank(vehicle.getVehicleNumber()) ||
            StringUtils.isBlank(vehicle.getVehicleType()) ||
            StringUtils.isBlank(vehicle.getVehicleModel()) ||
            StringUtils.isBlank(vehicle.getRegisteredCity())){

            logger.setErrorMessage("Error: All compulsory fields have not been filled");

            model.addAttribute("vehicle_details", vehicle);
            model.addAttribute("logger", logger);
            model.addAttribute("isAdmin", AuthenticationUtils.isAdmin(session));

            return "account/register_vehicle";
        }
        */

        vehicle.setUsername(currUser.getUsername());
        vehicle.setInsuranceStatus("Not Insured");

        model.addAttribute("vehicle_details", vehicle);

        vehicleRepo.save(vehicle);

        return "account/vehicle_success";
    }

    @GetMapping("/account/register_policy")
    public String policyForm(HttpSession session, Model model){

        User currUser = (User) session.getAttribute("curr_user");

        if(currUser == null){

            return "redirect:/home";
        }

        model.addAttribute("policy_details", new Policy());
        model.addAttribute("logger", new com.testpepin01.testing.controllers.Logger());
        model.addAttribute("isAdmin", AuthenticationUtils.isAdmin(session));

        return "account/register_policy";
    }

    @PostMapping("/account/register_policy")
    public String policyFormSubmit(@ModelAttribute Policy policy, @ModelAttribute Logger logger, HttpSession session, Model model){

        /*
        if(StringUtils.isBlank(policy.getVehicleNumber()) ||
        StringUtils.isBlank(policy.getParkingLocation()) ||
        StringUtils.isBlank(policy.getInsurancePolicy()) ||
        StringUtils.isBlank(policy.getNoClaimBonus()) ||
        StringUtils.isBlank(policy.getAntiTheftDevice()) ||
        StringUtils.isBlank(policy.getPaCoverNamedPerson()) ||
        StringUtils.isBlank(policy.getPaCoverPassengers()) ||
        StringUtils.isBlank(policy.getLegalLiability()) ||
        StringUtils.isBlank(policy.getAutomobileAssociationMember())){

            logger.setErrorMessage("Error: All compulsory fields have not been filled");

            model.addAttribute("policy_details", policy);
            model.addAttribute("logger", logger);

            return "account/register_policy";
        }
        */

        User currUser = (User) session.getAttribute("curr_user");

        policy.setUsername(currUser.getUsername());

        model.addAttribute("policy_details", policy);

        List<Vehicle> currVehicles = vehicleRepo.findByUsername(currUser.getUsername());

        boolean vehicleRegistered = false;
        Vehicle currVehicle = new Vehicle();

        for (Vehicle vehicle : currVehicles) {

            if(vehicle.getVehicleNumber().equals(policy.getVehicleNumber())){

                vehicleRegistered = true;
                currVehicle = vehicle;
            }
        }

        if(!vehicleRegistered){

            logger.debug("Error: Vehicle not registered.");

            model.addAttribute("policy_details", policy);
            model.addAttribute("logger", logger);

            return "account/register_policy";
        }

        if(currVehicle.getInsuranceStatus().equals("Applied")){

            logger.debug("You have already applied for insurance on this vehicle.");

            model.addAttribute("policy_details", policy);
            model.addAttribute("logger", logger);

            return "account/register_policy";
        }

        vehicleRepo.updateInsuranceStatus(currVehicle.getVehicleNumber(), "Applied");

        policyRepo.save(policy);

        return "account/policy_success";
    }

    @GetMapping("/account/removal")
    public String removalForm(Model model, HttpSession session){

        User currUser = (User) session.getAttribute("curr_user");

        if(currUser == null){

            return "redirect:/home";
        }

        model.addAttribute("vehicle_details", new Vehicle());
        model.addAttribute("logger", new com.testpepin01.testing.controllers.Logger() {
        });
        model.addAttribute("isAdmin", AuthenticationUtils.isAdmin(session));

        return "account/removal";
    }

    @PostMapping("/account/removal")
    public String removalForm(@ModelAttribute Vehicle v, @ModelAttribute Logger logger, HttpSession session, Model model){

        model.addAttribute("vehicle_details", v);

        /*
        //check if vehicle number field is blank
        if(StringUtils.isBlank(v.getVehicleNumber())){

            logger.setErrorMessage("Error: Vehicle number not specified. Please enter your vehicle number.");

            model.addAttribute("logger", logger);

            return "account/removal";
        }
        */

        User currUser = (User) session.getAttribute("curr_user");

        List<Vehicle> currVehicles = vehicleRepo.findByUsername(currUser.getUsername());

        boolean vehicleRegistered = false;
        Vehicle currVehicle = new Vehicle();

        for (Vehicle vehicle : currVehicles) {

            if(vehicle.getVehicleNumber().equals(v.getVehicleNumber())){

                vehicleRegistered = true;
                currVehicle = vehicle;
            }
        }

        //check if vehicle is in database
        if(!vehicleRegistered){

            logger.debug("Error: Vehicle not registered.");

            model.addAttribute("logger", logger);

            return "account/removal";
        }

        vehicleRepo.removeVehicle(currVehicle.getVehicleNumber());

        //check if vehicle has policy in database
        if(currVehicle.getInsuranceStatus().equals("Applied")){

            policyRepo.removePolicy(currVehicle.getVehicleNumber());
        }

        model.addAttribute("logger", logger);

        return "account/removal_success";
    }

    @GetMapping("/admin")
    public String adminPage(Model model, HttpSession session){

        User currUser = (User) session.getAttribute("curr_user");

        if(currUser == null){

            return "redirect:/home";
        }

        model.addAttribute("isAdmin", AuthenticationUtils.isAdmin(session));
        model.addAttribute("user_entered", new User());

        boolean userDetailsToggle = false;

        model.addAttribute("userDetailsToggle", userDetailsToggle);

        model.addAttribute("logger", new com.testpepin01.testing.controllers.Logger());

        return "admin";
    }

    @PostMapping("/admin")
    public String adminFormSubmit(Model model, @ModelAttribute Logger logger, @ModelAttribute User user, HttpSession session){

        boolean vehiclesExists = false;
        boolean policiesExists = false;
        boolean userDetailsToggle = false;

        model.addAttribute("isAdmin", AuthenticationUtils.isAdmin(session));
        model.addAttribute("user_entered", user);

        /*
        if(StringUtils.isBlank(user.getUsername())){

            userDetailsToggle = false;

            model.addAttribute("userDetailsToggle", userDetailsToggle);

            logger.setErrorMessage("Error: All compulsory fields have not been filled");

            model.addAttribute("logger", logger);

            return "admin";
        }
        */

        User userData = userRepo.findByUsername(user.getUsername());

        if(userData == null){

            userDetailsToggle = false;

            model.addAttribute("userDetailsToggle", userDetailsToggle);

            logger.debug("Error: No such user found");

            model.addAttribute("logger", logger);

            return "admin";
        }
        else{

            userDetailsToggle = true;

            model.addAttribute("userDetailsToggle", userDetailsToggle);

            model.addAttribute("userData", userData);
        }

        List<Vehicle> vehicles = vehicleRepo.findByUsername(user.getUsername());
        List<Policy> policies = new ArrayList<Policy>();

        for (Vehicle vehicle : vehicles) {

            if(vehicle.getInsuranceStatus().equals("Applied")){

                policies.add(policyRepo.findByVehicleNumber(vehicle.getVehicleNumber()));
            }
        }

        if(vehicles.isEmpty()){
            logger.debug("This user has not registered any vehicles.");
            vehiclesExists = false;
        }
        else{

            model.addAttribute("vehicles", vehicles);

            vehiclesExists = true;
        }

        if(policies.isEmpty()){
            logger.debug("This user has not applied for any insurance policies.");
            policiesExists = false;
        }
        else{

            model.addAttribute("policies", policies);

            policiesExists = true;
        }

        model.addAttribute("vehiclesExists", vehiclesExists);
        model.addAttribute("policiesExists", policiesExists);
        model.addAttribute("userDetailsToggle", userDetailsToggle);

        model.addAttribute("logger", logger);

        return "admin";
    }

    @GetMapping("/contact")
    public String contactPage(Model model, HttpSession session){

        User currUser = (User) session.getAttribute("curr_user");

        if(currUser != null){

            model.addAttribute("userExists", true);
        }
        else{
            model.addAttribute("userExists", false);
        }

        return "contact";
    }
}