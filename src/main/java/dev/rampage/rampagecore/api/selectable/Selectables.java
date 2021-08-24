package dev.rampage.rampagecore.api.selectable;

import dev.rampage.rampagecore.RampageCore;
import dev.rampage.rampagecore.json.JsonUtils;
import dev.rampage.rampagecore.selectables.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class Selectables {
    private final List<Selectable> selectableList;

    public Selectables(RampageCore plugin) {
        selectableList = new ArrayList<>();

        // Почему addAll, а не просто добавил каждый элемент в массив отдельно? Это быстрее.
        selectableList.addAll(Arrays.asList(
                new Archer(plugin),
                new Assassin(plugin),
                new Healer(plugin),
                new Tank(plugin),
                new Warrior(plugin)
        ));

        // Я добавил все и так отсортированным по алфавиту. Нахуя это?
        // А вот хуй вам! Если ты сортируешь даже уже ручками отсортированный массив, всё равно все взаимодействия с массивом происходят быстрей.
        selectableList.sort(Comparator.comparing(Selectable::getName));
    }

    public List<Selectable> getSelectableList() { return selectableList; }

    public boolean isSelectedClass(Player player, String neededClass) {
        String selectedClass = JsonUtils.getPlayerInfoName(player.getName()).getSelectedClass();
        return selectedClass != null && selectedClass.equalsIgnoreCase(selectedClass);
    }
}
