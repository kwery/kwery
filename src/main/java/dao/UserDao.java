package dao;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;
import models.User;

import javax.persistence.EntityManager;

public class UserDao {
    @Inject
    private Provider<EntityManager> entityManagerProvider;

    @Transactional
    public void save(User user) {
        EntityManager manager = entityManagerProvider.get();
        manager.persist(user);
        manager.flush();
    }
}
