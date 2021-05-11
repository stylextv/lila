<h1 align="center">
  <br>
  <img src="https://raw.githubusercontent.com/StylexTV/Lila/main/imgs/cover.png">
  <br>
</h1>

<h4 align="center">♟️ Source code of the Lila Chess engine, made with ❤️ in Java.</h4>

<p align="center">
  <a href="https://GitHub.com/StylexTV/Lila/stargazers/">
    <img alt="stars" src="https://img.shields.io/github/stars/StylexTV/Lila.svg?color=ffdd00"/>
  </a>
  <a href="https://www.codacy.com/gh/StylexTV/Lila/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=StylexTV/Lila&amp;utm_campaign=Badge_Grade">
    <img alt="Codacy Badge" src="https://app.codacy.com/project/badge/Grade/fc5372689544422eb86e33876bbbed15"/>
  </a>
  <a>
    <img alt="Code size" src="https://img.shields.io/github/languages/code-size/StylexTV/Lila.svg"/>
  </a>
  <a>
    <img alt="GitHub repo size" src="https://img.shields.io/github/repo-size/StylexTV/Lila.svg"/>
  </a>
  <a>
    <img alt="Lines of Code" src="https://tokei.rs/b1/github/StylexTV/Lila?category=code"/>
  </a>
</p>

## Overview
Lila is a free, open source chess engine written in Java.
> A compiled binary can be found [here](https://github.com/StylexTV/Lila/raw/main/bins/lila_3.jar).

This project is a UCI chess engine, which means that it does not contain an interface/gui, but is purely text-based.  
You can either run it from the command prompt via `java -jar lila.jar` or use a chess GUI (e.g. [Cute Chess](https://github.com/cutechess/cutechess)) in order to use it more conveniently.

## Features
Coming soon...

## Options
The [Universal Chess Interface (UCI)](http://wbec-ridderkerk.nl/html/UCIProtocol.html) is a standard protocol used to communicate between chess programs, and is the recommended way to do so for typical graphical user interfaces (GUI) or chess tools.

The following UCI options, which can typically be set via a GUI, are available in Lila:

  * #### Threads
    The number of CPU threads used for searching a position. For best performance, set
    this equal to the number of CPU cores available.

  * #### Hash
    The size of the hash table in MB.

## Commands
Lila supports most of the regular commands included in the [UCI protocol](http://wbec-ridderkerk.nl/html/UCIProtocol.html), but also has some special commands.

Name | Arguments | Description
--- | --- | ---
uci | - | The UCI startup command.
isready | - | Used to synchronize the chess engine with the GUI.
setoption | name [value] | Sets an option to a specific value. For buttons, simply omit the *value* argument.
ucinewgame | - | Tells the engine that a new game has started.
position | [fen &#124; startpos] moves | Sets up a new position.
go | depth<br/>movetime<br/>wtime<br/>btime<br/>movestogo<br/>winc<br/>binc | Starts a new search with the specified constraints.
stop | - | Ends the current search as soon as possible.
d | - | Prints the current board (used for debugging).
perft | [depth] | Executes a [perft](https://www.chessprogramming.org/Perft) call to the specified depth.<br/>⚠️ Warning: Starting an unrestricted call locks the program at the moment.
quit | - | Stops the program and eliminates all searches that are still running.

## Strength
The following table shows the wins, losses, draws and the Elo gain compared to the respective previous version.

Version | Wins | Losses | Draws | Elo gain | Production ready
--- | --- | --- | --- | --- | ---
3.0.1 | 92 | 0 | 8 | +234 | ❌

## Project Layout
Here you can see the current structure of the project.

```bash
├─ 📂 bins/              # ✨ Binaries
├─ 📂 src/               # 🌟 Source Files
│  └─ 📂 de/lila/            # ✉️ Source Code
└─ 📃 CODE_OF_CONDUCT.md # 📌 Code of Conduct
└─ 📃 LICENSE            # ⚖️ MIT License
└─ 📃 README.md          # 📖 Read Me!
```
