setlocal 
cd /d %~dp0
play dist && vmc push --path=dist\myfirstapp-1.0-SNAPSHOT.zip