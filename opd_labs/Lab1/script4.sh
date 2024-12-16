cd
dos2unix script4.sh


cd
cd lab0
echo 4.1
ls -lR '.*' 2>/dev/null | grep '^-'
grep -Rl '.*' 2>/dev/null | egrep '(\/h[^\/]*$)|(^h[^\/]*$)' | xargs wc -m | sort -n
echo 4.2
ls -lR 2>>errors.log | grep '^-' | egrep '(^(\S+\s+){8}p)' | sort -k 2
echo 4.3
grep -Rl '.*' 2>/tmp/erorrs.log | egrep '(\/p[^\/]*$)|(^p[^\/]*$)' | xargs cat | sort -r 
echo 4.4
ls -lR 2>&1 | grep -v 'permission denied' | grep '^-' | egrep '(^(\S+\s+)){8}l' | sort -rk 4 | tail -n 3
echo 4.5 
grep -lR '.*' | egrep '(\/s[^\/]*$)|(^s[^\/]*$)' | xargs wc -l | sort -r
echo 4.6
cd litwick4 
cat -b * 2>/dev/null | grep -iv 'pi'
cd ..