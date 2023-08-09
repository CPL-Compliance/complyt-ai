git checkout eyal/com-235-api-services-versioning-management
git pull origin eyal/com-235-api-services-versioning-management
git merge --no-edit "$1-$2"
git commit --amend -m "[skip ci]"
git push --set-upstream origin eyal/com-235-api-services-versioning-management
git push --delete origin "$1-$2"