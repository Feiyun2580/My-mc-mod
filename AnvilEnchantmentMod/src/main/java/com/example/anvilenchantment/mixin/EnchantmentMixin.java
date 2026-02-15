package com.example.anvilenchantment.mixin;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Enchantment.class)
public class EnchantmentMixin {

    /**
     * 允许任何附魔应用到任何物品上
     */
    @Inject(method = "isAcceptableItem", at = @At("HEAD"), cancellable = true)
    private void onIsAcceptableItem(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        // 总是返回 true，允许任何附魔应用到任何物品
        cir.setReturnValue(true);
    }

    /**
     * 允许任何附魔互相组合
     */
    @Inject(method = "canCombine", at = @At("HEAD"), cancellable = true)
    private void onCanCombine(Enchantment other, CallbackInfoReturnable<Boolean> cir) {
        // 总是返回 true，允许任何附魔互相组合
        cir.setReturnValue(true);
    }
}
