package com.simple.market;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Anotação para testar apenas a camada web, focando no UserController
@WebMvcTest(UserController.class)
public class UserControllerTest {

    // MockMvc permite simular requisições HTTP (GET, POST, etc.) sem um servidor real
    @Autowired
    private MockMvc mockMvc;

    // ObjectMapper é usado para converter objetos Java em JSON e vice-versa
    @Autowired
    private ObjectMapper objectMapper;

    // @MockBean cria um "mock" (objeto falso) das dependências do controller.
    // As chamadas a esses repositórios serão interceptadas e controladas por nós.
    @MockBean
    private UserRepository userRepository;

    @MockBean
    private UserTypeRepository userTypeRepository;

    private User user1;
    private User user2;
    private UserType defaultUserType;

    @BeforeEach
    void setUp() {
        // Prepara objetos comuns que serão usados em múltiplos testes
        defaultUserType = new UserType();
        user1 = new User(1L, "João Silva", "joao.silva");
        user2 = new User(2L, "Maria Santos", "maria.santos");
    }

    @Test
    void testIndex_deveRetornarPaginaIndexComListaDeUsuarios() throws Exception {
        // Arrange (Preparar)
        List<User> userList = Arrays.asList(user1, user2);
        // Quando o método findAll() for chamado, retorne nossa lista de usuários mocada.
        when(userRepository.findAll()).thenReturn(userList);

        // Act & Assert (Agir e Verificar)
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk()) // Espera status HTTP 200 (OK)
                .andExpect(view().name("index")) // Espera que a view "index" seja renderizada
                .andExpect(model().attributeExists("users", "user")) // Verifica se os atributos existem no model
                .andExpect(model().attribute("users", hasSize(2))) // Verifica se a lista de usuários tem 2 itens
                .andExpect(model().attribute("users", contains(user1, user2)));
    }

    @Test
    void testCreateUser_deveCriarUsuarioERedirecionar() throws Exception {
        // Arrange (Preparar)
        // Quando o findById(1L) do tipo de usuário for chamado, retorne nosso tipo padrão.
        when(userTypeRepository.findById(1L)).thenReturn(Optional.of(defaultUserType));
        // Quando save for chamado, apenas retorne o mesmo usuário.
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act & Assert (Agir e Verificar)
        mockMvc.perform(post("/users/create")
                        // Usamos .param() para simular dados de um formulário (<form>)
                        .param("nome", "Novo Usuário")
                        .param("login", "novo.login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is3xxRedirection()) // Espera um status de redirecionamento (302)
                .andExpect(redirectedUrl("/users")); // Espera que o redirecionamento seja para /users

        // Verifica se o método save() do repositório foi chamado exatamente 1 vez.
        verify(userRepository, times(1)).save(any(User.class));
        verify(userTypeRepository, times(1)).findById(1L);
    }

    @Test
    void testDeleteUser_deveDeletarUsuarioERedirecionar() throws Exception {
        // Arrange
        Long userIdToDelete = 1L;
        // doNothing() é usado para métodos void. Garante que nenhuma exceção seja lançada.
        doNothing().when(userRepository).deleteById(userIdToDelete);

        // Act & Assert
        mockMvc.perform(get("/users/delete/{id}", userIdToDelete))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users"));

        // Verifica se o método deleteById foi chamado com o ID correto.
        verify(userRepository, times(1)).deleteById(userIdToDelete);
    }

    @Test
    void testUpdateUser_deveAtualizarUsuarioERedirecionar() throws Exception {
        // Arrange
        Long userIdToUpdate = 1L;
        // Quando findById for chamado, retorne nosso usuário de setup.
        when(userRepository.findById(userIdToUpdate)).thenReturn(Optional.of(user1));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act & Assert
        mockMvc.perform(post("/users/edit/{id}", userIdToUpdate)
                        .param("nome", "João Silva Atualizado")
                        .param("login", "joao.silva.novo")
                        .param("ativo", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users"));

        // Verifica se o findById foi chamado.
        verify(userRepository, times(1)).findById(userIdToUpdate);
        // Verifica se o save foi chamado para persistir as alterações.
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testFetchUser_deveRetornarUsuarioEmJsonQuandoEncontrado() throws Exception {
        // Arrange
        Long userIdToFetch = 1L;
        when(userRepository.findById(userIdToFetch)).thenReturn(Optional.of(user1));

        // Act & Assert
        mockMvc.perform(get("/users/fetch/{id}", userIdToFetch)
                        .accept(MediaType.APPLICATION_JSON)) // Indica que esperamos uma resposta JSON
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1))) // Usa JsonPath para verificar campos do JSON
                .andExpect(jsonPath("$.nome", is("João Silva")));
    }

    @Test
    void testFetchUser_deveRetornarVazioQuandoNaoEncontrado() throws Exception {
        // Arrange
        Long userIdToFetch = 99L;
        // Simula o caso em que o usuário não existe no banco
        when(userRepository.findById(userIdToFetch)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/users/fetch/{id}", userIdToFetch)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("")); // Espera que o corpo da resposta seja vazio (pois o controller retorna null)
    }
}

