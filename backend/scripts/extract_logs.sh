#!/usr/bin/env bash
# Extract gzipped logs from the last three days into a target directory.
# Usage: ./extract_logs.sh [output_dir]
set -e
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
LOG_DIR="${SCRIPT_DIR}/../logs"
OUTPUT_DIR="${1:-${SCRIPT_DIR}/extracted}"

mkdir -p "$OUTPUT_DIR"
find "$LOG_DIR" -name 'application-*.log.gz' -mtime -3 -print \
  -exec sh -c 'gzip -dc "$1" > "$2/$(basename "$1" .gz)"' _ {} "$OUTPUT_DIR" \;
echo "Logs extracted to $OUTPUT_DIR"
