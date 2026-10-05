#!/usr/bin/env bash
# Prints the next semantic version, computed from the conventional commits
# since the last vX.Y.Z tag. Prints nothing if there is no new commit.
#   breaking change ("type!:" or "BREAKING CHANGE") -> major
#   feat                                            -> minor
#   anything else                                   -> patch
set -euo pipefail

last=$(git describe --tags --abbrev=0 --match 'v[0-9]*' 2>/dev/null || true)
if [ -z "$last" ]; then
    range=HEAD
    base=0.0.0
else
    range="$last..HEAD"
    base=${last#v}
fi

if [ -z "$(git rev-list -n 1 "$range")" ]; then
    exit 0
fi

subjects=$(git log --format=%s "$range")
bodies=$(git log --format=%B "$range")

bump=patch
if grep -qE '^[a-z]+(\([^)]*\))?!:' <<<"$subjects" || grep -q 'BREAKING CHANGE' <<<"$bodies"; then
    bump=major
elif grep -qE '^feat(\([^)]*\))?:' <<<"$subjects"; then
    bump=minor
fi

IFS=. read -r major minor patch <<<"$base"
case "$bump" in
    major) major=$((major + 1)); minor=0; patch=0 ;;
    minor) minor=$((minor + 1)); patch=0 ;;
    patch) patch=$((patch + 1)) ;;
esac

echo "$major.$minor.$patch"
