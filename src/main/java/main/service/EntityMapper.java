package main.service;

import main.api.CharacterRequest;
import main.api.ComicRequest;
import main.entities.Character;
import main.entities.Comic;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Service
public class EntityMapper {

    public Character mapperCharacter(JSONArray results, int i) {
        JSONObject o = results.getJSONObject(i);
        JSONObject thumbnail = o.getJSONObject("thumbnail");
        Character character = new Character();
        character.setId(String.valueOf(o.get("id")));
        character.setName(String.valueOf(o.get("name")));
        character.setDescription(String.valueOf(o.get("description")));
        try {
            character.setModified(LocalDateTime.parse(String.valueOf(o.get("modified")),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ")));
        } catch (DateTimeParseException e) {
            character.setModified(LocalDateTime.now());
        }
        character.setResourceURI(String.valueOf(o.get("resourceURI")));
        character.setThumbnail(thumbnail.get("path") + "." + thumbnail.get("extension"));
        return character;
    }

    public Comic mapperComic(JSONArray results, int i) {
        JSONObject o = results.getJSONObject(i);
        JSONObject thumbnail = o.getJSONObject("thumbnail");
        JSONObject series = o.getJSONObject("series");
        Comic comic = new Comic();
        comic.setId(String.valueOf(o.get("id")));
        comic.setTitle(String.valueOf(o.get("title")));
        comic.setDescription(String.valueOf(o.get("description")));
        try {
            comic.setModified(LocalDateTime.parse(String.valueOf(o.get("modified")),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ")));
        } catch (DateTimeParseException e) {
            comic.setModified(LocalDateTime.now());
        }
        comic.setFormat(String.valueOf(o.get("format")));
        comic.setPageCount(String.valueOf(o.get("pageCount")));
        comic.setResourceURI(String.valueOf(o.get("resourceURI")));
        comic.setSeries(String.valueOf(series.get("name")));
        comic.setThumbnail(thumbnail.get("path") + "." + thumbnail.get("extension"));
        return comic;
    }

    public void toCharacter(Character character, CharacterRequest request) {
        character.setId(request.getId());
        character.setName(request.getName());
        character.setDescription(request.getDescription());
        try {
            character.setModified(LocalDateTime.parse(request.getModified(),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ")));
        } catch (DateTimeParseException e) {
            character.setModified(LocalDateTime.now());
        }
        character.setResourceURI(request.getResourceURI());
    }

    public void toComic(Comic comic, ComicRequest request) {
        comic.setId(request.getId());
        comic.setTitle(request.getTitle());
        comic.setDescription(request.getDescription());
        try {
            comic.setModified(LocalDateTime.parse(request.getModified(),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ")));
        } catch (DateTimeParseException e) {
            comic.setModified(LocalDateTime.now());
        }
        comic.setFormat(request.getFormat());
        comic.setPageCount(request.getPageCount());
        comic.setResourceURI(request.getResourceURI());
        comic.setSeries(request.getSeries());
    }

}
