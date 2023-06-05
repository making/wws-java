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

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import am.ik.json.JsonObject;

public class Response {

	private final String data;

	private final Map<String, String> headers;

	private final int status;

	private final boolean base64;

	public static Builder status(int status) {
		return new Builder(status);
	}

	public Response(String data, Map<String, String> headers, int status, boolean base64) {
		this.data = data == null ? "" : data;
		this.headers = headers == null ? Collections.emptyMap() : headers;
		this.status = status;
		this.base64 = base64;
	}

	private JsonObject toJsonPartial() {
		return new JsonObject().put("data", this.data)
			.put("status", this.status)
			.put("base64", this.base64)
			.put("headers", Worker.mapToObject(this.headers));
	}

	public JsonObject toJson() {
		return this.toJsonPartial().put("kv", new JsonObject());
	}

	public JsonObject toJson(Cache cache) {
		return this.toJsonPartial().put("kv", cache.toJson());
	}

	@Override
	public String toString() {
		return "Response{" + "data='" + data + '\'' + ", headers=" + headers + ", status=" + status + ", base64="
				+ base64 + '}';
	}

	public static class Builder {

		private String data;

		private Map<String, String> headers;

		private final int status;

		private boolean base64;

		public Builder(int status) {
			this.status = status;
		}

		public Builder data(String data) {
			this.data = data;
			return this;
		}

		public Builder data(byte[] data) {
			// TODO base64 encode when data is not UTF-8
			return this.data(new String(data, StandardCharsets.UTF_8));
		}

		public Builder headers(Map<String, String> headers) {
			this.headers = headers;
			return this;
		}

		public Builder header(String name, String value) {
			if (this.headers == null) {
				this.headers = new LinkedHashMap<>();
			}
			this.headers.put(name, value);
			return this;
		}

		public Builder base64(boolean base64) {
			this.base64 = base64;
			return this;
		}

		public Response build() {
			return new Response(this.data, this.headers, this.status, this.base64);
		}

	}

}
