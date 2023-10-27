How to run the code:
- type 'gradle run' in the terminal

Features implemented:
- Difficulty Level (click on a preferred difficulty level before the game starts):
    - Easy
    - Medium
    - Hard
- Time and Score
- Undo and Cheat (click on one of the top left button buttons corresponding to the 4 different cheat options):
    - Remove Fast Enemies
    - Remove Slow Enemies
    - Remove Fast Projectiles (Enemy projectiles)
    - Remove Slow Projectiles (Enemy projectiles)

Design patterns used:
- Singleton Pattern
    Participants:
    - Singleton (ConfigReader class)

- Observer Pattern:
    Participants:
    - Subject (observer package - Subject interface)
    - Observer (observer package - Observer interface)
    - ConcreteSubject (engine package - GameEngine class)
    - ConcreteObserver (observer package - GamePanel class)

- Memento Pattern:
    Participants:
    - Originator (engine package - GameEngine class)
    - Caretaker (memento package - StateCaretaker class)
    - Memento (memento package - StateMemento class)