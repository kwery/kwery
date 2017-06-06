package com.kwery.dao;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;
import com.kwery.models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

@Singleton
public class UserDao {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Inject
    private Provider<EntityManager> entityManagerProvider;

    @Transactional
    public synchronized void save(User u) {
        long now = System.currentTimeMillis();
        u.setCreated(now);
        u.setUpdated(now);
        logger.info("Creating user - " + u);
        EntityManager m = entityManagerProvider.get();
        m.persist(u);
        m.flush();
    }

    @Transactional
    public User getUserByEmail(String email) {
        EntityManager m = entityManagerProvider.get();
        CriteriaBuilder cb = m.getCriteriaBuilder();
        CriteriaQuery<User> cq = cb.createQuery(User.class);
        Root<User> root = cq.from(User.class);
        cq.where(cb.equal(root.get("email"), email));
        TypedQuery<User> tq = m.createQuery(cq);
        List<User> users = tq.getResultList();

        if (users.isEmpty()) {
            return null;
        } else {
            if (users.size() > 1) {
                throw new AssertionError(String.format("More than one user with email %s present in users table", email));
            }
            return users.get(0);
        }
    }

    @Transactional
    public User getUser(String email, String password) {
        EntityManager m = entityManagerProvider.get();
        CriteriaBuilder cb = m.getCriteriaBuilder();
        CriteriaQuery<User> cq = cb.createQuery(User.class);
        Root<User> root = cq.from(User.class);

        Predicate p0 = cb.and(cb.equal(root.get("email"), email));
        Predicate p1 = cb.and(cb.equal(root.get("password"), password));

        cq.where(p0, p1);

        TypedQuery<User> tq = m.createQuery(cq);

        try {
            return tq.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    @Transactional
    public List<User> list() {
        EntityManager m = entityManagerProvider.get();
        return m.createQuery("SELECT u FROM User u order by u.created ASC").getResultList();
    }

    @Transactional
    public synchronized User update(User user) {
        user.setUpdated(System.currentTimeMillis());
        user.setCreated(getById(user.getId()).getCreated());

        EntityManager m = entityManagerProvider.get();
        user = m.merge(user);
        m.flush();
        return user;
    }

    @Transactional
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
