package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.DuplicateException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository
public class UserRepositoryInMemory implements UserRepository {
    private final Map<Long, User> userMap = new HashMap<>();
    private final Set<String> emails = new HashSet<>();
    private Long id = 0L;

    @Override
    public User createUser(User user) {

        checkExistsEmail(user.getEmail());

        if (user.getId() == null) {
            user.setId(++id);
        }
        userMap.put(id, user);

        return user;
    }

    @Override
    public User getUser(Long id) {
        if (!userMap.containsKey(id)) {
            throw new NotFoundException(String.format("Пользователь с id: %d не найден", id));
        }
        return userMap.get(id);
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(userMap.values());
    }

    @Override
    public User updateUser(Long id, User user) {
        User oldUser = getUser(id);

        oldUser.setName(user.getName() == null ? oldUser.getName() : user.getName());

        String newEmail = user.getEmail();

        if (newEmail != null && !newEmail.equalsIgnoreCase(oldUser.getEmail())) {
            emails.remove(oldUser.getEmail());
            checkExistsEmail(newEmail);
            oldUser.setEmail(newEmail);
        }

        return userMap.get(id);
    }

    @Override
    public void deleteUser(Long id) {
        emails.remove(getUser(id).getEmail());
        userMap.remove(id);
    }

    private void checkExistsEmail(String email) {
        if (!emails.add(email)) {
            throw new DuplicateException("Пользователь с указанным email уже зарегистрирован!");
        }
    }
}
