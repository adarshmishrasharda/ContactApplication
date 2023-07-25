package com.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.dao.ContactRepository;
import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.helper.Message;

@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ContactRepository contactRepository;

	// method for adding common data to response
	@ModelAttribute // this annoatation is used when we need to run our methode every time for all
					// other method
	public void addCommanData(Model model, Principal principal) {
		String username = principal.getName();
		System.out.println("username" + username);
		// get the user using username(email)
		User user = this.userRepository.getUserByUserName(username);

		System.out.println("USER" + user);

		model.addAttribute("user", user);

	}

	// dashboard home page handler
	@RequestMapping("/index")
	public String dashboard(Model model, Principal principal) {
		model.addAttribute("title", "User Dashboard");

		return "normal/user_dashboard";

	}

	// open add form handler
	@GetMapping("/add-contact")
	public String openAddContactForm(Model model) {
		model.addAttribute("title", "Add Contact");
		model.addAttribute("conatct", new Contact());

		return "normal/add_contact_form";
	}

	// processing Add contact form
	@PostMapping("/process-contact")
	public String processContact(@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile file,
			Principal principal, HttpSession session) {

		try {
			String name = principal.getName();
			User user = this.userRepository.getUserByUserName(name);

			// processing and uploading image file
			if (file.isEmpty()) {
				// if the file is empty the try our message
				System.out.println("Image is uploaded");

				contact.setImageString("contact.png");
			} else {
				// file the file to folder and update the name to contact image field
				contact.setImageString(file.getOriginalFilename());
				File savefile = new ClassPathResource("static/img").getFile();

				Path path = Paths.get(savefile.getAbsolutePath() + File.separator + file.getOriginalFilename());

				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

				System.out.println("image get uploaded");
			}

			contact.setUser(user);
			user.getContacts().add(contact);

			this.userRepository.save(user);

			System.out.println("Added to data base");
			System.out.print("data" + contact);

			// message success have to send from here
			session.setAttribute("message", new Message("Your contact is added successfuly", "success"));

		}

		catch (Exception e) {
			System.out.println("ERROR" + e.getMessage());
			e.printStackTrace();

			// message error have to send from here
			session.setAttribute("message", new Message("Somthing went wrong! Try again", "danger"));

		}

		return "normal/add_contact_form";
	}

	// show contact handler
	// in pagination we have to show 5 contact per page --> [n(variable)]
	// current page = 0 [page (variable)]
	@GetMapping("/show-contacts/{page}")
	public String showContacts(@PathVariable("page") Integer page, Model m, Principal principal) {

		m.addAttribute("title", "Show User Coontacts");

		// method 1 to use this we have to add principle in argument of method and using
		// principle to get current user
		// have to get list of user;
		// String username = principal.getName();

		// User user = this.userRepository.getUserByUserName(username);

		// List<Contact> contactslist = user.getContacts();

		/*
		 * we are not using method1 to get conatact list because in future programming
		 * to we are going to use ContactRepository so we use user repository method to
		 * get it method to fetch list of user
		 */

		// method 2

		// to get contact list we use this method ---> ContactRepository method

		String username = principal.getName();

		User user = this.userRepository.getUserByUserName(username);

		// start without pagination

		// this we use when we are not using pagination
		// List<Contact> contactlist =
		// this.contactRepository.findContactsByUser(user.getId());

		// end without pagination

		// start parination

		// this we use then we use pagination

		// it has two info--->1 current page 2.contact per page
		Pageable pageable = PageRequest.of(page, 3);

		Page<Contact> contactlist = this.contactRepository.findContactsByUser(user.getId(), pageable);

		// end pagination

		m.addAttribute("contacts", contactlist);

		// this is for pagination
		m.addAttribute("currentpage", page);

		m.addAttribute("totalpages", contactlist.getTotalPages());

		return "normal/show_contacts";
	}

	// showing particular conatct details

	@RequestMapping("/{cid}/contact")
	public String showContactDetail(@PathVariable("cid") Integer cid, Model model, Principal principal) {

		System.out.println("CID is" + cid);
		Optional<Contact> contactoptional = this.contactRepository.findById(cid);

		Contact contact = contactoptional.get();

		// check

		String username = principal.getName();
		User user = this.userRepository.getUserByUserName(username);

		if (user.getId() == contact.getUser().getId()) {
			model.addAttribute("contact", contact);
			model.addAttribute("title", contact.getName());

		}

		return "normal/contact_detail";
	}

	// delete contach handler
	@GetMapping("/delete/{cid}")
	public String deleteConatct(@PathVariable("cid") Integer cid, Model model, HttpSession session, Principal pp) {
		Optional<Contact> contactOptional = this.contactRepository.findById(cid);
		Contact contact = contactOptional.get();

		// check lagao jaise show particular contact me lagaya hai (Assignmenet).... jo
		// delete kar raha hai aur jo delete ho raha hai o usi ka conatct hai tabi
		// delete karan hai
		//contact.setUser(null);

		// Assignment-- As we are deleteing contact so its photo also get deleted, so
		// here wright the steps o delete photo also.
		// where is photo -- photo is inside image folder so fristly get image name then
		// delete it

		//this.contactRepository.delete(contact);
		
		User user = this.userRepository.getUserByUserName(pp.getName());
		
		user.getContacts().remove(contact);
		
		this.userRepository.save(user);
		
		
		session.setAttribute("message", new Message("Contact Deleted successfully....", "success"));

		return "redirect:/user/show-contacts/0";
	}

	// open update form handler

	@PostMapping("/update-contact/{cid}")
	public String updateForm(@PathVariable("cid") Integer cid, Model m) {

		m.addAttribute("title", "Update Contact");

		Contact contact = this.contactRepository.findById(cid).get();

		m.addAttribute("contact", contact);

		return "normal/update_form";
	}

	// update contact handler

	@PostMapping("/process-update")
	public String updateHandler(@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile file,
			Principal pp, Model m, HttpSession session) {

		// old contact detail fetch
		Contact oldcontactdetail = this.contactRepository.findById(contact.getCid()).get();

		try {

			if (!file.isEmpty()) {
				// file work-- rewrite
				// delete old photo

				File deletefile = new ClassPathResource("static/img").getFile();

				File file1= new File(deletefile,oldcontactdetail.getImageString());
				file1.delete();
			
				
				// update new photto

				File savefile = new ClassPathResource("static/img").getFile();

				Path path = Paths.get(savefile.getAbsolutePath() + File.separator + file.getOriginalFilename());

				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

				contact.setImageString(file.getOriginalFilename());

			} else {
				contact.setImageString(oldcontactdetail.getImageString());
			}
			User user = this.userRepository.getUserByUserName(pp.getName());
			contact.setUser(user);
			this.contactRepository.save(contact);

			session.setAttribute("message", new Message("Your Contact is updated....", "success"));

		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("contact name" + contact.getPhone());
		System.out.println("contact name" + contact.getName());
		System.out.println("contact name" + contact.getCid());

		return "redirect:/user/" + contact.getCid() + "/contact";
	}
	
	
	//Your Profile Handler
	
	@GetMapping("/profile")
	public String yourProfile(Model m)
	{
		
		m.addAttribute("title","Profile Page");
		return "normal/profile";
	}
	
	
	//Open Setting Handler
	@GetMapping("/setting")
	public String openSetting()
	{
		return "normal/setting";
	}
	
	
	//chnage password handler
	@PostMapping("/change-password")
	public String chnagePassword(@RequestParam("oldpassword") String oldpassword, @RequestParam("newpassword") String newpassword, 
			Principal principal, HttpSession session)
	{
		
		String username = principal.getName();
		
		User currentuser = this.userRepository.getUserByUserName(username);
		
		if(this.bCryptPasswordEncoder.matches(oldpassword, currentuser.getPasswod()))
		{
			//chnage the password
			
			currentuser.setPasswod(this.bCryptPasswordEncoder.encode(newpassword));
		
			this.userRepository.save(currentuser);
			session.setAttribute("message", new Message("Your Password is successfully changed","success"));
		}
		
		else
		{
			//error massege do 
			session.setAttribute("message", new Message("Please enter correct old password","danger"));
			return "redirect:/user/setting";
		}
		System.out.println("pppppppppppp"+currentuser.getPasswod());
		
		
		
		System.out.println("old"+oldpassword+"new"+newpassword);
		return "redirect:/user/index";
	}
}
