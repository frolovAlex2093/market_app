package ru.yandex.practicum.mymarket.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import ru.yandex.practicum.mymarket.model.Item;
import ru.yandex.practicum.mymarket.model.User;
import ru.yandex.practicum.mymarket.repository.ItemRepository;
import ru.yandex.practicum.mymarket.repository.UserRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        userRepository.findByUsername("user")
                .switchIfEmpty(userRepository.save(User.builder()
                        .username("user")
                        .password(passwordEncoder.encode("password"))
                        .enabled(true)
                        .build()))
                .subscribe();

        itemRepository.count()
                .filter(count -> count == 0)
                .flatMapMany(count -> {
                    List<Item> items = List.of(
                            Item.builder().title("Смартфон").description("Современный смартфон с камерой 48 МП")
                                    .imgPath("images/phone.jpg").price(29999L).build(),
                            Item.builder().title("Ноутбук").description("Мощный ноутбук для работы и игр")
                                    .imgPath("images/laptop.jpg").price(74999L).build(),
                            Item.builder().title("Наушники").description("Беспроводные наушники с шумоподавлением")
                                    .imgPath("images/headphones.jpg").price(12999L).build(),
                            Item.builder().title("Часы").description("Умные часы с отслеживанием активности")
                                    .imgPath("images/watch.jpg").price(19999L).build(),
                            Item.builder().title("Планшет").description("Планшет с большим экраном")
                                    .imgPath("images/tablet.jpg").price(34999L).build(),
                            Item.builder().title("Клавиатура").description("Механическая клавиатура с RGB подсветкой")
                                    .imgPath("images/keyboard.jpg").price(5999L).build(),
                            Item.builder().title("Мышь").description("Беспроводная компьютерная мышь")
                                    .imgPath("images/mouse.jpg").price(2499L).build(),
                            Item.builder().title("Монитор").description("Игровой монитор 144 Гц")
                                    .imgPath("images/monitor.jpg").price(45999L).build(),
                            Item.builder().title("Колонки").description("Стерео колонки с сабвуфером")
                                    .imgPath("images/speakers.jpg").price(8999L).build(),
                            Item.builder().title("Флешка").description("USB флеш-накопитель 128 ГБ")
                                    .imgPath("images/flash.jpg").price(1299L).build()
                    );
                    return Flux.fromIterable(items).flatMap(itemRepository::save);
                })
                .subscribe(
                        item -> log.debug("Товар сохранен: {}", item.getTitle()),
                        error -> log.error("Ошибка при инициализации: ", error),
                        () -> log.info("Инициализация данных завершена.")
                );
    }
}