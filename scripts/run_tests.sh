#!/bin/zsh
set -euo pipefail

PROJECT_ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$PROJECT_ROOT"

SRC_DIR="src"
TEST_DIR="test"
OUT_DIR="out"
TEST_OUT_DIR="out-test"
JUNIT_JAR="lib/junit-platform-console-standalone.jar"

rm -rf "$OUT_DIR" "$TEST_OUT_DIR"
mkdir -p "$OUT_DIR" "$TEST_OUT_DIR"

echo "[build] Compiling production sources..."
javac -d "$OUT_DIR" "$SRC_DIR"/*.java

echo "[build] Compiling test sources..."
find "$TEST_DIR" -name "*.java" > .test_sources
javac -cp "$OUT_DIR:$JUNIT_JAR" -d "$TEST_OUT_DIR" @.test_sources
rm .test_sources

echo "[test] Running JUnit suite..."
java -jar "$JUNIT_JAR" \
    --class-path "$OUT_DIR:$TEST_OUT_DIR" \
    --scan-classpath

echo "[done] Tests completed successfully."

