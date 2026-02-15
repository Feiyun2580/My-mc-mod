package com.example.anvilenchantment.mixin;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(AnvilScreenHandler.class)
public abstract class AnvilMenuMixin extends ForgingScreenHandler {

    @Shadow
    private int repairItemUsage;

    @Shadow
    private final Property levelCost = Property.create();

    protected AnvilMenuMixin(ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(type, syncId, playerInventory, context);
    }

    @Inject(method = "updateResult", at = @At("HEAD"), cancellable = true)
    private void onUpdateResult(CallbackInfo ci) {
        ItemStack left = this.input.getStack(0);
        ItemStack right = this.input.getStack(1);

        if (left.isEmpty()) {
            this.output.setStack(0, ItemStack.EMPTY);
            this.levelCost.set(0);
            ci.cancel();
            return;
        }

        // 如果右侧为空，不处理
        if (right.isEmpty()) {
            return;
        }

        handleAnvilOperation(left, right, ci);
    }

    @Unique
    private void handleAnvilOperation(ItemStack left, ItemStack right, CallbackInfo ci) {
        Map<Enchantment, Integer> leftEnchants = EnchantmentHelper.get(left);
        Map<Enchantment, Integer> rightEnchants = EnchantmentHelper.get(right);

        // 如果右侧有附魔，处理附魔合并
        if (!rightEnchants.isEmpty()) {
            handleEnchantmentMerge(left, leftEnchants, rightEnchants, ci);
        }
    }

    @Unique
    private void handleEnchantmentMerge(ItemStack target, Map<Enchantment, Integer> leftEnchants,
                                        Map<Enchantment, Integer> rightEnchants, CallbackInfo ci) {
        ItemStack result = target.copy();
        int totalCost = 0;
        boolean anyEnchantmentApplied = false;

        // 合并附魔
        for (Map.Entry<Enchantment, Integer> entry : rightEnchants.entrySet()) {
            Enchantment enchantment = entry.getKey();
            int rightLevel = entry.getValue();
            int leftLevel = leftEnchants.getOrDefault(enchantment, 0);

            // 计算新等级：相同等级+1，不同等级取最高
            int newLevel;
            if (leftLevel == rightLevel && leftLevel > 0) {
                newLevel = Math.min(leftLevel + 1, enchantment.getMaxLevel());
            } else {
                newLevel = Math.max(leftLevel, rightLevel);
            }

            // 应用附魔
            leftEnchants.put(enchantment, newLevel);
            totalCost += newLevel;
            anyEnchantmentApplied = true;
        }

        if (anyEnchantmentApplied) {
            // 应用附魔到结果物品
            EnchantmentHelper.set(leftEnchants, result);

            // 设置输出
            this.output.setStack(0, result);
            this.repairItemUsage = 1;
            this.levelCost.set(Math.max(1, totalCost));

            ci.cancel();
        }
    }
}
