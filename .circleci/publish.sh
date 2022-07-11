#!/bin/bash

if [ -z "$CIRCLE_TAG" ]; then
    echo "Only tagged commits are published"
else
    if [ -z "$GPGKEYURI" ]; then
        echo "Environment not configured for publishing"
    else
        curl -o secret.gpg $GPGKEYURI
        ./gradlew -PsonatypeUsername="$SONATYPEUSER" -PsonatypePassword="$SONATYPEPASS" \
                  -Psigning.keyId="$SIGNKEY" -Psigning.password="$SIGNPSW" \
                  -Psigning.secretKeyRingFile=./secret.gpg \
                  publish
        rm -f secret.gpg
    fi
fi
