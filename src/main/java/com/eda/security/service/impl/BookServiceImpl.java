package com.eda.security.service.impl;

import com.eda.security.entity.BookEntity;
import com.eda.security.dto.request.BookRequest;
import com.eda.security.repository.BookRepository;
import com.eda.security.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository repository;

    @Override
    public void save(BookRequest request) {
        var book = BookEntity.builder()
                .id(request.getId())
                .author(request.getAuthor())
                .isbn(request.getIsbn())
                .build();
        repository.save(book);
    }

    @Override
    public List<BookEntity> findAll() {
        return repository.findAll();
    }
}
