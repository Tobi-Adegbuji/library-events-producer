package com.learnkafka.libraryeventsproducer.intg.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learnkafka.libraryeventsproducer.LibraryEventsProducerApplication;
import com.learnkafka.libraryeventsproducer.domain.Book;
import com.learnkafka.libraryeventsproducer.domain.LibraryEvent;
import com.learnkafka.libraryeventsproducer.domain.LibraryEventType;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.TestPropertySource;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

//Avoids conf;oct with port 8080 of the running app
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//Creates Apache Zookeeper & Kafka broker for testing
@EmbeddedKafka(
        topics = {"library-events"},
        partitions = 3)
//Overriding bootstrap servers with embedded brokers property
@TestPropertySource(properties = {"spring.kafka.producer.bootstrap-servers = ${spring.embedded.kafka.brokers}",
        "spring.kafka.admin.properties.bootstrap-servers = ${spring.embedded.kafka.brokers}"})
public class LibraryEventsControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    private Consumer<Integer, String> consumer;

    private ObjectMapper objectMapper;



    @BeforeEach
    void setUp() {
        var config = new HashMap<>(KafkaTestUtils.consumerProps("1", "true", embeddedKafkaBroker));
        consumer = new DefaultKafkaConsumerFactory<>(config, new IntegerDeserializer(), new StringDeserializer()).createConsumer();
        embeddedKafkaBroker.consumeFromAllEmbeddedTopics(consumer);
        objectMapper = new ObjectMapper();
    }


    @AfterEach
    void tearDown() {
        consumer.close();
    }


    @Test
    @DisplayName("Create new Library Event - SUCCESS 201")
    //is a precautionary step that you add for the test cases to timeout
    // after some time. There is a possibility for test cases to run and hang
    // forever since we dont know how long a response can take
    @Timeout(5)
    void postLibraryEvent() throws JsonProcessingException {

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

        var httpEntity = new HttpEntity<>(libraryEvent, headers);

        //WHEN
        var res = restTemplate
                .exchange("/v1/libraryevent", HttpMethod.POST, httpEntity, LibraryEvent.class);

        //THEN
        assertEquals(HttpStatus.CREATED, res.getStatusCode());
        var consumerRecord = KafkaTestUtils.getSingleRecord(consumer, "library-events");

        var expectedRecordValue = objectMapper
                //Makes Json String Look Nice
                .writerWithDefaultPrettyPrinter()
                .writeValueAsString(libraryEvent);

        var recordValue = consumerRecord.value();

        assertEquals(expectedRecordValue, recordValue);


    }


}
