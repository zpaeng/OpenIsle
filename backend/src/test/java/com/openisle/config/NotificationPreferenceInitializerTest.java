package com.openisle.config;

import com.openisle.model.NotificationType;
import com.openisle.model.User;
import com.openisle.repository.UserRepository;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class NotificationPreferenceInitializerTest {

    @Test
    void addsDefaultsToUsers() throws Exception {
        User u1 = new User();
        u1.setId(1L);
        u1.getDisabledNotificationTypes().clear();

        User u2 = new User();
        u2.setId(2L);
        u2.getDisabledNotificationTypes().clear();
        u2.getDisabledNotificationTypes().add(NotificationType.POST_VIEWED);

        UserRepository repo = mock(UserRepository.class);
        when(repo.findAll()).thenReturn(List.of(u1, u2));

        NotificationPreferenceInitializer init = new NotificationPreferenceInitializer(repo);
        init.run();

        assertTrue(u1.getDisabledNotificationTypes().containsAll(
                EnumSet.of(NotificationType.POST_VIEWED, NotificationType.USER_ACTIVITY)));
        assertTrue(u2.getDisabledNotificationTypes().containsAll(
                EnumSet.of(NotificationType.POST_VIEWED, NotificationType.USER_ACTIVITY)));

        verify(repo).save(u1);
        verify(repo).save(u2);
    }
}
