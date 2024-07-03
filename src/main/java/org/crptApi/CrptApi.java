package org.crptApi;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.crptApi.dto.Document;
import org.crptApi.utils.PropertyLoader;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class CrptApi {

    private final TimeUnit timeUnit;
    private final int requestLimit;
    private final AtomicInteger requestCount;
    private final long intervalMillis;
    private final PropertyLoader propertyLoader;

    public CrptApi(TimeUnit timeUnit, int requestLimit, PropertyLoader propertyLoader) {
        this.timeUnit = timeUnit;
        this.requestLimit = requestLimit;
        this.requestCount = new AtomicInteger(0);
        this.intervalMillis = timeUnit.toMillis(1);
        this.propertyLoader = propertyLoader;

        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(intervalMillis);
                    requestCount.set(0);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public synchronized void createDocument(Document document, String signature) throws InterruptedException {
        while (requestCount.get() >= requestLimit) {
            wait();
        }

        requestCount.incrementAndGet();

        String baseUrl = propertyLoader.getProperty("api.url");
        String endpoint = propertyLoader.getProperty("api.endpoint");

        RestAssured
                .given()
                .baseUri(baseUrl)
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + signature)
                .body(document)
                .when()
                .post(endpoint)
                .then()
                .statusCode(200);

        requestCount.decrementAndGet();
        notifyAll();
    }
}
