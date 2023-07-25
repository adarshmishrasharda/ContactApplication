package com.smart.controller;

import java.util.Random;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.service.EmailService;

@Controller
public class ForgetController {

	Random random =new Random(1000);

	@Autowired
	private EmailService emailService;
	//email id open handler
	@RequestMapping("/forgot")
	public String openEmailForm()
	{
		return "forgot_email_form";
	}
	
	
	@PostMapping("/send-otp")
	public String sendOTP(@RequestParam("email") String email, HttpSession session)
	{
		
		System.out.print("email===="+email);
		//generating otp of 4 digit 
		
		
		int otp = random.nextInt(999999);
		
		System.out.println("otp===="+otp);
		
		//Code for send otp to email
		String subject ="OTP From SCM";
		String messege="<h1>OTP = "+otp+"</h1>";
		String to=email;
		boolean flag = this.emailService.sendEmial(subject, messege, to);
		
		if(flag)
		{ 
			return "verify_otp";
			
			
		}
		
		else
		{
			session.setAttribute("message", "check your email id");
			return "forgot_email_form";
			
		}
		
	}
}
