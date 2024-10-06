#!/bin/bash

set -e

current_version=$(cat ./build/version.txt)

new_version=""

case $1 in
  major)
    new_version=$(echo $current_version | awk -F. '{print $1+1".0.0"}')
    ;;
  minor)
    new_version=$(echo $current_version | awk -F. '{print $1"."$2+1".0"}')
    ;;
  patch)
    new_version=$(echo $current_version | awk -F. '{print $1"."$2"."$3+1}')
    ;;
  *)
    echo "Invalid argument. Use 'major', 'minor', or 'patch'."
    exit 1
    ;;
esac

echo $new_version > ./build/version.txt
