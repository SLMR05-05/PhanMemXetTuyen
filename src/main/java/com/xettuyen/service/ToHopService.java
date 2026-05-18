package com.xettuyen.service;

import java.util.List;

import com.xettuyen.dao.ToHopDAO;
import com.xettuyen.entity.ToHop;

public class ToHopService {

    private ToHopDAO dao = new ToHopDAO();

    public List<ToHop> getAll(int page, int size) {
        return dao.getAll(page, size);
    }

    public void add(ToHop th) {
        dao.insert(th);
    }

    public void update(ToHop th) {
        dao.update(th);
    }

    public void delete(String id) {
        dao.delete(id);
    }
}