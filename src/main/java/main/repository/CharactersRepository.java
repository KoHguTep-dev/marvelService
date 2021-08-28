package main.repository;

import main.entities.Character;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CharactersRepository extends MongoRepository<Character, String> {
}
