package main.api;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Schema(description = "Запрос комикса")
@Getter
public class ComicRequest {

    @Schema(description = "The unique ID of the comic resource", example = "1886")
    private String id;

    @Schema(description = "The canonical title of the comic", example = "Official Handbook of the Marvel Universe (2004) #12 (SPIDER-MAN)")
    private String title;

    @Schema(description = "The preferred description of the comic", example = "The spectacular sequel to last year's OFFICIAL HANDBOOK")
    private String description;

    @Schema(description = "The date the resource was most recently modified", example = "-0001-11-30T00:00:00-0500")
    private String modified;

    @Schema(description = "The publication format of the comic e.g. comic, hardcover, trade paperback", example = "Comic")
    private String format;

    @Schema(description = "The number of story pages in the comic", example = "0")
    private String pageCount;

    @Schema(description = "The canonical URL identifier for this resource", example = "http://gateway.marvel.com/v1/public/comics/1886")
    private String resourceURI;

    @Schema(description = "A summary representation of the series to which this comic belongs", example = "Official Handbook of the Marvel Universe (2004)")
    private String series;

    @Schema(description = "The representative image for this comic", example = "http://i.annihil.us/u/prod/marvel/i/mg/b/40/4bc64020a4ccc.jpg")
    private String thumbnail;

}
