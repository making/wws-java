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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiFunction;
import java.util.function.Function;

import am.ik.json.Json;
import am.ik.json.JsonNode;
import am.ik.json.JsonObject;

public class Worker {

	private final InputStream in;

	private final PrintStream out;

	public Worker(InputStream in, PrintStream out) {
		this.in = in;
		this.out = out;
	}

	public static void serve(Function<Request, Response> handler) {
		final Worker worker = new Worker(System.in, System.out);
		worker.doServe(handler);
	}

	public static void serve(BiFunction<Request, Cache, Response> handler) {
		final Worker worker = new Worker(System.in, System.out);
		worker.doServe(handler);
	}

	public void doServe(Function<Request, Response> handler) {
		final JsonObject json = Json.parse(copyToString(this.in)).asObject();
		final Request request = Request.fromJson(json);
		final Response response = handler.apply(request);
		this.out.println(Json.stringify(response.toJson()));
	}

	public void doServe(BiFunction<Request, Cache, Response> handler) {
		final JsonObject json = Json.parse(copyToString(this.in)).asObject();
		final Request request = Request.fromJson(json);
		final Cache cache = new Cache(objectToMap(json.get("kv").asObject()));
		final Response response = handler.apply(request, cache);
		this.out.println(Json.stringify(response.toJson(cache)));
	}

	private static String copyToString(InputStream in) {
		if (in == null) {
			return "";
		}
		final StringBuilder out = new StringBuilder();
		final InputStreamReader reader = new InputStreamReader(in, StandardCharsets.UTF_8);
		final char[] buffer = new char[256];
		int charsRead;
		try {
			while ((charsRead = reader.read(buffer)) != -1) {
				out.append(buffer, 0, charsRead);
			}
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
		return out.toString();
	}

	// utilities
	static Map<String, String> objectToMap(JsonObject object) {
		if (object == null) {
			return new LinkedHashMap<>();
		}
		final Map<String, String> map = new HashMap<>();
		for (Entry<String, JsonNode> entry : object.asMap().entrySet()) {
			String k = entry.getKey();
			JsonNode v = entry.getValue();
			map.put(k, v.asString());
		}
		return map;
	}

	static JsonObject mapToObject(Map<String, String> map) {
		if (map == null) {
			return new JsonObject();
		}
		final JsonObject object = new JsonObject();
		for (Entry<String, String> entry : map.entrySet()) {
			object.put(entry.getKey(), entry.getValue());
		}
		return object;
	}

}
