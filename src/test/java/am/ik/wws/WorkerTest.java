/*
 * Copyright (C) 2023 Toshiaki Maki <makingx@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package am.ik.wws;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;

import am.ik.wws.Request.Method;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class WorkerTest {

	@Test
	void serve_GET() {
		final String input = "{\"url\":\"\\/hello\",\"method\":\"GET\",\"headers\":{\"host\":\"127.0.0.1:8080\",\"user-agent\":\"curl\\/7.88.1\",\"accept\":\"*\\/*\"},\"body\":\"\",\"kv\":{},\"params\":{}}";
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		final Worker worker = new Worker(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)),
				new PrintStream(out));
		worker.doServe(req -> {
			assertThat(req.url()).isEqualTo(URI.create("/hello"));
			assertThat(req.method()).isEqualTo(Method.GET);
			assertThat(req.headers()).containsOnlyKeys("host", "user-agent", "accept");
			assertThat(req.header("host")).isEqualTo("127.0.0.1:8080");
			assertThat(req.header("user-agent")).isEqualTo("curl/7.88.1");
			assertThat(req.header("accept")).isEqualTo("*/*");
			assertThat(req.body()).isEmpty();
			assertThat(req.routeParams()).isEmpty();
			return Response.status(200).header("x-generated-by", "wasm-workers-server").data("Hello wasm!").build();
		});
		assertThat(out.toString()).isEqualTo(
				"{\"data\":\"Hello wasm!\",\"status\":200,\"base64\":false,\"headers\":{\"x-generated-by\":\"wasm-workers-server\"},\"kv\":{}}\n");
	}

	@Test
	void serve_POST() {
		final String input = "{\"url\":\"\\/hello\",\"method\":\"POST\",\"headers\":{\"host\":\"127.0.0.1:8080\",\"user-agent\":\"curl\\/7.88.1\",\"accept\":\"*\\/*\",\"content-type\":\"text/plain\"},\"body\":\"Hello!\",\"kv\":{},\"params\":{}}";
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		final Worker worker = new Worker(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)),
				new PrintStream(out));
		worker.doServe(req -> {
			assertThat(req.url()).isEqualTo(URI.create("/hello"));
			assertThat(req.method()).isEqualTo(Method.POST);
			assertThat(req.headers()).containsOnlyKeys("host", "user-agent", "accept", "content-type");
			assertThat(req.header("host")).isEqualTo("127.0.0.1:8080");
			assertThat(req.header("user-agent")).isEqualTo("curl/7.88.1");
			assertThat(req.header("accept")).isEqualTo("*/*");
			assertThat(req.header("content-type")).isEqualTo("text/plain");
			assertThat(req.body()).isEqualTo("Hello!");
			assertThat(req.routeParams()).isEmpty();
			return Response.status(200).header("x-generated-by", "wasm-workers-server").data("Hello wasm!").build();
		});
		assertThat(out.toString()).isEqualTo(
				"{\"data\":\"Hello wasm!\",\"status\":200,\"base64\":false,\"headers\":{\"x-generated-by\":\"wasm-workers-server\"},\"kv\":{}}\n");
	}

	@Test
	void serve_GET_params() {
		final String input = "{\"url\":\"\\/hello/100\",\"method\":\"GET\",\"headers\":{\"host\":\"127.0.0.1:8080\",\"user-agent\":\"curl\\/7.88.1\",\"accept\":\"*\\/*\"},\"body\":\"\",\"kv\":{},\"params\":{\"id\":\"100\"}}";
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		final Worker worker = new Worker(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)),
				new PrintStream(out));
		worker.doServe(req -> {
			assertThat(req.url()).isEqualTo(URI.create("/hello/100"));
			assertThat(req.method()).isEqualTo(Method.GET);
			assertThat(req.headers()).containsOnlyKeys("host", "user-agent", "accept");
			assertThat(req.header("host")).isEqualTo("127.0.0.1:8080");
			assertThat(req.header("user-agent")).isEqualTo("curl/7.88.1");
			assertThat(req.header("accept")).isEqualTo("*/*");
			assertThat(req.body()).isEmpty();
			assertThat(req.routeParam("id")).isEqualTo("100");
			return Response.status(200).header("x-generated-by", "wasm-workers-server").data("Hello wasm!").build();
		});
		assertThat(out.toString()).isEqualTo(
				"{\"data\":\"Hello wasm!\",\"status\":200,\"base64\":false,\"headers\":{\"x-generated-by\":\"wasm-workers-server\"},\"kv\":{}}\n");
	}

	@Test
	void serve_POST_cache_init() {
		final String input = "{\"url\":\"\\/counter\",\"method\":\"POST\",\"headers\":{\"host\":\"127.0.0.1:8080\",\"user-agent\":\"curl\\/7.88.1\",\"accept\":\"*\\/*\",\"content-type\":\"text/plain\"},\"body\":\"Hello!\",\"kv\":{},\"params\":{}}";
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		final Worker worker = new Worker(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)),
				new PrintStream(out));
		worker.doServe((req, cache) -> {
			int counter = cache.get("counter").map(Integer::parseInt).orElse(0);
			assertThat(req.url()).isEqualTo(URI.create("/counter"));
			assertThat(req.method()).isEqualTo(Method.POST);
			assertThat(req.headers()).containsOnlyKeys("host", "user-agent", "accept", "content-type");
			assertThat(req.header("host")).isEqualTo("127.0.0.1:8080");
			assertThat(req.header("user-agent")).isEqualTo("curl/7.88.1");
			assertThat(req.header("accept")).isEqualTo("*/*");
			assertThat(req.header("content-type")).isEqualTo("text/plain");
			assertThat(req.body()).isEqualTo("Hello!");
			assertThat(req.routeParams()).isEmpty();
			assertThat(counter).isEqualTo(0);
			cache.put("counter", String.valueOf(++counter));
			return Response.status(200).header("x-generated-by", "wasm-workers-server").data("Hello wasm!").build();
		});
		assertThat(out.toString()).isEqualTo(
				"{\"data\":\"Hello wasm!\",\"status\":200,\"base64\":false,\"headers\":{\"x-generated-by\":\"wasm-workers-server\"},\"kv\":{\"counter\":\"1\"}}\n");
	}

	@Test
	void serve_POST_cache_cont() {
		final String input = "{\"url\":\"\\/counter\",\"method\":\"POST\",\"headers\":{\"host\":\"127.0.0.1:8080\",\"user-agent\":\"curl\\/7.88.1\",\"accept\":\"*\\/*\",\"content-type\":\"text/plain\"},\"body\":\"Hello!\",\"kv\":{\"counter\":\"1\"},\"params\":{}}";
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		final Worker worker = new Worker(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)),
				new PrintStream(out));
		worker.doServe((req, cache) -> {
			int counter = cache.get("counter").map(Integer::parseInt).orElse(0);
			assertThat(req.url()).isEqualTo(URI.create("/counter"));
			assertThat(req.method()).isEqualTo(Method.POST);
			assertThat(req.headers()).containsOnlyKeys("host", "user-agent", "accept", "content-type");
			assertThat(req.header("host")).isEqualTo("127.0.0.1:8080");
			assertThat(req.header("user-agent")).isEqualTo("curl/7.88.1");
			assertThat(req.header("accept")).isEqualTo("*/*");
			assertThat(req.header("content-type")).isEqualTo("text/plain");
			assertThat(req.body()).isEqualTo("Hello!");
			assertThat(req.routeParams()).isEmpty();
			assertThat(counter).isEqualTo(1);
			cache.put("counter", String.valueOf(++counter));
			return Response.status(200).header("x-generated-by", "wasm-workers-server").data("Hello wasm!").build();
		});
		assertThat(out.toString()).isEqualTo(
				"{\"data\":\"Hello wasm!\",\"status\":200,\"base64\":false,\"headers\":{\"x-generated-by\":\"wasm-workers-server\"},\"kv\":{\"counter\":\"2\"}}\n");
	}

}