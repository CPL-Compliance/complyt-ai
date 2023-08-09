git checkout main
git pull origin main
git merge --no-edit "$1-$2"
git commit --amend -m "[skip ci]"
git push --set-upstream origin main
git push --delete origin "$1-$2"