package main.service;

import main.api.ComicRequest;
import main.entities.Character;
import main.entities.Comic;
import main.repository.ComicsRepository;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ComicsService {

    private final MarvelUtils marvelUtils;
    private final EntityMapper entityMapper;
    private final ComicsRepository comicsRepository;

    @Autowired
    public ComicsService(MarvelUtils marvelUtils, EntityMapper entityMapper, ComicsRepository comicsRepository) {
        this.marvelUtils = marvelUtils;
        this.entityMapper = entityMapper;
        this.comicsRepository = comicsRepository;
    }

    public List<Comic> getComics(String format, String title, String titleStartsWith, String orderBy, int limit, int offset) {
        Map<String, String> param = marvelUtils.createMap(format, title, titleStartsWith, orderBy, limit, offset);
        String request = marvelUtils.buildQueryComics();
        JSONArray results = marvelUtils.getResultWithParam(request, param);
        return marvelUtils.getComicsFromResult(results);
    }

    public Comic getComic(String comicId) {
        String request = marvelUtils.buildQueryComics(comicId);
        JSONArray results = marvelUtils.getResult(request);
        if (results == null) {
            return null;
        }
        return entityMapper.mapperComic(results, 0);
    }

    public List<Character> getComicCharacters(String comicId, String name, String nameStartsWith, String orderBy, int limit, int offset) {
        Map<String, String> param = marvelUtils.createMap(name, nameStartsWith, orderBy, limit, offset);
        String request = marvelUtils.buildQueryComicsByCharacters(comicId);
        JSONArray results = marvelUtils.getResultWithParam(request, param);
        return marvelUtils.getCharactersFromResult(results);
    }

    public Comic addComic(ComicRequest request) {
        Comic comic = new Comic();
        entityMapper.toComic(comic, request);
        return writeThenGet(comic, request);
    }

    public Comic updateComic(String comicId, ComicRequest request) {
        Comic comic = comicsRepository.findById(comicId).orElse(null);
        if (comic == null) {
            return null;
        }
        entityMapper.toComic(comic, request);
        return writeThenGet(comic, request);
    }

    public boolean deleteCharacter(String comicId) {
        Comic comic = comicsRepository.findById(comicId).orElse(null);
        if (comic == null) {
            return false;
        }
        comicsRepository.deleteById(comic.getId());
        return true;
    }

    private Comic writeThenGet(Comic comic, ComicRequest request) {
        comic.setThumbnail(marvelUtils.uploadImage(request.getThumbnail()));
        comicsRepository.save(comic);
        String id = comic.getId();
        return comicsRepository.findById(id).orElse(null);
    }

}
