package com.learnkafka.libraryeventsproducer.domain;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class LibraryEvent {

    @NotNull
    private Integer libraryEventId;
    private LibraryEventType libraryEventType;
    private Book book;


}
