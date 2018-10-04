package org.softwire.training.views;

import io.dropwizard.jersey.errors.ErrorMessage;
import io.dropwizard.views.View;

import java.nio.charset.StandardCharsets;

public class ErrorView extends View {
    private ErrorMessage errorMessage;

    public ErrorView(ErrorMessage errorMessage) {
        super("ErrorView.mustache", StandardCharsets.UTF_8);
        this.errorMessage = errorMessage;
    }
}
