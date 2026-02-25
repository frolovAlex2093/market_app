package ru.yandex.practicum.mymarket.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.yandex.practicum.mymarket.model.User;
import ru.yandex.practicum.mymarket.repository.UserRepository;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void findByUsername_shouldReturnUserDetails() {
        User user = User.builder()
                .username("ivan")
                .password("hashed_password")
                .enabled(true)
                .build();

        when(userRepository.findByUsername("ivan")).thenReturn(Mono.just(user));

        StepVerifier.create(userService.findByUsername("ivan"))
                .expectNextMatches(details ->
                        details.getUsername().equals("ivan") &&
                                details.getPassword().equals("hashed_password") &&
                                details.isEnabled())
                .verifyComplete();
    }

    @Test
    void findByUsername_whenUserNotFound_shouldReturnEmpty() {
        when(userRepository.findByUsername("ghost")).thenReturn(Mono.empty());

        StepVerifier.create(userService.findByUsername("ghost"))
                .verifyComplete();
    }
}