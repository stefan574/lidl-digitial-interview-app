package ro.rentea.lidldigitalinterviewapp.service;

import ro.rentea.lidldigitalinterviewapp.entity.Book;

public interface BookService {

    Iterable<Book> getAllBooks();

    Book getBookById(Long id);

    Book createBook(Book book);

    Book updateBook(Long id, Book dbBook);

    void deleteBook(Long id);

}
