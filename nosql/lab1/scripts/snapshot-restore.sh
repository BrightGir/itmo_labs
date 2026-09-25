#!/usr/bin/env bash
set -euo pipefail

usage() {
    echo "Usage: $0 snapshot.db"
}

if [[ "${1:-}" == "--help" || "${1:-}" == "-h" ]]; then
    usage
    exit 0
fi

if [[ $# -ne 1 ]]; then
    usage >&2
    exit 1
fi

snapshot_path="$1"
if [[ ! -f "$snapshot_path" ]]; then
    echo "Snapshot not found: $snapshot_path" >&2
    exit 1
fi

engine="${CONTAINER_ENGINE:-podman}"
volume="${ETCD_VOLUME:-library-etcd-data}"
image="${ETCD_IMAGE:-quay.io/coreos/etcd:v3.6.14}"
snapshot_dir="$(cd "$(dirname "$snapshot_path")" && pwd)"
snapshot_name="$(basename "$snapshot_path")"

"$engine" compose down
"$engine" volume rm -f "$volume"
"$engine" volume create "$volume" >/dev/null
"$engine" run --rm \
    --entrypoint=etcdutl \
    -v "$volume:/etcd-data" \
    -v "$snapshot_dir:/backup:ro" \
    "$image" snapshot restore "/backup/$snapshot_name" \
    --data-dir=/etcd-data \
    --name=node1 \
    --initial-cluster=node1=http://etcd:2380 \
    --initial-advertise-peer-urls=http://etcd:2380
"$engine" compose up -d

echo "Snapshot restored from $snapshot_path"
