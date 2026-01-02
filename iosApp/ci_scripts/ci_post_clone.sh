#!/bin/bash
set -e

echo "=============================="
echo " Xcode Cloud post-clone setup "
echo "=============================="

echo ""
echo "📍 Environment info:"
echo "CI_WORKSPACE_PATH=${CI_WORKSPACE_PATH}"
echo "CI_PRIMARY_REPOSITORY_PATH=${CI_PRIMARY_REPOSITORY_PATH}"
echo "CI_DERIVED_DATA_PATH=${CI_DERIVED_DATA_PATH}"
echo "Machine architecture: $(uname -m)"
echo ""

echo "🔎 Checking for Java..."
if command -v java >/dev/null 2>&1; then
  echo "✅ Java already available:"
  java -version
else
  echo "❌ Java not found"
  echo "➡️ Installing Temurin JDK 17 via Homebrew..."

  brew update
  brew install temurin@17
fi

echo ""
echo "🔧 Resolving JAVA_HOME..."
JAVA_HOME_PATH=$(/usr/libexec/java_home -v 17 2>/dev/null || true)

if [ -z "$JAVA_HOME_PATH" ]; then
  echo "❌ ERROR: Unable to resolve JAVA_HOME for Java 17"
  echo "Available JVMs:"
  /usr/libexec/java_home -V || true
  exit 1
fi

export JAVA_HOME="$JAVA_HOME_PATH"
export PATH="$JAVA_HOME/bin:$PATH"

echo "✅ JAVA_HOME set to:"
echo "   $JAVA_HOME"

echo ""
echo "🔎 Verifying Java execution:"
which java || true
java -version || true

echo ""
echo "✅ Java environment ready"

echo "=============================="
echo " End post-clone setup "
echo "=============================="
