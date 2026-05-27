# Pagination with Sorting — Sample

A minimal demonstration of offset-based pagination with sorting in Java.

## What It Demonstrates

Extends the basic pagination sample with sorting support. Before slicing the dataset into pages, the repository sorts the full list by the comparator provided in the request — so the order of records is consistent across all pages.

The project illustrates this with a concrete example: a `CustomerRepository` that accepts a `Pageable` request with a `Comparator<Customer>` and returns a sorted `Page<Customer>`.

## Structure

The entire project intentionally fits in a single file — so the mechanics are visible in full, without unnecessary noise:

```
PaginationSorting.java
│
├── Customer                # Model — record with id and name
├── Page<T>                 # Pagination result — content + metadata (page, size, total)
├── Pageable                # Pagination request — page number, size and sort comparator
├── CustomerRepository      # Data access — sorts then paginates
├── FakeCustomerTable       # In-memory data source — simulates a database table
└── PaginationSorting       # Entry point + demo()
```

## Key Points

`Pageable` carries a `Comparator<Customer>` alongside page number and size — the sort order is part of the request.

Sorting happens before pagination. The full dataset is sorted first, then sliced — this ensures consistent ordering across all pages.

`CustomerRepository` copies the source list before sorting — the original data source is never mutated.

## Console Output

```
Page[content=[Customer[id=1, name=Alice], Customer[id=2, name=Bob], Customer[id=3, name=Charlie]], page=0, size=3, total=5]

Page[content=[Customer[id=5, name=Eve], Customer[id=4, name=Diana], Customer[id=3, name=Charlie]], page=0, size=3, total=5]
```

First result — sorted by name ascending. Second result — sorted by id descending.
