# wws-java

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

prerequisites

* Java 8+
* Maven
* [`wws`](https://workers.wasmlabs.dev/docs/get-started/quickstart)

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

## Deploy to fly.io (free tier)

Continuation of above work

```
cat <<EOF > Dockerfile
FROM ghcr.io/vmware-labs/wws:latest
COPY app/ /app/
EOF
```

```
cat <<EOF > fly.toml
[build]
dockerfile = "Dockerfile"

[[services]]
internal_port = 8080
protocol = "tcp"

[[services.ports]]
handlers = ["http"]
port = 80

[[services.ports]]
handlers = ["tls", "http"]
port = 443
EOF
```

```
APP=wws-${RANDOM}
flyctl apps create --name ${APP} --machines
flyctl deploy -a ${APP}
```

```
==> Verifying app config
--> Verified app config
==> Building image
Remote builder fly-builder-patient-smoke-2723 ready
==> Creating build context
--> Creating build context done
==> Building image with Docker
--> docker host: 20.10.12 linux x86_64
[+] Building 0.0s (0/1)                                                                                                                                                                                                                                                                            
[+] Building 1.6s (6/6) FINISHED                                                                                                                                                                                                                                                                   
 => [internal] load remote build context                                                                                                                                                                                                                                                      0.0s
 => copy /context /                                                                                                                                                                                                                                                                           0.1s
 => [internal] load metadata for ghcr.io/vmware-labs/wws:latest                                                                                                                                                                                                                               1.4s
 => CACHED [1/2] FROM ghcr.io/vmware-labs/wws:latest@sha256:7a6032f444be44a3245816ddb8985e0e16690515fb3716c81089da4341f82633                                                                                                                                                                  0.0s
 => [2/2] COPY app/ /app/                                                                                                                                                                                                                                                                     0.0s
 => exporting to image                                                                                                                                                                                                                                                                        0.0s
 => => exporting layers                                                                                                                                                                                                                                                                       0.0s
 => => writing image sha256:984eeba44c03a271478f632ae354a085cec3acc98ddf53a689250911987cb4e4                                                                                                                                                                                                  0.0s
 => => naming to registry.fly.io/wws:deployment-01H25J2NCP4NM8NH9X7MK3WM7K                                                                                                                                                                                                                    0.0s
--> Building image done
==> Pushing image to fly
The push refers to repository [registry.fly.io/wws]
29f44ef747c7: Pushed 
3a9a2049a37e: Layer already exists 
6664d441c951: Layer already exists 
deployment-01H25J2NCP4NM8NH9X7MK3WM7K: digest: sha256:35fadd4a1fb15b72f503d29e255aaf991b7ecd2eb7ccf353edac33c62ec478f4 size: 949
--> Pushing image done
image: registry.fly.io/wws:deployment-01H25J2NCP4NM8NH9X7MK3WM7K
image size: 35 MB
Deploying wws app with rolling strategy
  Machine 6e82d4d4a07198 [app] update finished: success
  Finished deploying
```

```
$ curl https://${APP}.fly.dev
Hello wasm! 
$ curl https://${APP}.fly.dev/key-value
Counter: 1 
$ curl https://${APP}.fly.dev/key-value
Counter: 2
$ curl https://${APP}.fly.dev/hello/100
Hey! The parameter is: 100
$ curl https://${APP}.fly.dev/hello/200
Hey! The parameter is: 200
```

## License

Licensed under the Apache License, Version 2.0.