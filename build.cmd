@ echo off
cls
@ pushd src
C:\Java\jdk1.8.0_77\bin\javac.exe  ./ua/oschadbank/sender/*.java  -d ../build -g  -Xdiags:verbose -Xlint:unchecked -deprecation
@ popd
