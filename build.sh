#!/bin/bash

set -euo pipefail

PROJECT_ROOT="$(cd "$(dirname "$0")" && pwd)"
OUT_DIR="$PROJECT_ROOT/out"

echo "=========================================="
echo "Library Checkout System - Simple Build"
echo "=========================================="

rm -rf "$OUT_DIR"
mkdir -p "$OUT_DIR"

echo "Compiling sources..."
javac -d "$OUT_DIR" $(find "$PROJECT_ROOT/src/main/java" -name "*.java")

CLASS_COUNT=$(find "$OUT_DIR" -name "*.class" | wc -l | tr -d ' ')
echo "Build complete! $CLASS_COUNT class files created in $OUT_DIR"
echo ""
echo "Run the CLI version with:"
echo "  java -cp \"$OUT_DIR\" com.librarysystem.LibrarySystem"
echo ""
echo "Run the GUI version with:"
echo "  java -cp \"$OUT_DIR\" com.librarysystem.gui.LibraryGUI"
echo "=========================================="
