package main.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import main.api.ComicRequest;
import main.entities.Character;
import main.entities.Comic;
import main.service.ComicsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Комиксы", description = "Взаимодействие с базой комиксов")
@RestController
@RequestMapping("/comics")
public class ComicsController {

    private final ComicsService comicsService;

    @Autowired
    public ComicsController(ComicsService comicsService) {
        this.comicsService = comicsService;
    }

    @Operation(summary = "Получить комиксы", description = "Позволяет получить комиксы с сервера Marvel")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", content = @Content),
            @ApiResponse(responseCode = "404", content = @Content)})
    @GetMapping("")
    private ResponseEntity<List<Comic>> getComics(
            @RequestParam(value = "format", required = false) @Parameter(description = "Формат выпуска", example = "comic, magazine, hardcover") String format,
            @RequestParam(value = "title", required = false) @Parameter(description = "Заголовок комикса") String title,
            @RequestParam(value = "titleStartsWith", required = false) @Parameter(description = "Начальные буквы заголовка комикса") String titleStartsWith,
            @RequestParam(value = "orderBy", required = false) @Parameter(description = "Сортировка результату по полю. Добавить \"-\" для сортировки по убыванию", example = "title, modified") String orderBy,
            @RequestParam(value = "limit", defaultValue = "20") @Parameter(description = "Лимит вывода персонажей") int limit,
            @RequestParam(value = "offset", defaultValue = "0") @Parameter(description = "Смещение результата для постраничного вывода") int offset) {
        List<Comic> list = comicsService.getComics(format, title, titleStartsWith, orderBy, limit, offset);
        if (list == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        if (list.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(list);
    }

    @Operation(summary = "Получить комикс по id", description = "Позволяет получить комикс с сервера Marvel")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Comic.class))),
            @ApiResponse(responseCode = "404", content = @Content)})
    @GetMapping("/{comicId}")
    private ResponseEntity<Comic> getComicsId(@PathVariable(name = "comicId") @Parameter(description = "id комикса") String comicId) {
        Comic comic = comicsService.getComic(comicId);
        if (comic == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(comic);
    }

    @Operation(summary = "Получить персонажей по id комикса", description = "Позволяет получить персонажей конкретного комикса с сервера Marvel")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", content = @Content),
            @ApiResponse(responseCode = "404", content = @Content)})
    @GetMapping("/{comicId}/characters")
    private ResponseEntity<List<Character>> getComicsIdCharacters(
            @PathVariable(name = "comicId") @Parameter(description = "id комикса") String comicId,
            @RequestParam(value = "name", required = false) @Parameter(description = "Имя персонажа") String name,
            @RequestParam(value = "nameStartsWith", required = false) @Parameter(description = "Начальные буквы имени персонажа") String nameStartsWith,
            @RequestParam(value = "orderBy", required = false) @Parameter(description = "Сортировка результату по полю", example = "name, modified, -name, -modified") String orderBy,
            @RequestParam(value = "limit", defaultValue = "20") @Parameter(description = "Лимит вывода персонажей") int limit,
            @RequestParam(value = "offset", defaultValue = "0") @Parameter(description = "Смещение результата для постраничного вывода") int offset) {
        List<Character> list = comicsService.getComicCharacters(comicId, name, nameStartsWith, orderBy, limit, offset);
        if (list == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        if (list.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(list);
    }

    @Operation(summary = "Сохранить комикс", description = "Сохраняет комикс в базу данных приложения")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Comic.class))),
            @ApiResponse(responseCode = "400", content = @Content)})
    @PostMapping("")
    private ResponseEntity<Comic> addComic(@RequestBody ComicRequest request) {
        Comic comic = comicsService.addComic(request);
        if (comic == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        return ResponseEntity.ok(comic);
    }

    @Operation(summary = "Изменить комикс", description = "Изменяет данные комикса в базе данных приложения")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Comic.class))),
            @ApiResponse(responseCode = "400", content = @Content)})
    @PutMapping("/{comicId}")
    private ResponseEntity<Comic> editComic(@PathVariable(name = "comicId") @Parameter(description = "id комикса") String comicId, @RequestBody ComicRequest request) {
        Comic comic = comicsService.updateComic(comicId, request);
        if (comic == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        return ResponseEntity.ok(comic);
    }

    @Operation(summary = "Удалить комикс", description = "Удаляет комикс из базы данных приложения")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(example = "true"))),
            @ApiResponse(responseCode = "400", content = @Content(mediaType = "application/json", schema = @Schema(example = "false")))})
    @DeleteMapping("/{comicId}")
    private ResponseEntity<Boolean> deleteComic(@PathVariable(name = "comicId") @Parameter(description = "id комикса") String comicId) {
        if (!comicsService.deleteCharacter(comicId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
        }
        return ResponseEntity.ok(true);
    }

}
