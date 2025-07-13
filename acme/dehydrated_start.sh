#!/bin/sh

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
export PACKETPROXYHUB_DIR=${SCRIPT_DIR}/../

# edit the below settings
export ACME_PASSWORD=password
export ACME_DOMAIN=packetproxyhub.example.com
export ACME_CA=https://acme-v02.api.letsencrypt.org/directory

cd $SCRIPT_DIR
exec ./dehydrated -c -f config -d ${ACME_DOMAIN}

