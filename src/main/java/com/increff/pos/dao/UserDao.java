package com.increff.pos.dao;

import com.increff.pos.entity.UserPojo;
import org.springframework.stereotype.Repository;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;

@Repository
public class UserDao extends AbstractDao<UserPojo> {

    private static String delete_id = "delete from UserPojo p where id=:id";
    private static String select_id = "select p from UserPojo p where id=:id";
    private static String select_email = "select p from UserPojo p where email=:email";
    private static String select_all = "select p from UserPojo p";


    @Transactional
    public void insert(UserPojo p) {
        em().persist(p);
    }

    public int delete(int id) {
        Query query = em().createQuery(delete_id);
        query.setParameter("id", id);
        return query.executeUpdate();
    }

    public UserPojo select(int id) {
        TypedQuery<UserPojo> query = getQuery(select_id);
        query.setParameter("id", id);
        return getSingleResult(query);
    }

    public UserPojo select(String email) {
        TypedQuery<UserPojo> query = getQuery(select_email);
        query.setParameter("email", email);
        return getSingleResult(query);
    }

    public List<UserPojo> selectAll() {
        TypedQuery<UserPojo> query = getQuery(select_all);
        return query.getResultList();
    }

    public void update(UserPojo p) {
    }


}
