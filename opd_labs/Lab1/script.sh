cd
dos2unix script.sh

echo run task 1
cd
mkdir lab0
cd lab0
mkdir litwick4
cd litwick4
mkdir zigzagoon
touch panpour
echo Способности Torrent Gluttony Inner Focus > panpour
touch tirtouga
echo satk=5 > tirtouga
echo sdef=5 spd=2 >> tirtouga
mkdir squirtle
mkdir staraptor
mkdir hitmonchan
cd ..
touch mandibuzz6
echo weigth=87.1 height=47.0 atk=7 > mandibuzz6
echo def=11 >> mandibuzz6
touch pidgey7
echo Развитые спобности Big Pecks > pidgey7
touch teddiursa8
echo Ходы Body > teddiursa8
echo Slam Covet Defense Curl Dynamicpunch Fake Tears File Punch Focus >> teddiursa8
echo Punch Furry Cutter Gunk Shot Hyper Voice Ice Punch Last Resort Mega >> teddiursa8
echo Kick Mega Punch Metronome Mud-Slap Rollout Seed Bomb Sleep Talk Snore >> teddiursa8
echo Superpower Swift Thunderpunch >> teddiursa8
mkdir togetic8
cd togetic8
mkdir hitmonchan
touch pignite
echo Живет Cave Grassland > pignite
echo Mountain >> pignite
mkdir kricketune
touch shinx
echo Способности Overcharge Intimidate > shinx
echo Rivalry >> shinx
cd ..
mkdir weavile9
cd weavile9
mkdir lillipup
touch poochyena
echo Тип покемона DARK NONE > poochyena
mkdir gastrodon
mkdir solosis
touch accelgor
echo Развитые > accelgor
echo Способности Unburden >> accelgor
echo task1 completed

echo run task 2
cd 
cd lab0
#2.1
chmod 311 litwick4
cd litwick4
#2.2
chmod 307 zigzagoon
#2.3
chmod u-w,g-rw panpour
#2.4
chmod u-rw,g-rw,o+w tirtouga
#2.5
chmod ug-r,o+w squirtle
#2.6
chmod ug-r,o+w staraptor
#2.7
chmod u-w,g-rx,o-x hitmonchan
cd ..
#2.8
chmod ug-w,o-r mandibuzz6
#2.9
chmod ug-w,g-r pidgey7
#2.10
chmod 400 teddiursa8
#2.11
chmod g-r,o+w togetic8
cd togetic8 
#2.12
chmod 357 hitmonchan
#2.13
chmod u=r,g=,o=r pignite
#2.14
chmod u=rwx,g=rx,o=r kricketune
#2.15
chmod u=rw,go= shinx
cd ..
#2.16
chmod 550 weavile9
cd weavile9 
#2.17
chmod u=rx,go=rwx lillipup
#2.18
chmod uo=r,g= poochyena
#2.19
chmod 700 gastrodon
#2.20
chmod 357 solosis
#2.21
chmod u=r,g=r,o= accelgor
echo task2 completed

echo run task3
cd 
cd lab0
chmod 777 togetic8
chmod 777 pidgey7
chmod -R 777 litwick4
chmod 777 weavile9
chmod 777 weavile9/solosis
#3.1
ln -s togetic8 Copy_72
#3.2
cat togetic8/shinx weavile9/accelgor > pidgey7_65
#3.3
cp pidgey7 togetic8/shinxpidgey
cd weavile9
#3.4
ln -s ~/lab0/pidgey7 poochyenapidgey #допилить (ссылка битая)
cd ..
#3.5
cp mandibuzz6 litwick4/zigzagoon
#3.6
cp -r weavile9 litwick4/hitmonchan
#3.7
ln teddiursa8 weavile9/accelgorteddiursa
chmod -R 777 litwick4
echo task3 completed

echo run task4
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
ls -lR 2>&1 | grep '^-' | egrep '(^((\S+\s+)){8})l' | sort -rk 4 | tail -n 3
echo 4.5 
grep -lR '.*' | egrep '(\/s[^\/]*$)|(^s[^\/]*$)' | xargs wc -l | sort -r
echo 4.6
cd litwick4 
cat -b * 2>/dev/null | grep -iv 'pi' 
cd ..
echo task4 completed

echo run task5
echo 5
cd 
cd lab0
chmod 777 mandibuzz6
rm mandibuzz6
echo 5.1 COMPLETED
chmod 777 'weavile9/accelgor'
rm 'weavile9/accelgor'
echo 5.2 COMPLETED
ls -l weavile9/poochyenapidg* | grep '^l' | grep -o 'weavile9/poochyenapidg[^ ]*' | xargs rm
echo 5.3 COMPLETED
ls -lRd $(pwd)/*/** 2>/dev/null | grep '^-' | egrep '^.{10}\s1(\s+\S){7}' | egrep -o '(\/.*\/accelgorteddiurs.*$)' | xargs -I % echo 'chmod 777 "%"; rm "%"' | sh
echo 5.4 COMPLETED
rm -r litwick4
echo 5.5 COMPLETED
rm -r weavile9/solosis
echo 5.6 COMPLETED
echo task5 completed