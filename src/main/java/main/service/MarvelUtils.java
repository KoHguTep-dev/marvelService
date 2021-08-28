package main.service;

import main.entities.Character;
import main.entities.Comic;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MarvelUtils {

    @Value("${API_LINK}")
    private String API_LINK;
    @Value("${PUBLIC_KEY}")
    private String PUBLIC_KEY;
    @Value("${PRIVATE_KEY}")
    private String PRIVATE_KEY;

    private final EntityMapper entityMapper;

    @Autowired
    public MarvelUtils(EntityMapper entityMapper) {
        this.entityMapper = entityMapper;
    }

    public String buildQueryCharacters() {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String hash = generateHash(timestamp);
        return API_LINK + "/characters" + "?ts=" + timestamp + "&apikey=" + PUBLIC_KEY + "&hash=" + hash;
    }

    public String buildQueryCharacters(String characterId) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String hash = generateHash(timestamp);
        return API_LINK + "/characters/" + characterId + "?ts=" + timestamp + "&apikey=" + PUBLIC_KEY + "&hash=" + hash;
    }

    public String buildQueryCharactersByComics(String characterId) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String hash = generateHash(timestamp);
        return API_LINK + "/characters/" + characterId + "/comics" + "?ts=" + timestamp + "&apikey=" + PUBLIC_KEY + "&hash=" + hash;
    }

    public String buildQueryComics() {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String hash = generateHash(timestamp);
        return API_LINK + "/comics" + "?ts=" + timestamp + "&apikey=" + PUBLIC_KEY + "&hash=" + hash;
    }

    public String buildQueryComics(String comicId) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String hash = generateHash(timestamp);
        return API_LINK + "/comics/" + comicId + "?ts=" + timestamp + "&apikey=" + PUBLIC_KEY + "&hash=" + hash;
    }

    public String buildQueryComicsByCharacters(String comicId) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String hash = generateHash(timestamp);
        return API_LINK + "/comics/" + comicId + "/characters" + "?ts=" + timestamp + "&apikey=" + PUBLIC_KEY + "&hash=" + hash;
    }

    public JSONArray getResultWithParam(String request, Map<String, String> param) {
        request = addParam(request, param);
        return getResult(request);
    }

    public JSONArray getResult(String request) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = getJson(request);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (jsonObject == null || jsonObject.isEmpty() || !String.valueOf(jsonObject.get("code")).equals("200")) {
            return null;
        }
        return jsonObject.getJSONObject("data").getJSONArray("results");
    }

    public String uploadImage(String url) {
        if (url.equals("")) {
            return null;
        }
        String path;
        try {
            URI uri = new URI(url);
            String file = String.valueOf(Paths.get(uri.getPath()).getFileName());
            InputStream is = uri.toURL().openStream();
            if (!Files.exists(Path.of("images/"))) {
                Files.createDirectory(Path.of("images/"));
            }
            if (!Files.exists(Path.of("images/" + file))) {
                Files.copy(is, Path.of("images/" + file));
            }
            path = "images/" + file;
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return path;
    }

    public Map<String, String> createMap(String format, String title, String titleStartsWith, String orderBy, int limit, int offset) {
        Map<String, String> param = new HashMap<>();
        param.put("&format=", format);
        param.put("&title=", title);
        param.put("&titleStartsWith=", titleStartsWith);
        param.put("&orderBy=", orderBy);
        param.put("&limit=", String.valueOf(limit));
        param.put("&offset=", String.valueOf(offset));
        return param;
    }

    public Map<String, String> createMap(String name, String nameStartsWith, String orderBy, int limit, int offset) {
        Map<String, String> param = new HashMap<>();
        param.put("&name=", name);
        param.put("&nameStartsWith=", nameStartsWith);
        param.put("&orderBy=", orderBy);
        param.put("&limit=", String.valueOf(limit));
        param.put("&offset=", String.valueOf(offset));
        return param;
    }

    public List<Comic> getComicsFromResult(JSONArray results) {
        if (results == null) {
            return null;
        }
        List<Comic> comics = new ArrayList<>();
        if (results.isEmpty()) {
            return comics;
        }
        for (int i = 0; i < results.length(); i++) {
            comics.add(entityMapper.mapperComic(results, i));
        }
        return comics;
    }

    public List<Character> getCharactersFromResult(JSONArray results) {
        if (results == null) {
            return null;
        }
        List<Character> characters = new ArrayList<>();
        if (results.isEmpty()) {
            return characters;
        }
        for (int i = 0; i < results.length(); i++) {
            characters.add(entityMapper.mapperCharacter(results, i));
        }
        return characters;
    }

    private String addParam(String request, Map<String, String> param) {
        StringBuilder requestBuilder = new StringBuilder(request);
        for (String key : param.keySet()) {
            if (param.get(key) != null) {
                requestBuilder.append(key).append(param.get(key));
            }
        }
        return requestBuilder.toString();
    }

    private JSONObject getJson(String url) throws IOException, JSONException {
        InputStream is;
        try {
            is = new URL(url).openStream();
        } catch (FileNotFoundException e) {
            return null;
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder builder = new StringBuilder();
        int i;
        while ((i = reader.read()) != -1) {
            builder.append((char) i);
        }
        is.close();
        return new JSONObject(builder.toString());
    }

    private String generateHash(String timestamp) {
        String hash = timestamp + PRIVATE_KEY + PUBLIC_KEY;
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("MD5");
            digest.reset();
            digest.update(hash.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        byte[] bytes = digest.digest();
        BigInteger bigInt = new BigInteger(1,bytes);
        StringBuilder result = new StringBuilder(bigInt.toString(16));
        while(result.length() < 32 ){
            result.insert(0, "0");
        }
        return result.toString();
    }

}
