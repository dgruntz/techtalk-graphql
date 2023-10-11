package ch.fhnw.ds.graphql.shop.models;

public record Rating(String id, Product product, Customer customer, int score, String comment) { }
