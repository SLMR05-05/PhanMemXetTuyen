package com.xettuyen.dao;

import com.xettuyen.entity.BangQuydoi;
import org.hibernate.SessionFactory;

/**
 * BangQuyDoiDAO - DAO cho Entity BangQuydoi
 */
public class BangQuyDoiDAO extends BaseDAO<BangQuydoi> {

    public BangQuyDoiDAO(SessionFactory sessionFactory) {
        super(sessionFactory);
    }
}
