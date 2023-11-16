package com.eda.security.service;

import com.eda.security.dto.request.BookRequest;
import com.eda.security.entity.BookEntity;

import java.util.List;

public interface BookService {
    void save(BookRequest request);
    List<BookEntity> findAll();
}
