package from_ecom.browser.webclient.Domain

import com.fasterxml.jackson.annotation.JsonCreator

class Basket {
    String id

    Map<String, ErrorDetails> errors;

    @JsonCreator
    public Basket(){

    }

    public static class ErrorDetails {
        String message
        List<String> items
    }
}
