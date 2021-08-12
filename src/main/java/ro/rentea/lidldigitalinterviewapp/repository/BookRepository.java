package ro.rentea.lidldigitalinterviewapp.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ro.rentea.lidldigitalinterviewapp.entity.Book;

@Repository
public interface BookRepository extends CrudRepository<Book, Long> {
}
