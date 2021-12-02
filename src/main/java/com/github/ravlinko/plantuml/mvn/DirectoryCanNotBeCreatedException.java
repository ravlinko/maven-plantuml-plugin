package com.github.ravlinko.plantuml.mvn;

public class DirectoryCanNotBeCreatedException extends RuntimeException{
    public DirectoryCanNotBeCreatedException(String message) {
        super(message);
    }
}
