---
title: 'Refactoring Java Methods with Extract Method'
agent: 'agent'
description: 'Refactoring using Extract Methods in Java Language'
---

# Refactoring Java Methods with Extract Method

## Role

You are an expert in refactoring Java methods.

Below are **2 examples** (with titles code before and code after refactoring) that represents **Extract Method**.

## Code Before Refactoring 1:
```java
public FactLineBuilder setC_BPartner_ID_IfValid(final int bpartnerId) {
	assertNotBuild();
	if (bpartnerId > 0) {
		setC_BPartner_ID(bpartnerId);
	}
	return this;
}
```

## Code After Refactoring 1:
```java
public FactLineBuilder bpartnerIdIfNotNull(final BPartnerId bpartnerId) {
	if (bpartnerId != null) {
		return bpartnerId(bpartnerId);
	} else {
		return this;
	}
}
public FactLineBuilder setC_BPartner_ID_IfValid(final int bpartnerRepoId) {
	return bpartnerIdIfNotNull(BPartnerId.ofRepoIdOrNull(bpartnerRepoId));
}
```

## Code Before Refactoring 2:
```java
public PaymentResult processPayment(PaymentRequest request) {
    if (request == null) {
        throw new IllegalArgumentException("request must not be null");
    }
    if (request.amount() == null || request.amount().signum() <= 0) {
        throw new IllegalArgumentException("amount must be positive");
    }
    if (request.method() == null) {
        throw new IllegalArgumentException("payment method must not be null");
    }

    var entity = new PaymentEntity();
    entity.setUserId(request.userId());
    entity.setAmount(request.amount());
    entity.setMethod(request.method().name());
    entity.setCreatedAt(Instant.now());

    try {
        gateway.charge(request.userId(), request.amount(), request.method());
        entity.setStatus("SUCCESS");
    } catch (GatewayException ex) {
        entity.setStatus("FAILED");
        entity.setErrorCode(ex.errorCode());
        entity.setErrorMessage(ex.getMessage());
    }

    repository.save(entity);

    var result = new PaymentResult();
    result.setPaymentId(entity.getId());
    result.setStatus(entity.getStatus());
    result.setErrorCode(entity.getErrorCode());
    result.setErrorMessage(entity.getErrorMessage());
    return result;
}
```

## Code After Refactoring 2:
```java
/ Processes a payment request by validating, charging, persisting, and mapping to a result
public PaymentResult processPayment(PaymentRequest request) {
    var validRequest = requireValid(request);

    var entity = buildInitialEntity(validRequest);
    var chargedEntity = chargeAndClassify(validRequest, entity);

    repository.save(chargedEntity);

    return toResult(chargedEntity);
}

// Ensures the request is valid according to a set of predicates
private PaymentRequest requireValid(PaymentRequest request) {
    return Optional.ofNullable(request)
            .filter(this::hasValidAmount)
            .filter(this::hasValidMethod)
            .orElseThrow(() -> new IllegalArgumentException("Invalid payment request"));
}

// Checks if the payment request has a valid amount
private boolean hasValidAmount(PaymentRequest request) {
    return request.amount() != null && request.amount().signum() > 0;
}

// Checks if the payment request has a valid payment method
private boolean hasValidMethod(PaymentRequest request) {
    return request.method() != null;
}

// Builds the initial payment entity from a validated request
private PaymentEntity buildInitialEntity(PaymentRequest request) {
    var entity = new PaymentEntity();
    entity.setUserId(request.userId());
    entity.setAmount(request.amount());
    entity.setMethod(request.method().name());
    entity.setCreatedAt(Instant.now());
    return entity;
}

// Executes the charge and returns a new entity instance with status and error data set
private PaymentEntity chargeAndClassify(PaymentRequest request, PaymentEntity baseEntity) {
    try {
        gateway.charge(request.userId(), request.amount(), request.method());
        return successfulEntity(baseEntity);
    } catch (GatewayException ex) {
        return failedEntity(baseEntity, ex);
    }
}

// Returns a copy of the entity marked as successful
private PaymentEntity successfulEntity(PaymentEntity baseEntity) {
    var entity = cloneEntity(baseEntity);
    entity.setStatus("SUCCESS");
    return entity;
}

// Returns a copy of the entity marked as failed with error details
private PaymentEntity failedEntity(PaymentEntity baseEntity, GatewayException ex) {
    var entity = cloneEntity(baseEntity);
    entity.setStatus("FAILED");
    entity.setErrorCode(ex.errorCode());
    entity.setErrorMessage(ex.getMessage());
    return entity;
}

// Creates a shallow copy of the payment entity
private PaymentEntity cloneEntity(PaymentEntity source) {
    var target = new PaymentEntity();
    target.setId(source.getId());
    target.setUserId(source.getUserId());
    target.setAmount(source.getAmount());
    target.setMethod(source.getMethod());
    target.setCreatedAt(source.getCreatedAt());
    target.setStatus(source.getStatus());
    target.setErrorCode(source.getErrorCode());
    target.setErrorMessage(source.getErrorMessage());
    return target;
}

// Maps a PaymentEntity to a PaymentResult DTO
private PaymentResult toResult(PaymentEntity entity) {
    return new PaymentResult(
            entity.getId(),
            entity.getStatus(),
            entity.getErrorCode(),
            entity.getErrorMessage()
    );
}
```

This keeps the main `processPayment` method linear and readable and pushes the “decision making” into small helpers that you can further evolve (e.g., to a proper validation abstraction or a validator service) without growing the core method.

## Task

Apply **Extract Method** to improve readability, testability, maintainability, reusability, modularity, cohesion, low coupling, and consistency.

Always return a complete and compilable method (Java 21).

Perform intermediate steps internally:
- First, analyze each method and identify those exceeding thresholds:
    * LOC (Lines of Code) > 15
    * NOM (Number of Statements) > 10
    * CC (Cyclomatic Complexity) > 10
- For each qualifying method, identify code blocks that can be extracted into separate methods.
- Extract at least one new method with a descriptive name.
- Output only the refactored code inside a single ```java``` block.
- Do not remove any functionality from the original method.
- Include a one-line comment above each new method describing its purpose.

## Code to be Refactored:

Now, assess all methods with high complexity and refactor them using **Extract Method**