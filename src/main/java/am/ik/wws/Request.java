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

import java.net.URI;
import java.util.Collections;
import java.util.Map;

import am.ik.json.JsonObject;

public class Request {

	public enum Method {

		GET, POST, PUT, DELETE, PATCH, HEAD, OPTIONS

	}

	private final URI url;

	private final Method method;

	private final Map<String, String> headers;

	private final String body;

	private final Map<String, String> routeParams;

	public Request(URI url, Method method, Map<String, String> headers, String body, Map<String, String> routeParams) {
		this.url = url;
		this.method = method;
		this.headers = headers;
		this.body = body;
		this.routeParams = routeParams;
	}

	public static Request fromJson(JsonObject json) {
		final JsonObject headers = json.get("headers").asObject();
		final JsonObject params = json.get("params").asObject();
		final String url = json.get("url").asString();
		final String method = json.get("method").asString();
		return new Request(URI.create(url == null ? "" : url), method == null ? null : Method.valueOf(method),
				Collections.unmodifiableMap(Worker.objectToMap(headers)), json.get("body").asString(),
				Collections.unmodifiableMap(Worker.objectToMap(params)));
	}

	public URI url() {
		return this.url;
	}

	public Method method() {
		return this.method;
	}

	public Map<String, String> headers() {
		return this.headers;
	}

	public String header(String name) {
		return this.headers.get(name);
	}

	public String body() {
		return this.body;
	}

	public Map<String, String> routeParams() {
		return this.routeParams;
	}

	public String routeParam(String name) {
		return this.routeParams.get(name);
	}

	@Override
	public String toString() {
		return "Request{" + "url='" + url + '\'' + ", method='" + method + '\'' + ", headers=" + headers + ", body='"
				+ body + '\'' + ", params=" + routeParams + '}';
	}

}
