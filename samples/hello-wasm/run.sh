#!/bin/sh
DIR=$(dirname $0)
set -e
mvn clean prepare-package -f ${DIR} --no-transfer-progress
RESPONSE=$(echo '{"url":"/hello"}' | wasmtime ${DIR}/target/generated/wasm/teavm-wasm/hello.wasm)
if [ "${RESPONSE}" = '{"data":"Hello wasm!","status":200,"base64":false,"headers":{"X-Generated-By":"wasm-workers-server"},"kv":{}}' ]; then
  echo "SUCCESS: ${RESPONSE}"
else
  echo "FAILURE: ${RESPONSE}"
  exit 1
fi
