#!/bin/bash
set -e

echo "=============================="
echo " Xcode Cloud post-clone setup "
echo "=============================="

echo ""
echo "📍 Environment info:"
echo "CI_WORKSPACE_PATH=$CI_WORKSPACE_PATH"
echo "CI_PRIMARY_REPOSITORY_PATH=$CI_PRIMARY_REPOSITORY_PATH"
echo "CI_DERIVED_DATA_PATH=$CI_DERIVED_DATA_PATH"
echo "Machine architecture: $(uname -m)"
echo ""

echo "🔎 Resolving Java 17..."

JAVA_HOME_PATH=""

if /usr/libexec/java_home -v 17 >/dev/null 2>&1; then
  JAVA_HOME_PATH=$(/usr/libexec/java_home -v 17)
  echo "✅ Java 17 already installed at:"
  echo "   $JAVA_HOME_PATH"
else
  echo "⚠️ Java 17 not found — installing Temurin 17 via Homebrew"

  brew update
  brew install temurin@17

  JAVA_HOME_PATH=$(/usr/libexec/java_home -v 17)
  echo "✅ Installed Java at:"
  echo "   $JAVA_HOME_PATH"
fi

export JAVA_HOME="$JAVA_HOME_PATH"
export PATH="$JAVA_HOME/bin:$PATH"

echo ""
echo "🔍 Java verification:"
"$JAVA_HOME/bin/java" -version

echo ""
echo "✅ Java environment ready"
echo "=============================="
