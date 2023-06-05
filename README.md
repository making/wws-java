# www-java

Unofficial Java SDK for [Wasm Workers Server](https://workers.wasmlabs.dev)

```xml
<dependency>
	<groupId>am.ik.wws</groupId>
	<artifactId>wws-java</artifactId>
	<version>0.1.0</version>
</dependency>
```

## Your first Java worker

Equivalent to https://workers.wasmlabs.dev/docs/languages/rust#your-first-rust-worker

```java
import am.ik.wws.Response;
import am.ik.wws.Worker;

public class Main {
	public static void main(String[] args) {
		Worker.serve(req -> Response.status(200)
				.header("X-Generated-By", "wasm-workers-server")
				.data("Hello wasm!")
				.build());
	}
}
```

See [here](./samples/hello-wasm) for the full source code

## Add a Key / Value store

https://workers.wasmlabs.dev/docs/features/key-value

Equivalent to https://workers.wasmlabs.dev/docs/languages/rust#add-a-key--value-store

```java
import am.ik.wws.Response;
import am.ik.wws.Worker;

public class Main {
	public static void main(String[] args) {
		Worker.serve((req, cache) -> {
			int counter = cache.get("counter").map(Integer::parseInt).orElse(0);
			cache.put("counter", String.valueOf(++counter));
			return Response.status(200)
					.header("X-Generated-By", "wasm-workers-server")
					.data("Counter: " + counter)
					.build();
		});
	}
}
```

See [here](./samples/key-value) for the full source code

## Dynamic routes

https://workers.wasmlabs.dev/docs/features/dynamic-routes

Equivalent to https://workers.wasmlabs.dev/docs/languages/rust#dynamic-routes

```java
import am.ik.wws.Response;
import am.ik.wws.Worker;

public class Main {
	public static void main(String[] args) {
		Worker.serve(req -> {
			String id = req.routeParam("id").orElse(null);
			return Response.status(200)
					.header("X-Generated-By", "wasm-workers-server")
					.data("Hey! The parameter is: " + id)
					.build();
		});
	}
}
```

See [here](./samples/dynamic-routing) for the full source code

### License

Licensed under the Apache License, Version 2.0.