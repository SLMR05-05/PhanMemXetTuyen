package com.xettuyen.dao;

import com.xettuyen.entity.TohopMonthi;
import org.hibernate.SessionFactory;

/**
 * ToHopMonDAO - DAO cho Entity TohopMonthi
 */
public class ToHopMonDAO extends BaseDAO<TohopMonthi> {

    public ToHopMonDAO(SessionFactory sessionFactory) {
        super(sessionFactory);
    }
}
