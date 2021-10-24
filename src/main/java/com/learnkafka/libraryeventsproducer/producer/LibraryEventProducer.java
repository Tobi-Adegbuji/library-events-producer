package com.learnkafka.libraryeventsproducer.producer;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learnkafka.libraryeventsproducer.domain.LibraryEvent;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Component
@Log4j2
@AllArgsConstructor
public class LibraryEventProducer {

    private final KafkaTemplate<Integer,String> kafkaTemplate;

    private final ObjectMapper objectMapper;



    public void sendLibraryEvent(LibraryEvent libraryEvent) throws JsonProcessingException {

        var key = libraryEvent.getLibraryEventId();

        var value = objectMapper
                //Makes Json String Look Nice
                .writerWithDefaultPrettyPrinter()
                .writeValueAsString(libraryEvent.getBook());

        var listenableFuture = kafkaTemplate.sendDefault(key, value);
        listenableFuture.addCallback(new ListenableFutureCallback<SendResult<Integer, String>>() {
            @Override
            public void onFailure(Throwable ex) {
                handleFailure(key,value,ex);
            }

            @Override
            public void onSuccess(SendResult<Integer, String> result) {
                handleSuccess(key, value, result);
            }
        });

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
