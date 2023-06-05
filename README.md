# www-java

Unofficial Java SDK for [Wasm Workers Server](https://workers.wasmlabs.dev) works with [teavm-wasi](https://github.com/fermyon/teavm-wasi)

```xml
<dependency>
	<groupId>am.ik.wws</groupId>
	<artifactId>wws-java</artifactId>
	<version>0.1.2</version>
</dependency>
```

> ⚠️ `optimizationLevel` of the teavm-maven-plugin must be `SIMPLE`

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

## Run your worker with `wws`

```
git clone https://github.com/making/wws-java
./wws-java/samples/hello-wasm/run.sh
./wws-java/samples/key-value/run.sh
./wws-java/samples/dynamic-routing/run.sh

mkdir -p worker/app/hello
cp wws-java/samples/hello-wasm/target/generated/wasm/teavm-wasm/hello.wasm worker/app/index.wasm
cp wws-java/samples/key-value/target/generated/wasm/teavm-wasm/key-value.wasm worker/app
cp wws-java/samples/dynamic-routing/target/generated/wasm/teavm-wasm/\[id\].wasm worker/app/hello

cat <<EOF > worker/app/key-value.toml
name = "key-value"
version = "1"

[data]
[data.kv]
namespace = "key-value"
EOF

cd worker
wws app
```

```
⚙️  Loading routes from: app
⏳ Loading workers from 3 routes...
✅ Workers loaded in 143.659922ms.
    - http://127.0.0.1:8080/
      => app/index.wasm
    - http://127.0.0.1:8080/key-value
      => app/key-value.wasm
    - http://127.0.0.1:8080/hello/[id]
      => app/hello/[id].wasm
🚀 Start serving requests at http://127.0.0.1:8080
```


```
$ curl http://127.0.0.1:8080
Hello wasm! 
$ curl http://127.0.0.1:8080/key-value
Counter: 1 
$ curl http://127.0.0.1:8080/key-value
Counter: 2
$ curl http://127.0.0.1:8080/hello/100
Hey! The parameter is: 100
$ curl http://127.0.0.1:8080/hello/200
Hey! The parameter is: 200
```

## License

Licensed under the Apache License, Version 2.0.