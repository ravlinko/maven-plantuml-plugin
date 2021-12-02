package com.github.ravlinko.plantuml.mvn;

public class IsNotValidPathException extends RuntimeException {
    private final String path;

    public IsNotValidPathException(String path) {
        this.path = path;
    }
}
