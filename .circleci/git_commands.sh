git checkout eyal/com-235-api-services-versioning-management
git pull origin eyal/com-235-api-services-versioning-management
service_name="$1"
service_version="$2"
branch_name="$service_name-$service_version"
git merge --no-edit "$branch_name"
git commit --amend -m "[skip ci]"
git push --set-upstream origin eyal/com-235-api-services-versioning-management
git push --delete origin "$branch_name"