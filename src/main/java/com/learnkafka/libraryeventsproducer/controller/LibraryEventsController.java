package com.learnkafka.libraryeventsproducer.controller;


import com.learnkafka.libraryeventsproducer.domain.LibraryEvent;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1")
public class LibraryEventsController {

    @PostMapping("/libraryevent")
    public ResponseEntity<LibraryEvent> postLibraryEvent(@RequestBody LibraryEvent libraryEvent) {

        //Invoke Kafka producer

        return ResponseEntity.status(HttpStatus.CREATED).body(libraryEvent);

    }


}
