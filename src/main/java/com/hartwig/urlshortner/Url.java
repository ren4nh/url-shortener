package com.hartwig.urlshortner;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Data
public class Url implements Serializable {

    @Id
    private UUID id;
    @Column
    private String originalUrl;
    @Column
    private String generatedUrl;
}
