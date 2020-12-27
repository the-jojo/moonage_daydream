![Moonage logo](img/title.png)

`Moonage Daydream` causual space-platformer implemented in Java using the `Processing` lib.

![GIF demo](img/demo.gif)

<p align="center">
  <a href="#Getting Started">Usage</a> •
  <a href="#Tutorial">Tutorial</a> •
  <a href="#Download">Download</a> •
  <a href="#How to Contribute">How to Contribute</a> •
  <a href="#Acknowledgements">Acknowledgements</a>
</p>

**Getting Started**
---

1. Install [`java`](https://www.java.com/en/download/)

2. <a href="#Download">Download</a> the `MoonageDaydream` jar from Releases tab.

3. Run the jar executable to start the game `java -jar MoonageDaydream.jar`


**Tutorial**
---

1. Controls:

    + The player controls the astronaut, which will always face the nearest planet
    + `a/d` > move left or right
    + `w`   > jump; while jumping hold `w` to use the jetpack
    + `s`   > enter/exit the spacecraft when nearby; activate the radar-dish to transport to a new star system

2. Objectives:
    + The player's aim is to spread vegetation and life while defeating monsters and collecting stars. 
    + Explore constellations of planet-systems in leisure.
    + Find all 3 stars per constellation by defeating monsters.
    + Survive by collecting air and evading and defeating monsters.

3. How to Play:
    + You start out with 3 lives and 100 air. Each time you lose a life, you regain 100 air
    + Air depletes while outside the spacecraft and can be collected from grass or recollected after a monster attack.
    + Touching a planet sows grass all around the planet.
    + Jumping on monsters defeats them and they may drop a star, which you can collect.
    + After completing a planet-system, you can travel to the next by using the radar-dish.


## Download

You can [download](https://github.com/the-jojo/moonage_daydream/releases/tag/v1.0) the latest jar along with the report explaining the design and implementation.


**How to Contribute**
---

This game was developed as a deliverable as part of the degree of BSc of Computer Science at the University St Andrews. As such, development has ceased, however, you are welcome to contribute to this project.

1. Clone repo and create a new branch: `$ git checkout https://github.com/the-jojo/moonage_daydream -b name_for_new_branch`.
2. Make changes and test
3. Submit Pull Request with comprehensive description of changes

**Acknowledgements**
---

+ [Java Processing Library](https://processing.org/) for the graphic elements.
+ [University of St Andrews School of Computer Science](https://www.st-andrews.ac.uk/computer-science/) for their awesome BSc Computer Science program.
+ [CS4303 Video Games](https://portal.st-andrews.ac.uk/catalogue/View?code=CS4303&academic_year=2019%2F0) module for teaching the required skills and theory to develop this game.

