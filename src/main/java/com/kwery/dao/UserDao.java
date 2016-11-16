package com.kwery.dao;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;
import com.kwery.models.User;
import ninja.jpa.UnitOfWork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

public class UserDao {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Inject
    private Provider<EntityManager> entityManagerProvider;

    @Transactional
    public void save(User u) {
        logger.trace(">");
        logger.info("Creating user - " + u);
        EntityManager m = entityManagerProvider.get();
        m.persist(u);
        m.flush();
        logger.trace("<");
    }

    @UnitOfWork
    public User getByUsername(String username) {
        EntityManager m = entityManagerProvider.get();
        CriteriaBuilder cb = m.getCriteriaBuilder();
        CriteriaQuery<User> cq = cb.createQuery(User.class);
        Root<User> root = cq.from(User.class);
        cq.where(cb.equal(root.get("username"), username));
        TypedQuery<User> tq = m.createQuery(cq);
        List<User> users = tq.getResultList();

        if (users.isEmpty()) {
            return null;
        } else {
            if (users.size() > 1) {
                throw new AssertionError(String.format("More than one user with user name %s present in users table", username));
            }
            return users.get(0);
        }
    }

    @UnitOfWork
    public User getUser(String username, String password) {
        EntityManager m = entityManagerProvider.get();
        CriteriaBuilder cb = m.getCriteriaBuilder();
        CriteriaQuery<User> cq = cb.createQuery(User.class);
        Root<User> root = cq.from(User.class);

        Predicate p0 = cb.and(cb.equal(root.get("username"), username));
        Predicate p1 = cb.and(cb.equal(root.get("password"), password));

        cq.where(p0, p1);

        TypedQuery<User> tq = m.createQuery(cq);
        List<User> users = tq.getResultList();

        if (users.isEmpty()) {
            return null;
        } else {
            if (users.size() > 1) {
                throw new AssertionError(String.format("More than one user with user name %s present in users table", username));
            }
            return users.get(0);
        }
    }

    @SuppressWarnings("unchecked")
    @UnitOfWork
    public List<User> list() {
        EntityManager m = entityManagerProvider.get();
        return m.createQuery("SELECT u FROM User u").getResultList();
    }

    @Transactional
    public void update(User user) {
        EntityManager m = entityManagerProvider.get();

        //TODO - Use the method
        User fromDb = m.find(User.class, user.getId());;

        if (!user.getUsername().equals(fromDb.getUsername())) {
            throw new CannotModifyUsernameException();
        }

        fromDb.setPassword(user.getPassword());

        m.flush();
    }

    @UnitOfWork
    public User getById(int id) {
        EntityManager m = entityManagerProvider.get();
        return m.find(User.class, id);
    }

    @Transactional
    public void delete(int userId) {
        EntityManager m = entityManagerProvider.get();
        m.remove(m.find(User.class, userId));
    }
}
