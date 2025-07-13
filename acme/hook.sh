#!/usr/bin/env bash

deploy_challenge() {
  local DOMAIN="${1}" TOKEN_FILENAME="${2}" TOKEN_VALUE="${3}"

  echo -n "** booting up a tiny http server..."
  nohup bash -c "cd www_root && sudo python -m SimpleHTTPServer 80" > /dev/null 2>&1 &
  echo "done"
}

clean_challenge() {
  local DOMAIN="${1}" TOKEN_FILENAME="${2}" TOKEN_VALUE="${3}"

  echo -n "** cleaning up the tiny http server..."
  for i in {1..10}; do
    PIDS=$(ps aux | grep "[S]impleHTTPServer" | awk '{print $2}');
    if [ -z "$PIDS" ]; then
      echo "done"
      break
    fi
    for PID in $PIDS; do
      sudo kill $PID
    done
    sleep 3
  done
}

sync_cert() {
  local KEYFILE="${1}" CERTFILE="${2}" FULLCHAINFILE="${3}" CHAINFILE="${4}" REQUESTFILE="${5}"

  # This hook is called after the certificates have been created but before
  # they are symlinked. This allows you to sync the files to disk to prevent
  # creating a symlink to empty files on unexpected system crashes.
}

deploy_cert() {
  local DOMAIN="${1}" KEYFILE="${2}" CERTFILE="${3}" FULLCHAINFILE="${4}" CHAINFILE="${5}" TIMESTAMP="${6}"

  echo "** deploying new certificates..."
  cp ${FULLCHAINFILE} ${PACKETPROXYHUB_DIR}/web-client/etc_nginx_certs/server_and_ca.crt
  cp ${KEYFILE} ${PACKETPROXYHUB_DIR}/web-client/etc_nginx_certs/server.key
  openssl pkcs12 -export -in ${FULLCHAINFILE} -inkey ${KEYFILE} -name jetty -passout pass:${ACME_PASSWORD} > ${PACKETPROXYHUB_DIR}/api-server/etc_packetproxyhub/certs.p12
  (cd ${PACKETPROXYHUB_DIR}; docker-compose restart)
}

deploy_ocsp() {
  local DOMAIN="${1}" OCSPFILE="${2}" TIMESTAMP="${3}"

  # This hook is called once for each updated ocsp stapling file that has
  # been produced. Here you might, for instance, copy your new ocsp stapling
  # files to service-specific locations and reload the service.
}


unchanged_cert() {
  local DOMAIN="${1}" KEYFILE="${2}" CERTFILE="${3}" FULLCHAINFILE="${4}" CHAINFILE="${5}"

  # This hook is called once for each certificate that is still
  # valid and therefore wasn't reissued.
}

invalid_challenge() {
  local DOMAIN="${1}" RESPONSE="${2}"

  # This hook is called if the challenge response has failed, so domain
  # owners can be aware and act accordingly.
}

request_failure() {
  local STATUSCODE="${1}" REASON="${2}" REQTYPE="${3}" HEADERS="${4}"

  # This hook is called when an HTTP request fails (e.g., when the ACME
  # server is busy, returns an error, etc). It will be called upon any
  # response code that does not start with '2'. Useful to alert admins
  # about problems with requests.
}

generate_csr() {
  local DOMAIN="${1}" CERTDIR="${2}" ALTNAMES="${3}"

  # This hook is called before any certificate signing operation takes place.
  # It can be used to generate or fetch a certificate signing request with external
  # tools.
  # The output should be just the certificate signing request formatted as PEM.
}

startup_hook() {
  # This hook is called before the cron command to do some initial tasks
  # (e.g. starting a webserver).

  :
}

exit_hook() {
  local ERROR="${1:-}"

  # This hook is called at the end of the cron command and can be used to
  # do some final (cleanup or other) tasks.
}

HANDLER="$1"; shift
if [[ "${HANDLER}" =~ ^(deploy_challenge|clean_challenge|sync_cert|deploy_cert|deploy_ocsp|unchanged_cert|invalid_challenge|request_failure|generate_csr|startup_hook|exit_hook)$ ]]; then
  "$HANDLER" "$@"
fi
