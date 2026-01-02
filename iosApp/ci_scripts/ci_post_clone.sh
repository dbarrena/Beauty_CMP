#!/bin/sh
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

############################################
# Locate Java using macOS java_home
############################################
echo "🔎 Looking for Java using /usr/libexec/java_home ..."

JAVA_HOME_PATH=$(/usr/libexec/java_home 2>/dev/null || true)

if [ -z "$JAVA_HOME_PATH" ]; then
  echo "❌ ERROR: No Java runtime found via /usr/libexec/java_home"
  echo "Xcode Cloud image does not expose a JDK."
  exit 1
fi

echo "✅ Java found at:"
echo "$JAVA_HOME_PATH"

export JAVA_HOME="$JAVA_HOME_PATH"
export PATH="$JAVA_HOME/bin:$PATH"

############################################
# Verify Java
############################################
echo ""
echo "☕ Java version check:"
java -version || {
  echo "❌ Java exists but cannot be executed"
  exit 1
}
echo ""
echo "✅ Post-clone setup completed successfully"
