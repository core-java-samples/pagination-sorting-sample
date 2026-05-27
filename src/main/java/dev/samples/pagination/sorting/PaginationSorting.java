package dev.samples.pagination.sorting;

import java.util.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// ======================================================
// DOMAIN
// ======================================================
record Customer(long id, String name) {}

// ======================================================
// PAGINATION RESULT
// ======================================================
record Page<T>(List<T> content, int page, int size, long total) {}

// ======================================================
// PAGE REQUEST + SORTING
// ======================================================
record Pageable(int page, int size, Comparator<Customer> sort) {

	int offset() { return page * size;}
}

// ======================================================
// REPOSITORY
// ======================================================
class CustomerRepository {

	Page<Customer> findAll(Pageable pageable) {

		// копируем данные чтобы можно было сортировать
		List<Customer> all =
				new ArrayList<>(FakeCustomerTable.DATA);

		// сортировка ДО пагинации
		all.sort(pageable.sort());

		int start = pageable.offset();
		int end = Math.min(start + pageable.size(), all.size());

		List<Customer> content;

		if (start >= all.size()) {
			content = List.of();
		} else {
			content = all.subList(start, end);
		}

		return new Page<>(
				content,
				pageable.page(),
				pageable.size(),
				all.size()
		);
	}
}

// ======================================================
// FAKE DATABASE
// ======================================================
class FakeCustomerTable {

	static final List<Customer> DATA = List.of(
			new Customer(3, "Charlie"),
			new Customer(1, "Alice"),
			new Customer(5, "Eve"),
			new Customer(2, "Bob"),
			new Customer(4, "Diana")
	);
}

// ======================================================
// MAIN
// ======================================================
@SpringBootApplication
public class PaginationSorting {
	public static void main(String[] args) {
		SpringApplication.run(PaginationSorting.class, args);
		new PaginationSorting().demo();
	}
	void demo() {
		CustomerRepository repo = new CustomerRepository();

		// сортировка по имени
		Pageable byName =
				new Pageable(0, 3, Comparator.comparing(Customer::name));

		// сортировка по id DESC
		Pageable byIdDesc =
				new Pageable(0, 3, Comparator.comparing(Customer::id).reversed());

		System.out.println(repo.findAll(byName));
		System.out.println();
		System.out.println(repo.findAll(byIdDesc));
	}
}
