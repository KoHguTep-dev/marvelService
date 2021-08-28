package main.repository;

import main.entities.Comic;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ComicsRepository extends MongoRepository<Comic, String> {
}
