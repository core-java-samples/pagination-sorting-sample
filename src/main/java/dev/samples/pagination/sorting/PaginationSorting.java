package dev.samples.pagination.sorting;

import java.util.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

record Owner(Long id, String name) {}

interface OwnerRepository {
	Page<Owner> findAll(Pageable<Owner> pageable);
}

record Pageable<T>(int page, int size, Comparator<T> sort) {}

record Page<T>(List<T> content, int page, int size, long total) {}

class FakeOwnerRepository implements OwnerRepository {

	private final Map<Long, Owner> owners = new LinkedHashMap<>();

	public FakeOwnerRepository() {
		owners.put(1L, new Owner(1L, "jack1"));
		owners.put(2L, new Owner(2L, "jack2"));
		owners.put(3L, new Owner(3L, "jack3"));
		owners.put(4L, new Owner(4L, "jack4"));
		owners.put(5L, new Owner(5L, "jack5"));
	}

	@Override
	public Page<Owner> findAll(Pageable pageable) {

		List<Owner> content;
		List<Owner> all = new ArrayList<>(owners.values());

		if (pageable.sort() != null) all.sort(pageable.sort());

		long total = all.size(); // <- Дополнительный запрос SQL JDBC

		long offset = pageable.page() * pageable.size(); // start
		long limit = Math.min(offset + pageable.size(), total); // end

		if (offset >= total) content = List.of();
		else content = List.copyOf(all.subList((int) offset, (int) limit));

		return new Page<>(content, pageable.page(), pageable.size(), total);
	}
}

@SpringBootApplication
public class PaginationSorting {
	public static void main(String[] args) {
		SpringApplication.run(PaginationSorting.class, args);
		new PaginationSorting().demo();
	}
	void demo() {
		OwnerRepository repository = new FakeOwnerRepository();

		System.out.println(
				repository.findAll(
						new Pageable<>(
								0, 2, Comparator.comparing(Owner::id).reversed()
						)
				)
		);

		System.out.println();

		System.out.println(repository.findAll(new Pageable<>(0, 2, null)));
	}
}
