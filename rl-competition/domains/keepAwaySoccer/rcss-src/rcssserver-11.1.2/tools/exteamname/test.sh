#!/bin/bash

gameinfo=`exteamname record.log`
team1=`echo $gameinfo | cut -d' ' -f1`
score1=`echo $gameinfo | cut -d' ' -f2`
team2=`echo $gameinfo | cut -d' ' -f3`
score2=`echo $gameinfo | cut -d' ' -f4`

echo "$team1 $score1"
echo "$team2 $score2"
