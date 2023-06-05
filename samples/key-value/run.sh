#!/bin/sh
DIR=$(dirname $0)
set -e
mvn clean prepare-package -f ${DIR} --no-transfer-progress
RESPONSE=$(echo '{"url":"/key-value"}' | wasmtime ${DIR}/target/generated/wasm/teavm-wasm/key-value.wasm)
if [ "${RESPONSE}" = '{"data":"Counter: 1","status":200,"base64":false,"headers":{"X-Generated-By":"wasm-workers-server"},"kv":{"counter":"1"}}' ]; then
  echo "SUCCESS: ${RESPONSE}"
else
  echo "FAILURE: ${RESPONSE}"
  exit 1
fi

RESPONSE=$(echo '{"url":"/key-value","kv":{"counter":"1"}}' | wasmtime ${DIR}/target/generated/wasm/teavm-wasm/key-value.wasm)
if [ "${RESPONSE}" = '{"data":"Counter: 2","status":200,"base64":false,"headers":{"X-Generated-By":"wasm-workers-server"},"kv":{"counter":"2"}}' ]; then
  echo "SUCCESS: ${RESPONSE}"
else
  echo "FAILURE: ${RESPONSE}"
  exit 1
fi