#!/bin/sh
DIR=$(dirname $0)
set -e
mvn clean prepare-package -f ${DIR} --no-transfer-progress
RESPONSE=$(echo '{"url":"/100", "params":{"id": "100"}}' | wasmtime ${DIR}/target/generated/wasm/teavm-wasm/[id].wasm)
if [ "${RESPONSE}" = '{"data":"Hey! The parameter is: 100","status":200,"base64":false,"headers":{"X-Generated-By":"wasm-workers-server"},"kv":{}}' ]; then
  echo "SUCCESS: ${RESPONSE}"
else
  echo "FAILURE: ${RESPONSE}"
  exit 1
fi
