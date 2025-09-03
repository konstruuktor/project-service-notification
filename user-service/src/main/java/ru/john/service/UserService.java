package ru.john.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.john.dto.UserDto;
import ru.john.dto.UserEvent;
import ru.john.entity.User;
import ru.john.kafka.UserEventProducer;
import ru.john.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;
    private final UserEventProducer eventProducer;

    public UserDto create(UserDto dto) {
        User user = User.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .age(dto.getAge())
                .build();
        repository.save(user);
        eventProducer.send(new UserEvent("CREATE", user.getEmail()));
        return toDto(user);
    }

    public UserDto getById(Long id) {
        return repository.findById(id)
                .map(this::toDto)
                .orElse(null);
    }

    public List<UserDto> getAll() {
        return repository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    public UserDto update(Long id, UserDto dto) {
        return repository.findById(id).map(user -> {
            user.setName(dto.getName());
            user.setEmail(dto.getEmail());
            user.setAge(dto.getAge());
            repository.save(user);
            return toDto(user);
        }).orElse(null);
    }

    public void delete(Long id) {
        repository.findById(id).ifPresent(user -> {
            repository.delete(user);
            eventProducer.send(new UserEvent("DELETE", user.getEmail()));
        });
    }

    private UserDto toDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setAge(user.getAge());
        return dto;
    }
}