package com.hartwig.urlshortner;

import net.bytebuddy.utility.RandomString;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
public record UrlController(UrlRepository urlRepository) {

    private static final String URL_REGEX = "^(http:\\/\\/www\\.|https:\\/\\/www\\.|http:\\/\\/|https:\\/\\/)?[a-z0-9]+([\\-\\.]{1}[a-z0-9]+)*\\.[a-z]{2,5}(:[0-9]{1,5})?(\\/.*)?$";

    @PostMapping
    public ResponseEntity save(@RequestParam("url") String url) {
        if (!StringUtils.hasText(url) || !url.matches(URL_REGEX)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiError("Url is invalid"));
        }
        var urlEntity = new Url();
        urlEntity.setId(UUID.randomUUID());
        urlEntity.setOriginalUrl(url);
        urlEntity.setGeneratedUrl(RandomString.make(10));
        urlRepository.save(urlEntity);
        var responseUrl = ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{generatedUrk}").buildAndExpand(urlEntity.getGeneratedUrl()).toUriString();
        return ResponseEntity.status(HttpStatus.CREATED).body(new UrlResponse(responseUrl));
    }

    @GetMapping("/{generatedUrl}")
    public ResponseEntity<Void> get(@PathVariable("generatedUrl") String generatedUrl) {
        var url = urlRepository.findByGeneratedUrl(generatedUrl).orElse(null);
        if (url == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(url.getOriginalUrl())).build();
    }
}
