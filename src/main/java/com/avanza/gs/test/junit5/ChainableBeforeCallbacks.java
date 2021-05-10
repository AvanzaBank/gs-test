package com.avanza.gs.test.junit5;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

interface ChainableBeforeCallbacks extends BeforeAllCallback, BeforeEachCallback {

	default ChainableBeforeCallbacks andThen(ChainableBeforeCallbacks next) {
		return new Chain(this).andThen(next);
	}

	final class Chain implements ChainableBeforeCallbacks {

		private final List<ChainableBeforeCallbacks> chain;

		private Chain(List<ChainableBeforeCallbacks> chain) {
			this.chain = chain;
		}

		Chain(ChainableBeforeCallbacks first) {
			this(List.of(first));
		}

		@Override
		public void beforeAll(ExtensionContext context) throws Exception {
			for (BeforeAllCallback resourceExtension : chain) {
				resourceExtension.beforeAll(context);
			}
		}

		@Override
		public void beforeEach(ExtensionContext context) throws Exception {
			for (BeforeEachCallback resourceExtension : chain) {
				resourceExtension.beforeEach(context);
			}
		}

		@Override
		public ChainableBeforeCallbacks andThen(ChainableBeforeCallbacks next) {
			List<ChainableBeforeCallbacks> newOrder = new ArrayList<>(chain);
			if (next instanceof Chain) {
				newOrder.addAll(((Chain) next).chain);
			} else {
				newOrder.add(next);
			}
			return new Chain(newOrder);
		}

	}
}
