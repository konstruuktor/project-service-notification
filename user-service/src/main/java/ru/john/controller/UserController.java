package ru.john.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.CollectionModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.john.dto.UserDto;
import ru.john.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "API для управления пользователями")
public class UserController {

    private final UserService service;

    @PostMapping
    @Operation(summary = "Создать нового пользователя")
    @ApiResponse(responseCode = "200", description = "Пользователь успешно создан")
    public ResponseEntity<EntityModel<UserDto>> create(@RequestBody UserDto dto) {
        UserDto user = service.create(dto);
        EntityModel<UserDto> model = EntityModel.of(user,
                linkTo(methodOn(UserController.class).getById(user.getId())).withSelfRel(),
                linkTo(methodOn(UserController.class).getAll()).withRel("all-users"));
        return ResponseEntity.ok(model);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить пользователя по ID")
    @ApiResponse(responseCode = "200", description = "Пользователь найден")
    public ResponseEntity<EntityModel<UserDto>> getById(@PathVariable Long id) {
        UserDto user = service.getById(id);
        return getEntityModelResponseEntity(id, user);
    }

    private ResponseEntity<EntityModel<UserDto>> getEntityModelResponseEntity(@PathVariable Long id, UserDto user) {
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        EntityModel<UserDto> model = EntityModel.of(user,
                linkTo(methodOn(UserController.class).getById(id)).withSelfRel(),
                linkTo(methodOn(UserController.class).getAll()).withRel("all-users"));
        return ResponseEntity.ok(model);
    }

    @GetMapping
    @Operation(summary = "Получить список всех пользователей")
    @ApiResponse(responseCode = "200", description = "Список пользователей")
    public CollectionModel<EntityModel<UserDto>> getAll() {
        List<EntityModel<UserDto>> users = service.getAll().stream()
                .map(u -> EntityModel.of(u,
                        linkTo(methodOn(UserController.class).getById(u.getId())).withSelfRel()))
                .toList();

        return CollectionModel.of(users,
                linkTo(methodOn(UserController.class).getAll()).withSelfRel());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить пользователя по ID")
    @ApiResponse(responseCode = "200", description = "Пользователь успешно обновлён")
    public ResponseEntity<EntityModel<UserDto>> update(@PathVariable Long id, @RequestBody UserDto dto) {
        UserDto updated = service.update(id, dto);
        return getEntityModelResponseEntity(id, updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить пользователя по ID")
    @ApiResponse(responseCode = "204", description = "Пользователь успешно удалён")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}