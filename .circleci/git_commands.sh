#!/bin/bash

git checkout main
git pull origin main
service_name="$1"
service_version="$2"
branch_name="$service_name-$service_version"
git merge --no-edit "$branch_name"
git commit --amend -m "[skip ci]"
git push --set-upstream origin main
git push --delete origin "$branch_name"