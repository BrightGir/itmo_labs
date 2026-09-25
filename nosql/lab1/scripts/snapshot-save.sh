#!/usr/bin/env bash
set -euo pipefail

usage() {
    echo "Usage: $0 [snapshot.db]"
}

if [[ "${1:-}" == "--help" || "${1:-}" == "-h" ]]; then
    usage
    exit 0
fi

engine="${CONTAINER_ENGINE:-podman}"
container="${ETCD_CONTAINER:-library-etcd}"
snapshot_path="${1:-library-snapshot.db}"
container_snapshot="/tmp/library-snapshot-$$.db"

mkdir -p "$(dirname "$snapshot_path")"

"$engine" exec "$container" \
    etcdctl --endpoints=http://127.0.0.1:2379 snapshot save "$container_snapshot"
"$engine" cp "$container:$container_snapshot" "$snapshot_path"
"$engine" exec "$container" rm -f "$container_snapshot"

echo "Snapshot saved to $snapshot_path"
