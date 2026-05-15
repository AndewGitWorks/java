package services;

public interface GenericOperation<T> {
    T execute(T value);
}