package ru.mirea.coursework.defecttracker.application;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) { super(message); }
}
