package org.crptApi;

import org.crptApi.dto.Document;
import org.crptApi.dto.Product;
import org.crptApi.utils.PropertyLoader;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        PropertyLoader propertyLoader = new PropertyLoader("src/main/resources/config.properties");
        CrptApi crptApi = new CrptApi(TimeUnit.MINUTES, 10, propertyLoader);

        Document document = Document.builder()
                .description("Test Document")
                .participantInn("1234567890")
                .docId("doc_001")
                .docStatus("active")
                .docType("LP_INTRODUCE_GOODS")
                .importRequest(true)
                .ownerInn("0987654321")
                .productionDate("2024-06-25")
                .productionType("goods")
                .products(List.of(
                        Product.builder()
                                .certificateDocument("cert_doc_001")
                                .certificateDocumentDate("2024-06-25")
                                .certificateDocumentNumber("cert_number_001")
                                .ownerInn("0987654321")
                                .productionDate("2024-06-25")
                                .uitCode("uit_code_001")
                                .regDate("2024-06-25")
                                .build()
                ))
                .build();

        String signature = "example_signature";

        crptApi.createDocument(document, signature);
    }
}