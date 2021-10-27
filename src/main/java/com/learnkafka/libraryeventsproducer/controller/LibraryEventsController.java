package com.learnkafka.libraryeventsproducer.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.learnkafka.libraryeventsproducer.domain.LibraryEvent;
import com.learnkafka.libraryeventsproducer.domain.LibraryEventType;
import com.learnkafka.libraryeventsproducer.producer.LibraryEventProducer;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/v1")
@AllArgsConstructor
@Log4j2
public class LibraryEventsController {

    private final LibraryEventProducer libraryEventProducer;

    @PostMapping("/libraryevent")
    public ResponseEntity<LibraryEvent> postLibraryEvent(@RequestBody LibraryEvent libraryEvent) throws JsonProcessingException, ExecutionException, InterruptedException {

        log.info("Before SendLibraryEvent");

        libraryEvent.setLibraryEventType(LibraryEventType.NEW);

        //Invokes async Kafka producer method
       // libraryEventProducer.sendLibraryEvent(libraryEvent);

        //Invokes sync kafka producer method
        //var sendResult = libraryEventProducer.sendLibraryEventSynchronous(libraryEvent);

        //Invokes async kafka producer method using send Method with a ProducerRecord argument
        libraryEventProducer.sendLibraryEvent2(libraryEvent);



        //log.info("SendResult is: {}", sendResult.toString());
        log.info("After SendLibraryEvent");

        return ResponseEntity.status(HttpStatus.CREATED).body(libraryEvent);

    }


    //TODO: Implement put method
    @PutMapping("/libraryevent")
    public ResponseEntity<LibraryEvent> updateLibraryEvent(
            @RequestBody @Valid LibraryEvent libraryEvent
    ) throws JsonProcessingException{

        log.info("Before SendLibraryEvent");

        libraryEvent.setLibraryEventType(LibraryEventType.UPDATE);
        libraryEventProducer.sendLibraryEvent2(libraryEvent);
        log.info("After SendLibraryEvent");

        return ResponseEntity.status(HttpStatus.CREATED).body(libraryEvent);

    }

}
