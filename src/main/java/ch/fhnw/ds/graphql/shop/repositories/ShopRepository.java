package ch.fhnw.ds.graphql.shop.repositories;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Component;

import ch.fhnw.ds.graphql.shop.models.Customer;
import ch.fhnw.ds.graphql.shop.models.Product;
import ch.fhnw.ds.graphql.shop.models.Rating;

@Component
public class ShopRepository {
	private final AtomicInteger pid = new AtomicInteger(0);
	private final AtomicInteger cid = new AtomicInteger(0);

	private final Map<String, Customer> customers = new HashMap<>();
	private final Map<String, Product> products = new HashMap<>();

	private final Map<Customer, List<Rating>> cRatings = new HashMap<>();
	private final Map<Product, List<Rating>> pRatings = new HashMap<>();

	{
		createCustomer("Daniel");
		createCustomer("Dierk");
		createCustomer("Dominik");

		createProduct(
				"PHILIPPI Paco Brieföffner",
				"Wenn der Postmann zweimal klingelt",
				"https://static.digitecgalaxus.ch/Files/1/3/1/9/0/2/1/2/Unbenannt-121.jpeg?impolicy=ProductTileImage"
		);
		createProduct(
				"Driade Nemo", 
				"Kunst- und Sitzobjekt zugleich: Der von Fabio Novembre für Driade designte Sessel eignet sich sowohl für drinnen als auch für draussen.",
				"https://static.digitecgalaxus.ch/Files/8/1/4/9/5/6/2/051002079_01_1.jpg?impolicy=ProductTileImage"
		);
		createProduct(
				"Monkey Business Umbrella",
				"Das etwas andere Teesieb.",
				"https://static.digitecgalaxus.ch/Files/5/1/7/0/5/5/7/ot800_teeei_regenschirm4_k_z1.jpg?impolicy=ProductTileImage"
		);
		createProduct(
				"Freezack Vibro Mouse", 
				"Die Vibro Mouse aus dem Hause Freezack ist ein vibrierender Spielspass für Ihren Liebling. Das weiche Material schützt Pfoten und Zähne. Die Batterien sind im Lieferumfang enthalten.",
				"https://static.digitecgalaxus.ch/Files/7/8/5/8/3/9/2/31665-freezack-vibro-mouse-orange-31665-p.jpg?impolicy=ProductTileImage"
		);
		createProduct(
				"Moluk Boi",
				"Wie ein Pinguin oder eine Ente ist Boi sowohl zu Lande wie im Wasser in seinem Element.",
				"https://www.galaxus.ch/im/productimages/4/2/2/8/7/2/0/7/3/2/5/9/2/6/8/2/4/2/4/1a91c218-6bc6-45c1-9a1f-dbfce93e81b1_cropped.jpg?impolicy=ProductTileImage"
		);
		createProduct(
				"Monkey Business Penneli",
				"Die Penneli sieht aus wie die typische italienische Pasta, ist aber ein praktischer Knoblauchschäler aus Silikon.",
				"https://www.galaxus.ch/im/productimages/8/6/0/2/2/6/7/3/1/8/0/0/7/2/7/0/2/2/5/0fe1432b-5f28-4f5d-96fb-fe12f3b0b2a9_cropped.jpg?impolicy=ProductTileImage"
		);
		createProduct(
				"Alessi Voile",
				"Die Form des Spaghetti-Spenders entsteht durch drei gewundene Ringe, mit denen eine, zwei oder fünf Portionen Spaghetti gemessen werden.",
				"https://www.galaxus.ch/im/Files/2/0/9/1/4/2/1/1/0004_PG01_GP_300dpi_1250pxl.jpg?impolicy=ProductTileImage"
		);

		rateProduct("0", "0", 1, "really bad");
		rateProduct("1", "0", 2, "well... If you don't need it it is ok");
		rateProduct("1", "1", 4, "great product");
		rateProduct("2", "0", 4, "I like this product");
		rateProduct("2", "1", 3, "what the hack is this????");
		rateProduct("2", "2", 5, "awsome!!!");
	}

	public Product createProduct(String title, String description, String url) {
		String key = "" + pid.getAndIncrement();
		Product p = new Product(key, title, description, url);
		products.put(key, p);
		return p;
	}

	public Customer createCustomer(String name) {
		String key = "" + cid.getAndIncrement();
		Customer c = new Customer(key, name);
		customers.put(key, c);
		return c;
	}

	public Optional<Customer> getCustomerById(String id) {
		return Optional.ofNullable(customers.get(id));
	}

	public Collection<Product> getAllProducts() {
		return products.values();
	}
	
	public Optional<Product> getProductById(String id) {
		return Optional.ofNullable(products.get(id));
	}
	
	public Product getRandomProduct() {
		int id = (int)(Math.random() * pid.get());
		return products.get("" + id);
	}

	public Rating rateProduct(String productId, String customerId, int score, String comment) {
		Product product = getProductById(productId).get();
		Customer customer = getCustomerById(customerId).get();
		Rating rating = new Rating(UUID.randomUUID().toString(), product, customer, score, comment); 
		
		if(cRatings.get(customer) == null) cRatings.put(customer, new ArrayList<>());
		if(pRatings.get(product) == null) pRatings.put(product, new ArrayList<>());
		cRatings.get(customer).add(rating);
		pRatings.get(product).add(rating);
		return rating;
	}

	public List<Rating> getRatingsForCustomer(Customer c) {
		return cRatings.getOrDefault(c, new ArrayList<Rating>());
	}

	public List<Rating> getRatingsForProduct(Product p) {
		return pRatings.getOrDefault(p, new ArrayList<Rating>());
	}

	public Product updateProduct(Product p1, String title, String description, String imageUrl) {
		Product p2 = new Product(p1.id(), title, description, imageUrl);
		if(p1.equals(p2)) {
			return p1;
		}
		pRatings.put(p2, new ArrayList<>());
		for(Rating r : pRatings.get(p1)) {
			Rating r2 = new Rating(r.id(), p2, r.customer(), r.score(), r.comment());
			cRatings.get(r.customer()).remove(r);
			cRatings.get(r.customer()).add(r2);
			pRatings.get(p2).add(r2);
		}
		pRatings.remove(p1);
		products.put(p1.id(), p2);
		return p2;
	}

}
