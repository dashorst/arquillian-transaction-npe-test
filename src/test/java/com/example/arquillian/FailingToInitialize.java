package com.example.arquillian;

import javax.enterprise.context.Dependent;

@Dependent
public class FailingToInitialize {
	public FailingToInitialize() {
		throw new RuntimeException("Failed to initialize");
	}
}
