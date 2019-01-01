find . -type f | xargs -Ix sed -i.bak -r 's/\r//g' x
