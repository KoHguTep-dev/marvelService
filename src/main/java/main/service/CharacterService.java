package main.service;

import main.api.CharacterRequest;
import main.entities.Character;
import main.entities.Comic;
import main.repository.CharactersRepository;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class CharacterService {

    private final MarvelUtils marvelUtils;
    private final EntityMapper entityMapper;
    private final CharactersRepository charactersRepository;

    @Autowired
    public CharacterService(MarvelUtils marvelUtils, EntityMapper entityMapper, CharactersRepository charactersRepository) {
        this.marvelUtils = marvelUtils;
        this.entityMapper = entityMapper;
        this.charactersRepository = charactersRepository;
    }

    public List<Character> getCharacters(String name, String nameStartsWith, String orderBy, int limit, int offset) {
        Map<String, String> param = marvelUtils.createMap(name, nameStartsWith, orderBy, limit, offset);
        String request = marvelUtils.buildQueryCharacters();
        JSONArray results = marvelUtils.getResultWithParam(request, param);
        return marvelUtils.getCharactersFromResult(results);
    }

    public Character getCharacter(String characterId) {
        String request = marvelUtils.buildQueryCharacters(characterId);
        JSONArray results = marvelUtils.getResult(request);
        if (results == null) {
            return null;
        }
        return entityMapper.mapperCharacter(results, 0);
    }

    public List<Comic> getCharacterComics(String characterId, String format, String title, String titleStartsWith, String orderBy, int limit, int offset) {
        Map<String, String> param = marvelUtils.createMap(format, title, titleStartsWith, orderBy, limit, offset);
        String request = marvelUtils.buildQueryCharactersByComics(characterId);
        JSONArray results = marvelUtils.getResultWithParam(request, param);
        return marvelUtils.getComicsFromResult(results);
    }

    public Character addCharacter(CharacterRequest request) {
        Character character = new Character();
        entityMapper.toCharacter(character, request);
        return writeThenGet(character, request);
    }

    public Character updateCharacter(String characterId, CharacterRequest request) {
        Character character = charactersRepository.findById(characterId).orElse(null);
        if (character == null) {
            return null;
        }
        entityMapper.toCharacter(character, request);
        return writeThenGet(character, request);
    }

    public boolean deleteCharacter(String characterId) {
        Character character = charactersRepository.findById(characterId).orElse(null);
        if (character == null) {
            return false;
        }
        charactersRepository.deleteById(character.getId());
        return true;
    }

    private Character writeThenGet(Character character, CharacterRequest request) {
        character.setThumbnail(marvelUtils.uploadImage(request.getThumbnail()));
        charactersRepository.save(character);
        String id = character.getId();
        return charactersRepository.findById(id).orElse(null);
    }

}
