package roguelike;

import roguelike.model.units.Hero;
import roguelike.model.units.Hunter;
import roguelike.model.units.Mage;
import roguelike.model.units.Rogue;
import roguelike.model.units.Warrior;

public class HeroFactory {
    
    public Hero createPlayer(int choice) {
        return switch (choice) {
            case 1 -> new Warrior("Jon Snow", 300, 30, 4, 3);
            case 2 -> new Warrior("The Hound", 400, 20, 6, 5);
            case 3 -> new Mage("Melisandre", 100, 5, 1, 300, 30, 15, 5, 6);
            case 4 -> new Mage("Thoros of Myr", 250, 25, 4, 150, 20, 20, 3, 4);
            case 5 -> new Rogue("Arya Stark", 150, 40, 2, 20);
            case 6 -> new Rogue("Bronn", 250, 35, 3, 50);
            case 7 -> new Hunter("Ygritte", 220, 30, 2, 6);
            default -> new Warrior("Jon Snow", 300, 30, 4, 3);
        };
    }
}
