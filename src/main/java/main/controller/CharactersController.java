package main.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import main.api.CharacterRequest;
import main.entities.Character;
import main.entities.Comic;
import main.service.CharacterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Персонажи", description = "Взаимодействие с базой персонажей")

@RestController
@RequestMapping("/characters")
public class CharactersController {

    private final CharacterService characterService;

    @Autowired
    public CharactersController(CharacterService characterService) {
        this.characterService = characterService;
    }

    @Operation(summary = "Получить персонажей", description = "Позволяет получить персонажей с сервера Marvel")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", content = @Content),
            @ApiResponse(responseCode = "404", content = @Content)})
    @GetMapping("")
    private ResponseEntity<List<Character>> getCharacters(
            @RequestParam(value = "name", required = false) @Parameter(description = "Имя персонажа") String name,
            @RequestParam(value = "nameStartsWith", required = false) @Parameter(description = "Начальные буквы имени персонажа") String nameStartsWith,
            @RequestParam(value = "orderBy", required = false) @Parameter(description = "Сортировка результату по полю", example = "name, modified, -name, -modified") String orderBy,
            @RequestParam(value = "limit", defaultValue = "20") @Parameter(description = "Лимит вывода персонажей") int limit,
            @RequestParam(value = "offset", defaultValue = "0") @Parameter(description = "Смещение результата для постраничного вывода") int offset) {
        List<Character> list = characterService.getCharacters(name, nameStartsWith, orderBy, limit, offset);
        if (list == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        if (list.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(list);
    }

    @Operation(summary = "Получить персонажа по id", description = "Позволяет получить персонажа с сервера Marvel")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Character.class))),
            @ApiResponse(responseCode = "404", content = @Content)})
    @GetMapping("/{characterId}")
    private ResponseEntity<Character> getCharacter(@PathVariable(name = "characterId") @Parameter(description = "id персонажа") String characterId) {
        Character character = characterService.getCharacter(characterId);
        if (character == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(character);
    }

    @Operation(summary = "Получить комиксы по id персонажа", description = "Позволяет получить комиксы конкретного персонажа с сервера Marvel")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", content = @Content),
            @ApiResponse(responseCode = "404", content = @Content)})
    @GetMapping("/{characterId}/comics")
    private ResponseEntity<List<Comic>> getCharacterComics(
            @PathVariable(name = "characterId") @Parameter(description = "id персонажа") String characterId,
            @RequestParam(value = "format", required = false) @Parameter(description = "Формат выпуска", example = "comic, magazine, hardcover") String format,
            @RequestParam(value = "title", required = false) @Parameter(description = "Заголовок комикса") String title,
            @RequestParam(value = "titleStartsWith", required = false) @Parameter(description = "Начальные буквы заголовка комикса") String titleStartsWith,
            @RequestParam(value = "orderBy", required = false) @Parameter(description = "Сортировка результату по полю. Добавить \"-\" для сортировки по убыванию", example = "title, modified") String orderBy,
            @RequestParam(value = "limit", defaultValue = "20") @Parameter(description = "Лимит вывода персонажей") int limit,
            @RequestParam(value = "offset", defaultValue = "0") @Parameter(description = "Смещение результата для постраничного вывода") int offset) {
        List<Comic> list = characterService.getCharacterComics(characterId, format, title, titleStartsWith, orderBy, limit, offset);
        if (list == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        if (list.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(list);
    }

    @Operation(summary = "Сохранить персонажа", description = "Сохраняет персонажа в базу данных приложения")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Character.class))),
            @ApiResponse(responseCode = "400", content = @Content)})
    @PostMapping("")
    private ResponseEntity<Character> addCharacter(@RequestBody CharacterRequest request) {
        Character character = characterService.addCharacter(request);
        if (character == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        return ResponseEntity.ok(character);
    }

    @Operation(summary = "Изменить персонажа", description = "Изменяет данные персонажа в базе данных приложения")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Character.class))),
            @ApiResponse(responseCode = "400", content = @Content)})
    @PutMapping("/{characterId}")
    private ResponseEntity<Character> editCharacter(@PathVariable(name = "characterId") @Parameter(description = "id персонажа") String characterId, @RequestBody CharacterRequest request) {
        Character character = characterService.updateCharacter(characterId, request);
        if (character == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        return ResponseEntity.ok(character);
    }

    @Operation(summary = "Удалить персонажа", description = "Удаляет персонажа из базы данных приложения")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(example = "true"))),
            @ApiResponse(responseCode = "400", content = @Content(mediaType = "application/json", schema = @Schema(example = "false")))})
    @DeleteMapping("/{characterId}")
    private ResponseEntity<Boolean> deleteCharacter(@PathVariable(name = "characterId") @Parameter(description = "id персонажа") String characterId) {
        if (!characterService.deleteCharacter(characterId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
        }
        return ResponseEntity.ok(true);
    }

}
