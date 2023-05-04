## Generics outline

* `Box`, generic `Box`
* `Pair`, generic `Pair`
* Bounded types (`Box<T extends Number>`)
* `NaturalNumber<T extends Number>`
* Multiple bounds `Box<T extends A & B & C>`
* Using Comparable

* `Box<Integer>` and `Box<Double>` are not subtypes of `Box<Number>`.
* Wildcard types `?` — generally, little reason to use them