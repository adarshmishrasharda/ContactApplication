package com.smart.service;



import java.util.Properties;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.stereotype.Service;

@Service
public class EmailService {
	
	
	public boolean sendEmial(String subject,String message, String to)
	{
		
		 boolean f = false;
		 String from="mishraaadarsh2000@gmail.com";
		 String host="smtp.gmail.com";
		 
		 Properties properties=System.getProperties();
		 
		 System.out.print("Properties"+properties);
		 properties.put("mail.smtp.host", host);
		 properties.put("mail.smtp.port", 465);
		 properties.put("mail.smtp.ssl.enable", "true");
		 
		 Session session = Session.getInstance(properties,new Authenticator() {
		
			 //omginywqnnvfydqd
			 //hogbwstalpcnxkre

			 
			 protected PasswordAuthentication getPasswordAuthetication() {
				 return new PasswordAuthentication("mishraaadarsh2000@gmail.com", "omginywqnnvfydqd");
			 }
		 
		 
		 });
		 
		 session.setDebug(true);
		 
		 MimeMessage m= new MimeMessage(session);
		 
		 try
		 {
			 m.setFrom(from);
			 m.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
			 m.setSubject(subject);
			 m.setText(message);
			 Transport.send(m);
			 
			 System.out.println("Sent success............");
			 f=true;
			 
			 
		 } catch (Exception e) {
			e.printStackTrace();
		}
		 return f;
		
	}
}
