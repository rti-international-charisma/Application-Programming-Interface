#!/bin/sh
set -e
set -u

ssh-keygen -A
exec /usr/sbin/sshd -D -e "$@"