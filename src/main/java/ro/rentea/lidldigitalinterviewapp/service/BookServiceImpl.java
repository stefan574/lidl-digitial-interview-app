package ro.rentea.lidldigitalinterviewapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ro.rentea.lidldigitalinterviewapp.entity.Book;
import ro.rentea.lidldigitalinterviewapp.exception.BookAlreadyExistsException;
import ro.rentea.lidldigitalinterviewapp.exception.BookNotFoundException;
import ro.rentea.lidldigitalinterviewapp.repository.BookRepository;

import java.util.Optional;

@Service
public class BookServiceImpl implements BookService {

    @Autowired
    private BookRepository bookRepository;

    @Override
    public Iterable<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    @Override
    public Book getBookById(Long id) {
        return getOptionalBookById(id).orElseThrow(() -> new BookNotFoundException(id));
    }

    private Optional<Book> getOptionalBookById(Long id) {
        return bookRepository.findById(id);
    }

    @Override
    public Book createBook(Book book) {
        if (book.getId() != null) {
            getOptionalBookById(book.getId()).stream()
                    .findFirst()
                    .ifPresent((dbBook) -> {
                        throw new BookAlreadyExistsException(dbBook.getId());
                    });
        }

        return bookRepository.save(book);
    }

    @Override
    public Book updateBook(Long id, Book book) {
        return getOptionalBookById(id)
                .map(dbBook -> {
                    dbBook.setTitle(book.getTitle());

                    return bookRepository.save(dbBook);
                })
                .orElseGet(() -> bookRepository.save(book));
    }

    @Override
    public void deleteBook(Long id) {
        Book dbBook = getOptionalBookById(id).orElseThrow(() -> new BookNotFoundException(id));
        bookRepository.delete(dbBook);
    }

}
