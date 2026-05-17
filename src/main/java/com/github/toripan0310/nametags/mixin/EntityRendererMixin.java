package com.github.toripan0310.nametags.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = EntityRenderer.class)
public abstract class EntityRendererMixin<T extends Entity> {

	@Inject(method = "shouldShowName", at = @At("HEAD"), cancellable = true)
	private void alwaysShowName(T entity, CallbackInfoReturnable<Boolean> cir) {
		if (entity instanceof Player) {
			cir.setReturnValue(true);
		}
	}
	@ModifyArg(
			method = "renderNameTag",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/Font;drawInBatch(Lnet/minecraft/network/chat/Component;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/gui/Font$DisplayMode;II)I"
			),
			index = 3
	)
	private int yellowNameTagColor(int originalColor) {
		return 0xFFFFFF00; // 黄色
	}
}