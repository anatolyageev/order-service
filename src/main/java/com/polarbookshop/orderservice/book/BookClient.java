package com.polarbookshop.orderservice.book;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

@Component
public class BookClient {

    private static final String BOOKS_ROOT_API = "/books/";
    private final WebClient webClient;

    public BookClient(WebClient webClient) {
        this.webClient = webClient;                  /// A WebClient bean as configured previously in ClientConfig
    }

    public Mono<Book> getBookByIsbn(String isbn) {
        return webClient
                .get()                                                  ///  The request should use the GET method.
                .uri(BOOKS_ROOT_API + isbn)                          ///  The target URI of the request is /books/{isbn}.
                .retrieve()                                             ///  Sends the request and retrieves the response
                .bodyToMono(Book.class)                                 ///  Returns the retrieved object as Mono<Book>
                .timeout(Duration.ofSeconds(3), Mono.empty())
                .onErrorResume(WebClientResponseException.NotFound.class, exception -> Mono.empty())
                .retryWhen(Retry.backoff(3, Duration.ofMillis(100)))
                .onErrorResume(Exception.class, exception -> Mono.empty());
    }
}
