package ch.fhnw.ds.graphql.shop.controllers;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;

import org.reactivestreams.Publisher;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.stereotype.Controller;

import ch.fhnw.ds.graphql.shop.models.Customer;
import ch.fhnw.ds.graphql.shop.models.Product;
import ch.fhnw.ds.graphql.shop.models.ProductInput;
import ch.fhnw.ds.graphql.shop.models.Rating;
import ch.fhnw.ds.graphql.shop.repositories.ShopRepository;
import reactor.core.publisher.Flux;

@Controller
public record ShopController(ShopRepository shopRepository) {

	@QueryMapping
	public Collection<Product> products() {
		return shopRepository.getAllProducts();
	}

	@QueryMapping
	public Optional<Product> product(@Argument String id) {
		return shopRepository.getProductById(id);
	}

	@SchemaMapping
	public List<Rating> ratings(Customer c) {
		return shopRepository.getRatingsForCustomer(c);
	}

	@SchemaMapping
	public List<Rating> ratings(Product p) {
		return shopRepository.getRatingsForProduct(p);
	}

	@SchemaMapping
	public OptionalDouble averageRatingScore(Product p) {
		return shopRepository.getRatingsForProduct(p).stream().mapToInt(r -> r.score()).average();
	}

	@MutationMapping
	public Product createProduct(@Argument ProductInput product) {
		return shopRepository.createProduct(product.title(), product.description(), product.imageUrl());
	}

	@MutationMapping
	public Product updateProduct(@Argument String id, @Argument ProductInput product) {
		Optional<Product> op = shopRepository.getProductById(id);
		if (op.isPresent()) {
			return shopRepository.updateProduct(op.get(), product.title(), product.description(), product.imageUrl());
		}
		return null;
	}

	@MutationMapping
	public Rating rateProduct(@Argument String productId, @Argument String customerId, @Argument int score,
			@Argument String comment) {
		return shopRepository.rateProduct(productId, customerId, score, comment);
	}

	@SubscriptionMapping
	public Publisher<Product> productAdvertisement() {
		return Flux.interval(Duration.ZERO, Duration.ofSeconds(5))
				.map(num -> shopRepository.getRandomProduct());
	}
}
