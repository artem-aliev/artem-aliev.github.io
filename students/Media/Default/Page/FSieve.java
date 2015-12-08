package edu.spbgu;

import java.util.function.IntPredicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class FSieve {
	static class FPair {
		int p;
		IntPredicate filter;
		FPair(int i, IntPredicate f) {p=i; filter = f;}
	}
	
	public static void main(String[] args) {
		Stream<FPair> sieve = Stream.iterate( new FPair(2, x->x%2!=0), p -> { 
			final int next = IntStream.range(p.p, Integer.MAX_VALUE).filter(p.filter).findFirst().orElse(Integer.MAX_VALUE); 
			return new FPair(next, x->(x%next!=0 && p.filter.test(x)));
		});
		IntStream primes = sieve.mapToInt(x->x.p);
		primes.limit(100).forEach(System.out::println);
	}

}
