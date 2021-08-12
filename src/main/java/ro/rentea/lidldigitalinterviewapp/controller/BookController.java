package ro.rentea.lidldigitalinterviewapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.rentea.lidldigitalinterviewapp.entity.Book;
import ro.rentea.lidldigitalinterviewapp.service.BookService;

@RestController
@RequestMapping(path = "/books")
public class BookController {

    @Autowired
    private BookService bookService;

    @GetMapping
    public ResponseEntity<Iterable<Book>> getAllBooks(@RequestHeader("Authorization") int token) {
        return ResponseEntity.ok().body(bookService.getAllBooks());
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable("id") Long id, @RequestHeader("Authorization") int token) {
        return ResponseEntity.ok().body(bookService.getBookById(id));
    }

    @PostMapping
    public ResponseEntity<Book> createBook(@RequestBody Book book, @RequestHeader("Authorization") int token) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookService.createBook(book));
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<Book> updateBook(@PathVariable("id") Long id, @RequestBody Book book,
                                           @RequestHeader("Authorization") int token) {
        Book dbBook = bookService.updateBook(id, book);
        if (id.equals(dbBook.getId())) {
            return ResponseEntity.ok().body(dbBook);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(dbBook);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<String> deleteBook(@PathVariable Long id, @RequestHeader("Authorization") int token) {
        bookService.deleteBook(id);

        return ResponseEntity.ok("Book with id " + id + " has been deleted.");
    }

}
