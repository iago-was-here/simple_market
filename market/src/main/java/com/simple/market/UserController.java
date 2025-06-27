package com.simple.market;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Controller
public class UserController {

    private final UserRepository userRepository;
    private final UserTypeRepository userTypeRepository; // Você deve criar esse repository

    public UserController(UserRepository userRepository, UserTypeRepository userTypeRepository) {
        this.userRepository = userRepository;
        this.userTypeRepository = userTypeRepository;
    }

    @GetMapping("/users")
    public String index(Model model) {
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("user", new User()); // necessário para o form de criação
        return "index";
    }

    @PostMapping("/users/create")
    public String createUser(@ModelAttribute User user) {
        user.setData_cadastro(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        if (user.getSenha_hash() == null || user.getSenha_hash().isEmpty()) {
            user.setSenha_hash("temp123");
        }

        if (user.getTipo_usuario() == null) {
            // exemplo fixo para tipo_usuario id=1 (ajuste conforme seu banco)
            Optional<UserType> tipo = userTypeRepository.findById(1L);
            tipo.ifPresent(user::setTipo_usuario);
        }

        userRepository.save(user);
        return "redirect:/users";
    }

    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
        return "redirect:/users";
    }

    @PostMapping("/users/edit/{id}")
    public String updateUser(@PathVariable Long id, @ModelAttribute User user) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User existingUser = optionalUser.get();
            existingUser.setNome(user.getNome());
            existingUser.setLogin(user.getLogin());
            existingUser.setAtivo(user.getAtivo());

            // opcional: atualizar senha e tipo_usuario se quiser
            if (user.getSenha_hash() != null && !user.getSenha_hash().isEmpty()) {
                existingUser.setSenha_hash(user.getSenha_hash());
            }

            if (user.getTipo_usuario() != null) {
                existingUser.setTipo_usuario(user.getTipo_usuario());
            }

            userRepository.save(existingUser);
        }
        return "redirect:/users";
    }

    @GetMapping("/users/fetch/{id}")
    @ResponseBody
    public User fetchUser(@PathVariable Long id) {
        return userRepository.findById(id).orElse(null);
    }

}
