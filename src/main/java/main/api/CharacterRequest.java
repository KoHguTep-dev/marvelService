package main.api;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Schema(description = "Запрос персонажа")
@Getter
public class CharacterRequest {

    @Schema(description = "The unique ID of the character resource", example = "1011190")
    private String id;

    @Schema(description = "The name of the character", example = "Captain Cross")
    private String name;

    @Schema(description = "A short bio or description of the character", example = "Captain Cross is an officer in the Chicago Police Department")
    private String description;

    @Schema(description = "The date the resource was most recently modified", example = "1969-12-31T19:00:00-0500")
    private String modified;

    @Schema(description = "The canonical URL identifier for this resource", example = "http://gateway.marvel.com/v1/public/characters/1011190")
    private String resourceURI;

    @Schema(description = "The representative image for this character", example = "http://i.annihil.us/u/prod/marvel/i/mg/b/40/image_not_available.jpg")
    private String thumbnail;

}
