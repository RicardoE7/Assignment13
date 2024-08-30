package com.coderscampus.assignment13.web;

import java.util.Arrays;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.coderscampus.assignment13.domain.Account;
import com.coderscampus.assignment13.domain.User;
import com.coderscampus.assignment13.service.AccountService;
import com.coderscampus.assignment13.service.UserService;

@Controller
public class UserController {

	@Autowired
	private UserService userService;

	@Autowired
	private AccountService accountService;

	@GetMapping("/register")
	public String getCreateUser(ModelMap model) {

		model.put("user", new User());

		return "register";
	}

	@PostMapping("/register")
	public String postCreateUser(User user) {
		userService.saveUser(user);
		return "redirect:/register";
	}

	@GetMapping("/users")
	public String getAllUsers(ModelMap model) {
		Set<User> users = userService.findAll();

		model.put("users", users);
		if (users.size() == 1) {
			model.put("user", users.iterator().next());
		}

		return "users";
	}

	@GetMapping("/users/{userId}")
	public String getOneUser(ModelMap model, @PathVariable Long userId) {
		User user = userService.findById(userId);
		model.put("users", Arrays.asList(user));
		model.put("user", user);
		return "users";
	}

	@GetMapping("/users/{userId}/accounts/{accountId}")
	public String getOneAccount(ModelMap model, @PathVariable Long userId, @PathVariable Long accountId) {
		Account account = accountService.findById(accountId);

		if (account == null) {
			model.put("error", "Account not found");
			return "error"; // redirect to an error page or handle appropriately
		}

		model.put("account", account);
		model.put("userId", userId);
		return "accounts";
	}

	@PostMapping("/users/{userId}")
	public String postOneUser(@PathVariable Long userId, User user) {
		User existingUser = userService.findById(userId);
		if (existingUser != null) {
			existingUser.setName(user.getName());
			existingUser.setAddress(user.getAddress());
			userService.saveUser(existingUser);
		}
		return "redirect:/users/" + userId;
	}

	@PostMapping("/users/{userId}/accounts")
	public String createAccount(@PathVariable Long userId) {
		User user = userService.findById(userId);

		Account account = new Account();
		account.setAccountName("Account #" + (user.getAccounts().size() + 1));

		user.getAccounts().add(account);
		account.getUsers().add(user);

		accountService.saveAccount(account);
		userService.saveUser(user);
		return "redirect:/users/{userId}";
	}

	@PostMapping("/users/{userId}/accounts/{accountId}")
	public String saveExistingAccount(@PathVariable Long userId, @PathVariable Long accountId,
			@RequestParam String accountName, ModelMap model) {
		Account account = accountService.findById(accountId);
		User user = userService.findById(userId);

		if (account == null) {
			model.put("error", "Account not found");
			return "error";
		}

		if (user == null) {
			model.put("error", "User not found");
			return "error";
		}

		account.setAccountName(accountName);
		accountService.saveAccount(account);

		userService.saveUser(user);

		model.put("account", account);
		model.put("userId", userId);

		return "redirect:/users/" + userId;
	}

	@PostMapping("/users/{userId}/delete")
	public String deleteOneUser(@PathVariable Long userId) {
		userService.delete(userId);
		return "redirect:/users";
	}
}
