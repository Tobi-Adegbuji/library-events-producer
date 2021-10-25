package com.learnkafka.libraryeventsproducer.producer;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learnkafka.libraryeventsproducer.domain.LibraryEvent;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Component
@Log4j2
public class LibraryEventProducer {

    private final KafkaTemplate<Integer,String> kafkaTemplate;

    private final ObjectMapper objectMapper;

    private String topic = "library-events";

    public LibraryEventProducer(KafkaTemplate<Integer, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }


    public void sendLibraryEvent(LibraryEvent libraryEvent) throws JsonProcessingException {

        var key = libraryEvent.getLibraryEventId();

        var value = objectMapper
                //Makes Json String Look Nice
                .writerWithDefaultPrettyPrinter()
                .writeValueAsString(libraryEvent);

        var listenableFuture = kafkaTemplate
                .sendDefault(key, value); //Send default uses the default topic configured in the yml file.

        //Notice this is async. Controller already gives response before the below methods even print their log statements.
        //The async code is handled in a different thread
        listenableFuture.addCallback(new ListenableFutureCallback<>() {
            @Override
            public void onFailure(Throwable ex) {
                handleFailure(key, value, ex);
            }

            @Override
            public void onSuccess(SendResult<Integer, String> result) {
                handleSuccess(key, value, result);
            }
        });

    }

    public void sendLibraryEvent2(LibraryEvent libraryEvent) throws JsonProcessingException {

        var key = libraryEvent.getLibraryEventId();

        var value = objectMapper
                //Makes Json String Look Nice
                .writerWithDefaultPrettyPrinter()
                .writeValueAsString(libraryEvent);

       var producerRecord = buildProducerRecord(key, value, topic);


        var listenableFuture = kafkaTemplate
                .send(producerRecord); //Send allows you to choose which topic you want to produce to.

        //Notice this is async. Controller already gives response before the below methods even print their log statements.
        //The async code is handled in a different thread
        listenableFuture.addCallback(new ListenableFutureCallback<>() {
            @Override
            public void onFailure(Throwable ex) {
                handleFailure(key, value, ex);
            }

            @Override
            public void onSuccess(SendResult<Integer, String> result) {
                handleSuccess(key, value, result);
            }
        });

    }

    private ProducerRecord<Integer,String> buildProducerRecord(Integer key, String value, String topic) {

        List<Header> recordHeaders = List.of(
                new RecordHeader("event-source", "library-scanner".getBytes()),
                new RecordHeader("hello-header", "helloWorld".getBytes())
        );

        return new ProducerRecord<>(topic, null, key,value,recordHeaders);
    }


    public SendResult<Integer, String> sendLibraryEventSynchronous(LibraryEvent libraryEvent) throws JsonProcessingException, ExecutionException, InterruptedException {

        var key = libraryEvent.getLibraryEventId();

        var value = objectMapper
                //Makes Json String Look Nice
                .writerWithDefaultPrettyPrinter()
                .writeValueAsString(libraryEvent);

        SendResult<Integer, String> sendResult;

        try {

            sendResult = kafkaTemplate.sendDefault(key, value)
                    .get(); //Makes call sync instead of async

        } catch (InterruptedException | ExecutionException e) {
            log.error("InterruptedException | ExecutionException: Error sending the message. Exception is {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Exception: Error sending the message. Exception is {}", e.getMessage());
            throw e;
        }

        return sendResult;

    }


    private void handleFailure(Integer key, String value, Throwable ex) {
        log.error("Error sending the message. Exception is {}", ex.getMessage());
        try {
            throw ex;
        } catch (Throwable throwable) {
            log.error("Error in OnFailure: {}", throwable.getMessage());
        }
    }

    private void handleSuccess(Integer key, String value, SendResult<Integer, String> result) {
        log.info("Message Sent Successfully for the key : {} and value {}, partition is {}",
                key, value, result.getRecordMetadata().partition());
    }


}
