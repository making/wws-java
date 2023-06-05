# Hello WASM (Java)

https://github.com/fermyon/teavm-wasi

```
./mvnw clean package
```

```
$ echo '{"body":"WASM"}' | wasmtime target/generated/wasm/teavm-wasm/classes.wasm
{"data":"Hello WASM!"}
```