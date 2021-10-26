package com.learnkafka.libraryeventsproducer.intg.controller;


import com.learnkafka.libraryeventsproducer.domain.Book;
import com.learnkafka.libraryeventsproducer.domain.LibraryEvent;
import com.learnkafka.libraryeventsproducer.domain.LibraryEventType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

//Avoids conf;oct with port 8080 of the running app
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LibraryEventsControllerIntegrationTest {


    @Autowired
    TestRestTemplate restTemplate;

    @Test
    void postLibraryEvent(){

        //GIVEN

        var libraryEvent = LibraryEvent.builder()
                .libraryEventId(null)
                .book(Book.builder()
                        .bookAuthor("Tobi Adegbuji")
                        .bookId(1)
                        .bookName("Living life or Life Living")
                        .build())
                .libraryEventType(LibraryEventType.NEW)
                .build();

        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        var httpEntity = new HttpEntity<>(libraryEvent,headers);

        //WHEN
        var res = restTemplate
                .exchange("/v1/libraryevent", HttpMethod.POST, httpEntity, LibraryEvent.class);
        //THEN
        assertEquals(HttpStatus.CREATED, res.getStatusCode());

    }



}
