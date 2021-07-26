package com.origami;

import lombok.Data;

import java.time.LocalDateTime;

@Data
class MetaData{

    Integer accessCount;
    LocalDateTime lastAccessed;

    public MetaData() {
        this.accessCount = 0;
        this.lastAccessed = LocalDateTime.now();
    }
}
