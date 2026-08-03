package com.mathmod.program;

import com.mathmod.MathMod;
import com.mathmod.registry.ModDataComponents;
import com.mathmod.registry.ModItems;
import com.mathmod.screen.RuneProgrammerMenu;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import java.util.List;

/** Runtime proof that ordinary Laboratory mutations stay bound to the captured component target. */
@GameTestHolder(MathMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class P12DsProgrammerGameTests {
    private P12DsProgrammerGameTests() {
    }

    @GameTest(template = "empty")
    public static void ordinaryMutationsRejectComponentDistinctReplacement(GameTestHelper helper) {
        ServerPlayer player = helper.makeMockServerPlayerInLevel();
        ItemStack captured = programmedTalisman("captured", "minecraft:amethyst_shard", 3);
        player.setItemInHand(InteractionHand.MAIN_HAND, captured);
        RuneProgrammerMenu menu = new RuneProgrammerMenu(1, player.getInventory(), InteractionHand.MAIN_HAND);
        player.containerMenu = menu;

        ItemStack replacement = programmedTalisman("replacement", "minecraft:diamond", 5);
        ItemStack capturedBefore = captured.copy();
        ItemStack replacementBefore = replacement.copy();
        player.setItemInHand(InteractionHand.MAIN_HAND, replacement);

        helper.assertTrue(!ItemStack.isSameItemSameComponents(captured, replacement),
                "the replacement must be the same item but component-distinct from the captured target");
        helper.assertTrue(!menu.setCustomSpellName(player, "must not apply"),
                "component-distinct replacement must reject custom-name mutation");
        helper.assertTrue(!menu.clickMenuButton(player, RuneProgrammerMenu.SAVE_HOP_BUTTON),
                "component-distinct replacement must reject preset inscription");
        helper.assertTrue(!menu.clickMenuButton(player, RuneProgrammerMenu.CLEAR_BUTTON),
                "component-distinct replacement must reject clear");
        helper.assertTrue(!menu.clickMenuButton(player, RuneProgrammerMenu.SAVE_CUSTOM_BUTTON),
                "component-distinct replacement must reject custom save");
        helper.assertTrue(!menu.clickMenuButton(player, RuneProgrammerMenu.RESET_CUSTOM_BUTTON),
                "component-distinct replacement must reject custom reset");
        helper.assertTrue(!menu.clickMenuButton(player, RuneProgrammerMenu.UNDO_CUSTOM_BUTTON),
                "component-distinct replacement must reject custom undo");
        helper.assertTrue(!menu.clickMenuButton(player,
                        RuneProgrammerMenu.CUSTOM_ACTION_BUTTON_BASE + CustomSpellAction.SELF.ordinal()),
                "component-distinct replacement must reject custom action mutation");
        helper.assertTrue(!menu.applyCustomInvocation(player, CustomSpellInvocation.defaults(CustomSpellAction.SELF)),
                "component-distinct replacement must reject parameterized custom-action mutation");

        helper.assertTrue(ItemStack.isSameItemSameComponents(capturedBefore, captured),
                "the original captured talisman and its resources must remain byte-for-byte component-equivalent");
        helper.assertTrue(ItemStack.isSameItemSameComponents(replacementBefore, player.getItemInHand(InteractionHand.MAIN_HAND)),
                "the replacement talisman and its resources must remain byte-for-byte component-equivalent");
        helper.assertTrue(player.connection != null && player.containerMenu == menu,
                "the stale mutation attempts must leave the player connected in the original menu");
        helper.succeed();
    }

    private static ItemStack programmedTalisman(String name, String resourceId, int quantity) {
        ItemStack talisman = new ItemStack(ModItems.PROGRAMMED_TALISMAN.get());
        talisman.set(ModDataComponents.PROGRAM_NAME.get(), name);
        talisman.set(ModDataComponents.PROGRAM_RESOURCES.get(), List.of(new ResourceSelection(resourceId, quantity)));
        return talisman;
    }
}
