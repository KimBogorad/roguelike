package roguelike.model.units;

import java.util.List;

public interface HeroicUnit {
    public boolean CastAbility(List<Enemy> validTargets, Hero player);
}
