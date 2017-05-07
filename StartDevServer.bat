@echo off
copy /Y target\maro-craft-1.1.2-SNAPSHOT.jar S:\Server\Games\Minecraft\Develop\plugins\
S:
cd S:\Server\Games\Minecraft\Develop
java -jar craftbukkit-1.11.2.jar