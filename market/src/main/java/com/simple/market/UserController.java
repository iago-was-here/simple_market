package com.simple.market;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.security.MessageDigest;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepository;
    private final UserTypeRepository userTypeRepository;
    private static final String DEFAULT_PASSWORD = "temp123";
    private static final String SUCCESS_MESSAGE = "Usuário criado com sucesso!";
    private static final String DELETE_MESSAGE = "Usuário deletado com sucesso!";
    private static final String UPDATE_MESSAGE = "Usuário atualizado com sucesso!";
    static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public UserController(UserRepository userRepository, UserTypeRepository userTypeRepository) {
        this.userRepository = userRepository;
        this.userTypeRepository = userTypeRepository;
    }

    @GetMapping
    public String index(Model model) {
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("user", new User());
        return "index";
    }

    @PostMapping("/create")
    public String createUser(@ModelAttribute User user, RedirectAttributes redirectAttributes) {
        user.setData_cadastro(LocalDateTime.now());
        setDefaultPasswordIfEmpty(user);
        setDefaultUserTypeIfEmpty(user);

        userRepository.save(user);

        addFlashMessage(redirectAttributes, SUCCESS_MESSAGE, "success");
        return "redirect:/users";
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        userRepository.deleteById(id);
        addFlashMessage(redirectAttributes, DELETE_MESSAGE, "danger");
        return "redirect:/users";
    }

    @PostMapping("/edit/{id}")
    public String updateUser(@PathVariable Long id, @ModelAttribute User user, RedirectAttributes redirectAttributes) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User existingUser = optionalUser.get();
            updateUserFields(existingUser, user);
            userRepository.save(existingUser);
            addFlashMessage(redirectAttributes, UPDATE_MESSAGE, "warning");
        }
        return "redirect:/users";
    }

    @GetMapping("/fetch/{id}")
    @ResponseBody
    public User fetchUser(@PathVariable Long id) {
        return userRepository.findById(id).orElse(null);
    }

    private void setDefaultPasswordIfEmpty(User user) {
        if (user.getSenha_hash() == null || user.getSenha_hash().isEmpty()) {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            user.setSenha_hash(encoder.encode(DEFAULT_PASSWORD));
        }
    }

    private void setDefaultUserTypeIfEmpty(User user) {
        if (user.getTipo_usuario() == null) {
            userTypeRepository.findById(1L).ifPresent(user::setTipo_usuario);
        }
    }

    private void updateUserFields(User existingUser, User newUser) {
        existingUser.setNome(newUser.getNome());
        existingUser.setLogin(newUser.getLogin());
        existingUser.setAtivo(newUser.getAtivo());

        if (newUser.getSenha_hash() != null && !newUser.getSenha_hash().isEmpty()) {
            existingUser.setSenha_hash(newUser.getSenha_hash());
        }

        if (newUser.getTipo_usuario() != null) {
            existingUser.setTipo_usuario(newUser.getTipo_usuario());
        }
    }

    private void addFlashMessage(RedirectAttributes redirectAttributes, String message, String type) {
        redirectAttributes.addFlashAttribute("message", message);
        redirectAttributes.addFlashAttribute("messageType", type);
    }
}